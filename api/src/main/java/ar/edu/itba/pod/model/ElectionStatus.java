package ar.edu.itba.pod.model;

public enum ElectionStatus {
    STARTED("Elections have started"),
    ENDED("Elections already ended"),
    NOTSTARTEDYET("Elections have not started yet");

    private String statusDescription;

    ElectionStatus(final String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getStatusDescription() {return this.statusDescription;}
}
