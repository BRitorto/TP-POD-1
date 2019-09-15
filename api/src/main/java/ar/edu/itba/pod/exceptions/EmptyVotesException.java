package ar.edu.itba.pod.exceptions;

public class EmptyVotesException extends RuntimeException {
    public EmptyVotesException(String s) {
        super(s);
    }
}
