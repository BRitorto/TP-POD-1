package ar.edu.itba.pod.client;

import ar.edu.itba.pod.ClientInterface;
import ar.edu.itba.pod.InspectorService;
import ar.edu.itba.pod.model.Party;
import ar.edu.itba.pod.model.Vote;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Objects;

/**
 * Created by estebankramer on 15/09/2019.
 */

public class InspectorClient extends Client<InspectorService> implements ClientInterface {
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
        InspectorClient InspectorClient = new InspectorClient(args);
        String pollingPlaceId = InspectorClient.getParameter("id").orElseThrow(IllegalArgumentException::new);
        String queryPartyName = InspectorClient.getParameter("party").orElseThrow(IllegalArgumentException::new);
        Party party = Party.valueOf(queryPartyName);
        InspectorClient.setParty(party);
        InspectorClient.register(Long.valueOf(pollingPlaceId), party, InspectorClient);
    }

    public void register(Long pollingPlaceId, Party party, ClientInterface callback) throws RemoteException {
        Objects.requireNonNull(this.remoteService);
        System.out.println(this.remoteService.registerInspector(pollingPlaceId, party, callback));
    }

    public Long getId() {
        return id;
    }

    @Override
    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    @Override
    public void ping() throws RemoteException {
        System.out.println("this is a ping");
    }

    @Override
    public void notifyChanges(Party party, Long table) throws RemoteException {
        System.out.println("New vote for " + party.name() + " on polling place " + table);
    }

    @Override
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
