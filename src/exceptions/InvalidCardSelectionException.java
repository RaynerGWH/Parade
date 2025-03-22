package exceptions;

public class InvalidCardSelectionException extends RuntimeException {
    public InvalidCardSelectionException(String message) {
        super(message);
    }
}
