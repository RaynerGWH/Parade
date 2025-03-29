package exceptions;

public class CorruptFileException extends Exception {
    public CorruptFileException() {
        super();
    }
    
    public CorruptFileException(String message) {
        super(message);
    }
}
