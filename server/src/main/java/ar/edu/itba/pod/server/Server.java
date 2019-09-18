package ar.edu.itba.pod.server;

import ar.edu.itba.pod.*;
import ar.edu.itba.pod.exceptions.ElectionsNotStartedException;
import ar.edu.itba.pod.exceptions.EmptyVotesException;
import ar.edu.itba.pod.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static ar.edu.itba.pod.model.ElectionStatus.FINISHED;
import static ar.edu.itba.pod.model.ElectionStatus.OPEN;

//import com.sun.java.swing.plaf.windows.TMSchema;

public class Server implements ManagementService, FiscalService, QueryService, VoteService {
    private static Logger logger = LoggerFactory.getLogger(Server.class);

    private ElectionStatus electionStatus = FINISHED;
    // table, votos (TODO: eliminar static, lo pongo para probar el conteo de votos en el servidor)
    private static Map<Long, List<Vote>> allVotes = new ConcurrentHashMap<>();
    private static Map<Long, List<Vote>> allVotesAux = new ConcurrentHashMap<>();

    // table, fiscal
    private Map<Long, List<ClientInterface>> fiscals = new ConcurrentHashMap<>();
    private Map<Party, Long> totalVotesByParty = new ConcurrentHashMap<>();

    private long fiscal_counter = 0;

    @Override
    public synchronized boolean startElections() throws RemoteException {
        if (electionStatus.equals(FINISHED)) {
            this.electionStatus = ElectionStatus.OPEN;
            return true;
        }
        return false;
    }

    @Override
    public String getElectionsState() throws RemoteException {
        return this.electionStatus.getStatusDescription();
    }

    @Override
    public synchronized boolean endElections() throws RemoteException {
        if (electionStatus.equals(ElectionStatus.OPEN) || electionStatus.equals(ElectionStatus.CLOSED)) {
            this.electionStatus = ElectionStatus.CLOSED;
            return true;
        }
        return false;
    }

    @Override
    public long registerFiscal(Long table, Party party,  ClientInterface callback) throws RemoteException {
        /* IF ELECTIONS HAVE ALL READY STARTED, YOU CAN'T REGISTER ANYONE */
        if(electionStatus != FINISHED){
            throw new IllegalStateException("All registrations must be done before voting begins");
        }
        long fiscal = fiscal_counter;
        callback.setId(fiscal);
        callback.setParty(party);
        if(this.fiscals.containsKey(table)){
            this.fiscals.get(table).add(callback);
        }else{
            List<ClientInterface> newList = new ArrayList<>();
            this.fiscals.put(table, newList);
        }
        fiscal_counter++;
        return this.fiscals.size();
    }


//    @Override
//              /* No esta dentro de lo pedido */
//    public boolean unregisterFiscal(Long table, ClientInterface callback) throws RemoteException {
//        if(this.fiscals.containsKey(table)){
//            if(this.fiscals.get(table).contains(callback)){
//                this.fiscals.get(table).remove(callback);
//                return true;
//            }
//            return false;
//        }
//        return  false;
//    }


    public void notifyFiscal(Vote vote) throws RemoteException {
        fiscals.entrySet().stream().forEach(fiscalList ->
                fiscalList.getValue().forEach(fiscal -> {
                    vote.getChoices().forEach(choice -> {
                        /* actualizo la cantidad total de votos por partido */
//                        if(!totalVotesByParty.keySet().contains(choice)) {
//                            totalVotesByParty.put(choice, (long) 1);
//                        } else {
//                            totalVotesByParty.put(choice, totalVotesByParty.get(choice) + 1);
//                        }
                        try {
                            if(fiscal.getParty().equals(choice)) {
                                fiscal.notifyChanges(choice, vote.getTable());
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    });
                })
        );

    }

    @Override
    public Collection<PartyResults> queryByTable(long table) throws RemoteException {

        if(this.electionStatus == ElectionStatus.FINISHED || (this.electionStatus!=ElectionStatus.OPEN &&
                this.electionStatus != ElectionStatus.CLOSED)){
            return null;
        }

        Long[] partyVotesCounter = new Long[Party.values().length];
        Arrays.fill(partyVotesCounter,new Long(0));
        long totalVotes;

        List <Vote> votes = new ArrayList<>(this.allVotes.get(table));

        if(votes.size() == 0){
            /* manejarlo */
            totalVotes = 0;
        }else{
            votes.forEach(p -> partyVotesCounter[p.getChoices().get(0).ordinal()]++);
            totalVotes = votes.size();
        }

        Collection<PartyResults> parcial =
                Arrays.stream(Party.values()).
                        map(p -> new PartyResults(p, partyVotesCounter[p.ordinal()]*100.0/(double) totalVotes)).
                        collect(Collectors.toList());
        return parcial;
    }

    public Collection<PartyResults> queryByProvince(Province province) throws RemoteException {

        if(this.electionStatus == ElectionStatus.FINISHED){
            return null;
        }

        switch(this.electionStatus) {
            case FINISHED:
                return null;
            //throw new ElectionsNotStartedException("Elections haven't started yet!");

            case OPEN:
                /* resultados parciales*/

                long[] partyVotesCounter = new long[Party.values().length];
                long totalVotes;

                Map<Long, List<Vote>> votes = new HashMap<>(this.allVotes);

                if(votes.size() == 0){
                    /* manejarlo */
                    totalVotes = 0;
                }else{

                    for(Long l : votes.keySet()){
                        List<Vote> v1 = votes.get(l);
                        v1.forEach(p -> { if (p.getProvince().equals(province)) partyVotesCounter[p.getChoices().get(0).ordinal()]++;});
                    }

                    totalVotes = Arrays.stream(partyVotesCounter).sum();

                    Collection<PartyResults> parcial =
                            Arrays.stream(Party.values()).
                                    map(p -> new PartyResults(p, partyVotesCounter[p.ordinal()]*100.0/(double) totalVotes)).
                                    collect(Collectors.toList());

                    return parcial;
                }

            case CLOSED:
                // resultados finales
                return null;
            default:
                throw new RuntimeException("Invalid election state.");
        }
    }

    @Override
    public Collection<PartyResults> queryByCountry() throws RemoteException {

        if (this.electionStatus.equals(FINISHED)) {
            throw new ElectionsNotStartedException("Elections haven't started yet!");
        }

        long[] votesByParty = new long[13];
        for (Long table : allVotes.keySet()) {
            List<Vote> votes = allVotes.get(table);
            votes.forEach(vote -> votesByParty[vote.getChoices().get(0).ordinal()]++);
        }

        long sumVotes = LongStream.of(votesByParty).sum();
        //if elections open
//        if (this.electionStatus.equals(OPEN)) {
//            return getResponse(votesByParty, sumVotes);
//        }
        // if elections closed
        allVotesAux = allVotes;
        boolean condition = true;
        long maxVotes = 0;
        long minVotes = sumVotes;
        int minParty = 0;
        do {
            sumVotes = LongStream.of(votesByParty).sum();
            for (int i = 0; i < votesByParty.length; i++) {
                if (votesByParty[i] > maxVotes) {
                    maxVotes = votesByParty[i];
                }
                if (votesByParty[i] < minVotes) {
                    minVotes = votesByParty[i];
                    minParty = i;
                }
            }
            //System.out.println((double) maxVotes/(double) sumVotes);
            if ((double) maxVotes/(double) sumVotes >= 0.5){
                condition = false;
            } else {
                votesByParty[minParty] = 0;
                transferVotes(votesByParty, minParty);
            }
        } while (condition);
        return getResponse(votesByParty, sumVotes);
    }

    @Override
    public ElectionStatus electionStatus() throws RemoteException {
        return this.electionStatus;
    }

    private Collection<PartyResults> getResponse(long[] votesByParty, double sumVotes) {
        List<PartyResults> response = new ArrayList<>();
        for (int i = 0; i < votesByParty.length; i++) {
            response.add(new PartyResults(Party.values()[i], votesByParty[i]*100/ sumVotes));
        }
        return response;
    }

    private void transferVotes(long[] votesByParty, final int party) {
        for (Long table : allVotesAux.keySet()) {
            List<Vote> votes = allVotesAux.get(table);
            votes.forEach(vote -> {
                List<Party> choices = vote.getChoices();
                for (int i = 0; i < choices.size(); i++) {
                    if (choices.get(i).ordinal() == party) {
                        choices.remove(i);
                        if (!(choices.size() == 1 || i == 2)) {
                            votesByParty[choices.get(0).ordinal()]++;
                            // decidimos que se pierde el voto si era la tercera opcion o si era la unica
                            // bajar el numero de votes??
                        }
                        break;
                    }
                }
            });
        }
    }

    @Override
    public int ballot(Collection<Vote> votes) throws RemoteException, ElectionsNotStartedException, EmptyVotesException {
        if (!this.electionStatus.equals(ElectionStatus.OPEN)) {
            return -1;
//            throw new ElectionsNotStartedException("Elections haven't started yet!");
        }

        if (votes.size() == 0) {
            return -2;
//            throw new EmptyVotesException("Please enter at least one vote");
        }

        votes.forEach(vote -> {
            /* el try catch?... */
            try {
                notifyFiscal(vote);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            this.allVotes.computeIfAbsent(vote.getTable(), key -> new ArrayList<>()).add(vote);
        });
        return 0;
    }

    public static void main(String[] args) throws RemoteException {

        logger.info("Vote System Server Starting.");

        final Server servant = new Server();
        final Remote remote = UnicastRemoteObject.exportObject(servant, 0);
        final Registry registry = LocateRegistry.getRegistry();

        logger.info("Rebinding Management Service");
        registry.rebind("management", remote);
        logger.info("Rebinding Vote Service");
        registry.rebind("vote", remote);
        logger.info("Rebinding Query Service");
        registry.rebind("query", remote);
        logger.info("Rebinding Fiscal Service");
        registry.rebind("fiscal", remote);

    }

}
