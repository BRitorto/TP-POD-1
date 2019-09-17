package ar.edu.itba.pod.model;


import java.io.Serializable;

public class PartyResults implements Serializable, Comparable<PartyResults> {
    private final Party party;
    private double percentage;

    public PartyResults(final Party party, double percentage) {
        this.party = party;
        this.percentage = percentage;
    }

    public Party getParty() {
        return party;
    }

    public Double getPercentage() {
        return percentage;
    }

    @Override
    public int compareTo(PartyResults o) {
        if(getParty().name() == null || o.getParty().name() == null){
            return 0;
        }
        return this.getParty().name().compareTo(o.getParty().name());
    }

    @Override
    public String toString() {
        return "PartyResults{" +
                "party=" + party +
                ", percentage=" + percentage +
                '}';
    }
}
