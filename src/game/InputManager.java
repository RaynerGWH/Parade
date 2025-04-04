package game;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class InputManager {
    private static final BlockingQueue<String> INPUT_QUEUE = new LinkedBlockingQueue<>();

    public static void offerInput(String input) {
        INPUT_QUEUE.offer(input);
    }

    public static String waitForInput() throws InterruptedException {
        String input = INPUT_QUEUE.poll(30, TimeUnit.SECONDS);
        INPUT_QUEUE.clear();
        return input;
    }
}

