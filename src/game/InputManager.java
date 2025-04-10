package game;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class InputManager {
    private static final BlockingQueue<String> INPUT_QUEUE = new LinkedBlockingQueue<>();
    
    public static void offerInput(String input) {
        // Accept empty strings (ENTER key presses) for turn advancement
        INPUT_QUEUE.offer(input == null ? "" : input);
    }
    
    public static String waitForInput() throws InterruptedException {
        // Wait indefinitely for input
        return INPUT_QUEUE.take();
    }
    
    public static String waitForInputWithTimeout(long timeout, TimeUnit unit) throws InterruptedException {
        // Wait with timeout and return null if nothing arrives
        return INPUT_QUEUE.poll(timeout, unit);
    }
    
    /**
     * Wait for the ENTER key press only (empty input)
     * This is specifically for turn advancement.
     */
    public static void waitForEnterPress() throws InterruptedException {
        // Just wait for any input and discard it - we just care about notification
        INPUT_QUEUE.take();
    }
    
    public static void clearInput() {
        INPUT_QUEUE.clear();
    }
}

