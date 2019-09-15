package ar.edu.itba.pod.server;

import ar.edu.itba.pod.*;
import ar.edu.itba.pod.exceptions.ElectionsNotStartedException;
import ar.edu.itba.pod.exceptions.EmptyVotesException;
import ar.edu.itba.pod.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;

public class Server implements AdministrationService, InspectorService, QueryService, VotingService {
    private static Logger logger = LoggerFactory.getLogger(Server.class);

    private ElectionStatus electionStatus = ElectionStatus.ENDED;

    @Override
    public boolean startElections() throws RemoteException {
        if (electionStatus.equals(ElectionStatus.NOTSTARTEDYET)) {
            this.electionStatus = ElectionStatus.STARTED;
            return true;
        }
        return false; // arrojar un error consigna
    }

    @Override
    public String getElectionsState() throws RemoteException {
        return this.electionStatus.getStatusDescription();
    }

    @Override
    public boolean endElections() throws RemoteException {
        if (electionStatus.equals(ElectionStatus.STARTED)) {
            this.electionStatus = ElectionStatus.ENDED;
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

    }

    @Override
    public Collection<PartyResults> queryByTable(long table) throws RemoteException {
        switch(this.electionStatus) {
            case NOTSTARTEDYET:
                throw new ElectionsNotStartedException("Elections haven't started yet!");

            case STARTED:
                // resultdaos parciales
                return null;

            case ENDED:
                // resultados finales
                return null;
            default:
                throw new RuntimeException("Invalid election state.");
        }
    }

    @Override
    public Collection<PartyResults> queryByProvince(Province province) throws RemoteException {
        switch(this.electionStatus) {
            case NOTSTARTEDYET:
                throw new ElectionsNotStartedException("Elections haven't started yet!");

            case STARTED:
                // resultdaos parciales
                return null;

            case ENDED:
                // resultados finales
                return null;
            default:
                throw new RuntimeException("Invalid election state.");
        }
    }

    @Override
    public Collection<PartyResults> queryByCountry() throws RemoteException {
        switch(this.electionStatus) {
            case NOTSTARTEDYET:
                throw new ElectionsNotStartedException("Elections haven't started yet!");

            case STARTED:
                // resultdaos parciales
                return null;

            case ENDED:
                // resultados finales
                return null;
            default:
                throw new RuntimeException("Invalid election state.");
        }
    }

    @Override
    public void ballot(Collection<Vote> votes) throws RemoteException, ElectionsNotStartedException {
        if (!this.electionStatus.equals(ElectionStatus.STARTED)) {
            throw new ElectionsNotStartedException("Elections haven't started yet!");
        }

        if (votes.size() == 0) {
            throw new EmptyVotesException("Please enter at least one vote");
        }

        // guardar los votos
    }


}
