package ar.edu.itba.pod.management;

import ar.edu.itba.pod.ManagementService;
import ar.edu.itba.pod.client.Client;
import ar.edu.itba.pod.exceptions.ElectionsEndedException;
import ar.edu.itba.pod.exceptions.ElectionsNotStartedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ManagementClient extends Client<ManagementService> {

    private static Logger logger = LoggerFactory.getLogger(Client.class);

    public ManagementClient(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        super("management");
        this.addOption("actionName", "Name of the action to follow", true, true);
        this.parse(args);
        this.lookup();
    }

    public boolean startElections() throws RemoteException, ElectionsEndedException {
        boolean open = this.remoteService.startElections();
        if(!open){
            System.out.println("Elections have already ended, you can't restart them");
            throw new ElectionsEndedException("Elections have already ended");
        }
        System.out.println(this.remoteService.getElectionsState());
        return open;
    }


    public String getElectionsState() throws RemoteException {
        System.out.println(this.remoteService.getElectionsState());
        return this.remoteService.getElectionsState();
    }

    public boolean endElections() throws RemoteException, ElectionsNotStartedException {
        boolean close = this.remoteService.endElections();
        if(!close){
            System.out.println("Elections have not started yet, you can't end them before they start");
            throw new ElectionsNotStartedException("Elections have not started yet");
        }
        System.out.println(this.remoteService.getElectionsState());
        return close;
    }

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        logger.info("Starting management client");
        final ManagementClient managementClient = new ManagementClient(args);
        String actionName = managementClient.getParameter("action").orElseThrow(() -> new IllegalArgumentException("No action name specified"));

        if(actionName.equalsIgnoreCase("open")){
            managementClient.startElections();
        }else if(actionName.equalsIgnoreCase("state")){
            managementClient.getElectionsState();
        }else if(actionName.equalsIgnoreCase("close")){
            managementClient.endElections();
        }else {
            throw new IllegalArgumentException("Action name is invalid");
        }
    }
}
