package ar.edu.itba.pod.exceptions;

public class ElectionsStartedException extends RuntimeException{
    public ElectionsStartedException() {
        super();
    }
    public ElectionsStartedException(String message) {
        super(message);
    }

}
