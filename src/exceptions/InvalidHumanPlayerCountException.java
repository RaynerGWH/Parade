package exceptions;

public class InvalidHumanPlayerCountException extends RuntimeException {
    public InvalidHumanPlayerCountException(String message) {
        super(message);
    }
}
