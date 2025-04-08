package exceptions;

/**
 * Exception thrown when a file's content is corrupted or not in the expected format.
 */
public class CorruptFileException extends Exception {

    /**
     * Constructs a new CorruptFileException with the specified detail message.
     *
     * @param message the detail message
     */
    public CorruptFileException(String message) {
        super(message);
    }

    /**
     * Constructs a new CorruptFileException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public CorruptFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
