package ar.edu.itba.pod.model;

public class PartyResults {
    private final Party party;
    private double percentage;

    public PartyResults(final Party party, double percentage) {
        this.party = party;
        this.percentage = percentage;
    }
}
