import java.rmi.Remote;
import java.rmi.RemoteException;


public interface AdministrationtService extends Remote {

    boolean startElections() throws RemoteException;

    String getElectionsState() throws RemoteException;

    void endElections() throws RemoteException;

}
