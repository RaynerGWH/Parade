package exceptions;

public class NoAvailableNPCNamesException extends RuntimeException{
    public NoAvailableNPCNamesException(String message) {
        super(message);
    }
}
