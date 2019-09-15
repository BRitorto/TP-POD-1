package ar.edu.itba.pod.model;

public enum ElectionStatus {
    OPEN("Elections have started"),
    CLOSED("Elections already ended"),
    FINISHED("Elections have not started yet");

    private String statusDescription;

    ElectionStatus(final String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getStatusDescription() {return this.statusDescription;}
}
