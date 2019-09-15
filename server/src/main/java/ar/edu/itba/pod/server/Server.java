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

public class Server implements AdministrationService, InspectorService, QueryService, VotingService {
    private static Logger logger = LoggerFactory.getLogger(Server.class);

    private ElectionStatus electionStatus = ElectionStatus.CLOSED;
    private Map<Long, List<Vote>> allVotes = new ConcurrentHashMap<>();
    private Map<Long, List<ClientInterface>> inspectors = new ConcurrentHashMap<>();

    @Override
    public synchronized boolean startElections() throws RemoteException {
        if (electionStatus.equals(ElectionStatus.FINISHED)) {
            this.electionStatus = ElectionStatus.OPEN;
            return true;
        }
        return false; // arrojar un error consigna
    }

    @Override
    public String getElectionsState() throws RemoteException {
        return this.electionStatus.getStatusDescription();
    }

    @Override
    public synchronized boolean endElections() throws RemoteException {
        if (electionStatus.equals(ElectionStatus.OPEN)) {
            this.electionStatus = ElectionStatus.CLOSED;
            return true;
        }
        return false; // arrojar un error consigna
    }

    @Override
    public long registerInspector(Long table, Party party, ClientInterface callback) throws RemoteException {
        return 0;
    }

    @Override
    public boolean unregisterInspector(Long table, ClientInterface callback) throws RemoteException {
        return false;
    }

    @Override
    public void notifyInspectors(Vote vote) throws RemoteException {
        return;
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
    public void ballot(Collection<Vote> votes) throws RemoteException, ElectionsNotStartedException, EmptyVotesException {
        if (!this.electionStatus.equals(ElectionStatus.OPEN)) {
            throw new ElectionsNotStartedException("Elections haven't started yet!");
        }

        if (votes.size() == 0) {
            throw new EmptyVotesException("Please enter at least one vote");
        }

        votes.forEach(vote -> {
            //notifyInspectors(vote);
            this.allVotes.computeIfAbsent(vote.getTable(), key -> new ArrayList<>()).add(vote);
        });
    }

    public static void main(String[] args) throws RemoteException {
        logger.info("Voting System Server Starting.");

        final Server servant = new Server();
        final Remote remote = UnicastRemoteObject.exportObject(servant, 8081);
        final Registry registry = LocateRegistry.getRegistry();

        logger.info("Rebinding Administration Service");
        registry.rebind("administration", remote);
        logger.info("Rebinding Voting Service");
        registry.rebind("voting", remote);
        logger.info("Rebinding Query Service");
        registry.rebind("query", remote);
        logger.info("Rebinding Inspector Service");
        registry.rebind("inspector", remote);
    }

}
