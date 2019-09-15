package ar.edu.itba.pod;
import ar.edu.itba.pod.model.Party;
import ar.edu.itba.pod.model.Vote;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface InspectorService extends Remote {
    long registerInspector(Long table, Party party, ClientCallbackInterface callback) throws RemoteException;

    boolean unregisterInspector(Long table, ClientCallbackInterface callback) throws RemoteException;

    void notifyInspectors(Vote vote) throws RemoteException;
}
