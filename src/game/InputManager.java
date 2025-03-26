package game;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class InputManager {
    private static final BlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();

    public static void offerInput(String input) {
        inputQueue.offer(input);
    }

    public static String waitForInput() throws InterruptedException {
        return inputQueue.take();
    }
}

