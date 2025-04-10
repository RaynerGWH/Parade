package game;
import java.util.Scanner;

public class InputReader implements Runnable {
    private final Scanner scanner;
    private volatile boolean running = true;
    
    public InputReader() {
        this.scanner = new Scanner(System.in);
    }
    
    @Override
    public void run() {
        try {
            while (running) {
                // Check if there's an input line ready and read it
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine();
                    InputManager.offerInput(input);
                }
                
                // Small sleep to prevent CPU hogging
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            // Thread was interrupted, exit gracefully
        } finally {
            // Don't close the scanner as it would close System.in
        }
    }
    
    public void stop() {
        running = false;
    }
}