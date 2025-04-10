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
        // Wait for any input, including empty strings (ENTER key presses)
        String input = INPUT_QUEUE.poll(30, TimeUnit.SECONDS);
        // Optionally clear the queue after polling if that suits your design.
        INPUT_QUEUE.clear();
        return input;
    }
    
    /**
     * Wait for the ENTER key press only (empty input)
     * This is specifically for turn advancement.
     */
    public static void waitForEnterPress() throws InterruptedException {
        String input = INPUT_QUEUE.poll(30, TimeUnit.SECONDS);
        INPUT_QUEUE.clear();
    }
}

