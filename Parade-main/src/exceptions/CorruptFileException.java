package exceptions;

public class CorruptFileException extends Exception {
    //checked exception
    public CorruptFileException() {
        super("Corrupted account information file. Please delete your .PG1 file to access the game.");
    }
}
