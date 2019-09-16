package ar.edu.itba.pod.client;

import ar.edu.itba.pod.ClientInterface;
import ar.edu.itba.pod.InspectorService;
import ar.edu.itba.pod.model.Party;
import ar.edu.itba.pod.model.Vote;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Objects;


public class InspectorClient extends Client<InspectorService> implements ClientInterface{
    private Long id;
    private Party party;

    public InspectorClient(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        super("audit");
        this.addOption("id", "Polling place number", true, true);
        this.addOption("party", "Party name", true,true);
        this.parse(args);
        this.lookup();
    }


    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        InspectorClient inspectorClient = new InspectorClient(args);
        String pollingPlaceId = inspectorClient.getParameter("id").orElseThrow(IllegalArgumentException::new);
        String queryPartyName = inspectorClient.getParameter("party").orElseThrow(IllegalArgumentException::new);
        Party party = Party.valueOf(queryPartyName);
        /* es necesario que se lo setiemos en el client, no puede ir al server? */
//        inspectorClient.setParty(party);
        inspectorClient.register(Long.valueOf(pollingPlaceId), party, inspectorClient);
    }

    public void register(Long pollingPlaceId, Party party, ClientInterface callback) throws RemoteException {
        Objects.requireNonNull(this.remoteService);
        System.out.println("number of values in the map " + this.remoteService.registerInspector(pollingPlaceId, party, callback));
        System.out.println("Fiscal of " + callback.getParty() + " registered on polling place " + pollingPlaceId);
    }

    public Long getId() {
        return id;
    }


    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }


    public void ping() throws RemoteException {
        System.out.println("this is a ping");
    }


    public void notifyChanges(Party party, Long table) throws RemoteException {
        System.out.println("New vote for " + party.name() + " on polling place " + table);
    }


    public void setId(long id) throws RemoteException {
        this.id = id;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || this.getClass() != other.getClass()) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
