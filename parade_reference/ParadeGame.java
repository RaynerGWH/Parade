import java.util.*;

/**
 * ParadeGame is the main entry point for this console-based card game.
 * It sets up user input, determines single-player or multi-player mode,
 * creates a Game instance, and starts the game flow.
 */
public class ParadeGame {

    // ANSI escape codes for console colors (reset, bold, etc.)
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";   // ADDED
    public static final String ANSI_PURPLE = "\u001B[35m"; // ADDED
    public static final String ANSI_CYAN = "\u001B[36m";
    
    public static void main(String[] args) {
        // Display a welcome banner with ASCII art
        System.out.println(ANSI_PURPLE
                + "\n============================================\n"
                + "        WELCOME TO THE PARADE GAME         \n"
                + "============================================\n"
                + ANSI_RESET);

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println(ANSI_BOLD + "Would you like to play Single Player or Multiplayer?" + ANSI_RESET);
            System.out.print("Enter 1 for Single Player, 2 for Multiplayer: ");
            int modeChoice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            Game game;

            if (modeChoice == 1) {
                // Single Player mode: 1 real player + 1 NPC
                System.out.println(ANSI_CYAN + "You have chosen Single Player mode!" + ANSI_RESET);
                game = new Game(true, 1); // 1 human player, plus 1 NPC
            } else {
                // Multiplayer mode
                System.out.println(ANSI_CYAN + "You have chosen Multiplayer mode!" + ANSI_RESET);
                System.out.print(ANSI_BOLD + "Enter number of players: " + ANSI_RESET);
                final int numberOfPlayers = scanner.nextInt();
                scanner.nextLine(); // consume newline
                game = new Game(false, numberOfPlayers);
            }

            // Start the main game loop
            game.startGame(scanner);
        } catch (Exception e) {
            System.err.println(ANSI_RED + "Error: " + e.getMessage() + ANSI_RESET);
        }
    }
}