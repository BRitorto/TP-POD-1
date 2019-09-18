package ar.edu.itba.pod;

import ar.edu.itba.pod.model.ElectionStatus;
import ar.edu.itba.pod.model.PartyResults;
import ar.edu.itba.pod.model.Province;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;


public interface QueryService extends Remote {

    Collection<PartyResults> queryByTable(final long table) throws RemoteException;

    Collection<PartyResults> queryByProvince(final Province province) throws RemoteException;

    Collection<PartyResults> queryByCountry() throws RemoteException;

    ElectionStatus electionStatus() throws RemoteException;
}
