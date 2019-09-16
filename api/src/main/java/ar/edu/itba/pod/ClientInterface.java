package ar.edu.itba.pod;
import ar.edu.itba.pod.model.Party;
import ar.edu.itba.pod.model.Vote;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote, Serializable {

    void ping() throws RemoteException;

    void notifyChanges(Party party, Long totalVotes) throws RemoteException;

    void setId(long id) throws RemoteException;

    Party getParty() throws RemoteException;
}
