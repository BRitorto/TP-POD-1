package ar.edu.itba.pod.exceptions;

public class ElectionsNotStartedException extends RuntimeException {
    public ElectionsNotStartedException() {
        super();
    }
    public ElectionsNotStartedException(String message) {
        super(message);
    }
}
