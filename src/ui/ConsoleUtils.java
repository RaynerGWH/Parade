package ui;

import constants.UIConstants;
import java.io.IOException;

public class ConsoleUtils {
    private static final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");

    /**
     * Clears the console.
     */
    public static void clear() {
        try {
            if (isWindows) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // Works for macOS and most Linux terminals that support ANSI escape codes
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("Could not clear console.");
        }
    }

    /**
     * Clears the console and displays a header for a new turn.
     */
    public static void clearConsoleWithHeader() {
        // Clear the screen using the proper platform logic
        clear();

        // Force a wide, consistent console width for Visual Studio Code or IDEs
        int consoleWidth = getConsoleWidth();

        String title = " NEW TURN ";
        int sideWidth = (consoleWidth - title.length() - 2);
        int paddingLeft = sideWidth / 2;
        int paddingRight = sideWidth - paddingLeft;

        // Print the full-width rounded box
        System.out.println("╭" + "─".repeat(consoleWidth - 2) + "╮");
        System.out.println("│" + " ".repeat(paddingLeft) + title + " ".repeat(paddingRight) + "│");
        System.out.println("╰" + "─".repeat(consoleWidth - 2) + "╯");
    }

    /**
     * Gets the console width, falling back to standard values if unavailable.
     * 
     * @return The width of the console in characters
     */
    public static int getConsoleWidth() {
        // Try environment variables commonly set in Unix-based terminals
        try {
            return Integer.parseInt(System.getenv("COLUMNS"));
        } catch (Exception ignored) {
        }

        // If COLUMNS isn't available, try fallback environment
        try {
            return Integer.parseInt(System.getenv("CONSOLE_WIDTH"));
        } catch (Exception ignored) {
        }

        return 80; // Final fallback width
    }

    /**
     * Display time progress bar.
     * 
     * @param elapsedTime Time elapsed in milliseconds
     * @param timeLimit   Total time limit in milliseconds
     * @return String representation of the progress bar
     */
    public static String displayTimeProgressBar(long elapsedTime, long timeLimit) {
        int barLength = 30;
        double progress = Math.min(1.0, (double) elapsedTime / timeLimit);
        int filledBars = (int) (progress * barLength);

        StringBuilder progressBar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            progressBar.append(i < filledBars ? "█" : " ");
        }
        progressBar.append("]");

        long remainingTime = Math.max(0, timeLimit - elapsedTime);
        String timeString = formatTime(remainingTime);
        int percentage = Math.min(100, (int) (progress * 100));

        return String.format("\nTime remaining: %s%n%s%d%%", timeString, progressBar.toString(), percentage);
    }

    /**
     * Format milliseconds to mm:ss format.
     * 
     * @param milliseconds Time in milliseconds
     * @return Formatted time string
     */
    public static String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Displays a countdown animation before starting the game.
     * Uses constants for countdown numbers and adds a delay between each number.
     * 
     * @param ui The UserInterface to display messages on
     */
    public static void displayCountdown(UserInterface ui) {
        ui.broadcastMessage("\nGame starting in...");

        try {
            // Display 3
            clear();
            ui.broadcastMessage(UIConstants.COUNTDOWN_THREE);
            Thread.sleep(1000);
            clear();

            // Display 2
            ui.broadcastMessage(UIConstants.COUNTDOWN_TWO);
            Thread.sleep(1000);
            clear();

            // Display 1
            ui.broadcastMessage(UIConstants.COUNTDOWN_ONE);
            Thread.sleep(1000);
            clear();

            // Game start message
            // ui.broadcastMessage("\n                                               🎮 GAME START! 🎮\n");
            // ui.broadcastMessage(UIConstants.SEPARATOR);

        } catch (InterruptedException e) {
            // If interrupted, just continue with the game
            Thread.currentThread().interrupt();
            ui.broadcastMessage("                                  Countdown interrupted. Starting game immediately!");
        }
    }

    /**
     * Prints a single-run "PARADE" animation using a purple color.
     * The animation speed increases (delay decreases) for each subsequent row.
     *
     * @throws InterruptedException if the thread is interrupted during sleep.
     */
    public static void printParadeAnimation() throws InterruptedException {
        int timer = 70; // initial delay in milliseconds
        double timerDivisor = 1.3; // proportion to make timer smaller by to speed up animation of parade letter display

        System.out.println("\n");

        // Build the correct parade order: For "PARADE", the letters should be:
        // Index 0: "P", 1: "A", 2: "R", then we need a second "A" (reuse index 1), 
        // then index 3: "D" and index 4: "E".
        String[][] paradeOrder;
        if (UIConstants.PARADE_LETTERS.length == 5) {
            paradeOrder = new String[6][];
            paradeOrder[0] = UIConstants.PARADE_LETTERS[0]; // "P"
            paradeOrder[1] = UIConstants.PARADE_LETTERS[1]; // first "A"
            paradeOrder[2] = UIConstants.PARADE_LETTERS[2]; // "R"
            paradeOrder[3] = UIConstants.PARADE_LETTERS[1]; // second "A" (reuse the same array)
            paradeOrder[4] = UIConstants.PARADE_LETTERS[3]; // "D"
            paradeOrder[5] = UIConstants.PARADE_LETTERS[4]; // "E"
        } else {
            paradeOrder = UIConstants.PARADE_LETTERS;
        }

        // Loop through the 6 rows of the ASCII art letters.
        for (int row = 0; row < 6; row++) {
            for (String[] letter : paradeOrder) {
                System.out.print(UIConstants.PURPLE + letter[row]);
                Thread.sleep(timer);
            }
            // Corrected from REST_COLOR to RESET_COLOR.
            System.out.print(UIConstants.RESET_COLOR);
            System.out.println();
            timer /= timerDivisor; // speed up for the next row
        }
    }

    /**
     * Continuously prints the "PARADE" animation with cycling colors until input is available.
     * Clears the console on each iteration and hides the cursor for a cleaner display.
     *
     * @throws IOException if an I/O error occurs.
     * @throws InterruptedException if the thread is interrupted during sleep.
     */
    public static void printParadeAnimationLoop() throws IOException, InterruptedException {
        int colorShift = 0;
        int numRows = 6; // we expect 6 rows for the ASCII art

        // Build the correct parade order as above.
        String[][] paradeOrder;
        if (UIConstants.PARADE_LETTERS.length == 5) {
            paradeOrder = new String[6][];
            paradeOrder[0] = UIConstants.PARADE_LETTERS[0]; // "P"
            paradeOrder[1] = UIConstants.PARADE_LETTERS[1]; // first "A"
            paradeOrder[2] = UIConstants.PARADE_LETTERS[2]; // "R"
            paradeOrder[3] = UIConstants.PARADE_LETTERS[1]; // second "A"
            paradeOrder[4] = UIConstants.PARADE_LETTERS[3]; // "D"
            paradeOrder[5] = UIConstants.PARADE_LETTERS[4]; // "E"
        } else {
            paradeOrder = UIConstants.PARADE_LETTERS;
        }

        System.out.print(UIConstants.HIDE_CURSOR); // hide cursor

        while (System.in.available() == 0) {
            clear(); // assume you have a method that clears the console

            System.out.println("\n");
            // Print each row of the letters, applying a cycling color to each.
            for (int row = 0; row < numRows; row++) {
                StringBuilder line = new StringBuilder();
                for (int i = 0; i < paradeOrder.length; i++) {
                    String color = UIConstants.RAINBOW_COLORS[(i - colorShift + UIConstants.RAINBOW_COLORS.length) % UIConstants.RAINBOW_COLORS.length];
                    line.append(color)
                        .append(paradeOrder[i][row])
                        .append(UIConstants.RESET_COLOR)
                        .append(" ");
                }
                System.out.println(line);
            }

            System.out.print("\n" + UIConstants.PRESS_ENTER_TO_START);
            Thread.sleep(500);
            colorShift = (colorShift + 1) % UIConstants.RAINBOW_COLORS.length;
        }

        System.out.print(UIConstants.SHOW_CURSOR); // show cursor again
    }

}