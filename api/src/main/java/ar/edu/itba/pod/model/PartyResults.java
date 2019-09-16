package ar.edu.itba.pod.model;

import java.io.Serializable;

public class PartyResults implements Serializable {
    private final Party party;
    private double percentage;

    public PartyResults(final Party party, double percentage) {
        this.party = party;
        this.percentage = percentage;
    }

    public Party getParty() {
        return party;
    }

    public double getPercentage() {
        return percentage;
    }
}
