package ar.edu.itba.pod;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface AdministrationService extends Remote {

    boolean startElections() throws RemoteException;

    String getElectionsState() throws RemoteException;

    void endElections() throws RemoteException;

}
