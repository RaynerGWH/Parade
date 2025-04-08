package game;

import java.util.Scanner;

public class InputReader implements Runnable {
    private final Scanner scanner;

    public InputReader() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        while (true) {
            // Check if there's an input line ready and read it.
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                InputManager.offerInput(input);
            }
        }
    }
}
