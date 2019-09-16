package ar.edu.itba.pod;
import ar.edu.itba.pod.model.Party;
import ar.edu.itba.pod.model.Vote;

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface FiscalService extends Remote {
    long registerFiscal(Long table, Party party, ClientInterface callback) throws RemoteException;

    /* No lo pide el enunciado */
//    boolean unregisterFiscal(Long table, ClientInterface callback) throws RemoteException;

    void notifyFiscal(Vote vote) throws RemoteException;
}
