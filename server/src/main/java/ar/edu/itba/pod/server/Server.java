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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server implements ManagementService, FiscalService, QueryService, VoteService {
    private static Logger logger = LoggerFactory.getLogger(Server.class);

    private ElectionStatus electionStatus = ElectionStatus.FINISHED;
    private Map<Long, List<Vote>> allVotes = new ConcurrentHashMap<>();
    // table, fiscal
    private Map<Long, List<ClientInterface>> fiscals = new ConcurrentHashMap<>();
    private Map<Party, Long> totalVotesByParty = new ConcurrentHashMap<>();

    private long fiscal_counter = 0;

    @Override
    public synchronized boolean startElections() throws RemoteException {
        if (electionStatus.equals(ElectionStatus.FINISHED)) {
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
        return false; // arrojar un error consigna
    }

    @Override
    public long registerFiscal(Long table, Party party,  ClientInterface callback) throws RemoteException {
        /* IF ELECTIONS HAVE ALL READY STARTED, YOU CAN'T REGISTER ANYONE */
        if(electionStatus != ElectionStatus.FINISHED){
            /* tirar error */
            return -1;
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
        switch(this.electionStatus) {
            case FINISHED:
                throw new ElectionsNotStartedException("Elections haven't started yet!");

            case OPEN:
                long[] partyVotesCounter = new long[Party.values().length];
                long totalVotes;


                // resultdaos parciales
                return null;

            case CLOSED:
                // resultados finales
                return null;
            default:
                throw new RuntimeException("Invalid election state.");
        }
    }

    @Override
    public Collection<PartyResults> queryByProvince(Province province) throws RemoteException {
        switch(this.electionStatus) {
            case FINISHED:
                throw new ElectionsNotStartedException("Elections haven't started yet!");

            case OPEN:
                // resultdaos parciales
                return null;

            case CLOSED:
                // resultados finales
                return null;
            default:
                throw new RuntimeException("Invalid election state.");
        }
    }

    @Override
    public Collection<PartyResults> queryByCountry() throws RemoteException {
        switch(this.electionStatus) {
            case FINISHED:
                throw new ElectionsNotStartedException("Elections haven't started yet!");

            case OPEN:
                // resultdaos parciales
                return null;

            case CLOSED:
                // resultados finales
                return null;
            default:
                throw new RuntimeException("Invalid election state.");
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
