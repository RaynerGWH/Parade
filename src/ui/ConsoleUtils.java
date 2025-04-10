package ui;

import constants.*;

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
            ui.broadcastMessage(Constants.COUNTDOWN_THREE);
            Thread.sleep(1000);
            clear();

            // Display 2
            ui.broadcastMessage(Constants.COUNTDOWN_TWO);
            Thread.sleep(1000);
            clear();

            // Display 1
            ui.broadcastMessage(Constants.COUNTDOWN_ONE);
            Thread.sleep(1000);
            clear();

            // Game start message
            ui.broadcastMessage("\n🎮 GAME START! 🎮\n");
            ui.broadcastMessage(Constants.SEPARATOR);

        } catch (InterruptedException e) {
            // If interrupted, just continue with the game
            Thread.currentThread().interrupt();
            ui.broadcastMessage("Countdown interrupted. Starting game immediately!");
        }
    }
}