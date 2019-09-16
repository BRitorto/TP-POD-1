package ar.edu.itba.pod;

import ar.edu.itba.pod.model.Party;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ManagementService extends Remote {

    boolean startElections() throws RemoteException;

    String getElectionsState() throws RemoteException;

    boolean endElections() throws RemoteException;

}
