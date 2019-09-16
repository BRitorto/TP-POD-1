package ar.edu.itba.pod.exceptions;

public class ElectionsEndedException extends RuntimeException{
    public ElectionsEndedException() {
        super();
    }
    public ElectionsEndedException(String message) {
        super(message);
    }
}
