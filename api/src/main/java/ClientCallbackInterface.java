import ar.edu.itba.pod.model.Vote;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallbackInterface extends Remote, Serializable {

    void ping() throws RemoteException;

    void notifyChanges(Vote vote) throws RemoteException;

    void setId(long id) throws RemoteException;
}
