package ar.edu.itba.pod.client;

import ar.edu.itba.pod.AdministrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Objects;


public class AdministrationClient extends Client<AdministrationService> {

    /*La información de cuál es la acción a realizar se recibe a través de argumentos de línea de
    comando al llamar a la clase del cliente de administración y el resultado se debe imprimir en
    pantalla.*/

    private static Logger logger = LoggerFactory.getLogger(Client.class);

    public AdministrationClient(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        super("administration");
        this.addOption("action", "Name of the action to follow", true, true);
        this.parse(args);
        this.lookup();
    }


    public boolean startElections() throws RemoteException {
        this.remoteService.getElectionsState();
        System.out.println(this.remoteService.getElectionsState());
        return this.remoteService.startElections();
    }


    public String getElectionsState() throws RemoteException {
        System.out.println(this.remoteService.getElectionsState());
        return this.remoteService.getElectionsState();
    }


    public boolean endElections() throws RemoteException {
        System.out.println(this.remoteService.getElectionsState());
        return this.remoteService.endElections();
    }

    private static enum actionType {
        /* abre los comicios */
        OPEN,
        /* consulta el estado de los comicios*/
        STATE,
        /* cierra los comicios*/
        CLOSE;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        logger.info("Starting administration client");

        final AdministrationClient administrationClient = new AdministrationClient(args);

        String actionName = administrationClient.getParameter("action").orElseThrow(() -> new IllegalArgumentException("No action name specified"));

        if(actionName.equalsIgnoreCase("open")){
            administrationClient.startElections();
        }else if(actionName.equalsIgnoreCase("state")){
            administrationClient.getElectionsState();
        }else if(actionName.equalsIgnoreCase("close")){
            administrationClient.endElections();
        }else {
            throw new IllegalArgumentException("Action name is invalid");
        }

    }


}
