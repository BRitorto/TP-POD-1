package ar.edu.itba.pod;

import ar.edu.itba.pod.exceptions.ElectionsNotStartedException;
import ar.edu.itba.pod.model.Vote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;


public interface VoteService extends Remote {
    int ballot(Collection<Vote> votes) throws RemoteException, ElectionsNotStartedException;
}
