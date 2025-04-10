import account.Account;
import account.AccountFileManager;
import account.Flair;
import account.FlairShop;
import game.GameClientEndpoint;
import game.GameManager;
import game.GameServerEndpoint;
import jakarta.websocket.DeploymentException;
import ui.ConsoleUtils;
import ui.LoginUI;
import ui.MultiplayerUI;
import ui.SinglePlayerUI;
import ui.UserInterface;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * Revised RunGame class applying Clean Code principles.
 * <p>
 * Key changes:
 * <ul>
 *   <li>Single Scanner usage for input.</li>
 *   <li>Clear, helper methods for each operation (login, rulebook scrolling, single/multi-player, flair shop, etc.).</li>
 *   <li>Centralized definition of ASCII art for the "PARADE" animation to eliminate duplicates.</li>
 *   <li>Improved error handling and descriptive error messages.</li>
 *   <li>Separated navigation logic for the rulebook into a dedicated helper.</li>
 * </ul>
 * </p>
 */
public class RunGame {

    /** Single Scanner instance for the entire program's console input. */
    private final Scanner mainScanner;

    /** Local collection of accounts (for single-player or local usage). */
    private final List<Account> accounts;

    /** Our main GameManager instance. */
    private final GameManager gameMgr;

    /** The FlairShop for purchasing and selecting flairs. */
    private final FlairShop flairShop;

    /** The AccountFileManager used to load/save data to Save.PG1. */
    private final AccountFileManager fileMgr;

    /** The currently logged-in account. */
    private Account currentAccount;

    /** ANSI code to reset terminal colors. */
    private static final String RESET_COLOR = "\u001B[0m";

    /**
     * Centralized ASCII art for each letter in "PARADE".
     * Since the word contains two 'A's, the same art is used for both.
     */
    private static final Map<Character, String[]> PARADE_LETTER_ART = createParadeLetterArt();

    private static Map<Character, String[]> createParadeLetterArt() {
        Map<Character, String[]> letters = new HashMap<>();

        letters.put('P', new String[]{
            "                           ██████╗ ",
            "                           ██╔══██╗",
            "                           ██████╔╝",
            "                           ██╔═══╝ ",
            "                           ██║     ",
            "                           ╚═╝     "
        });
        letters.put('A', new String[]{
            " █████╗ ",
            "██╔══██╗",
            "███████║",
            "██╔══██║",
            "██║  ██║",
            "╚═╝  ╚═╝"
        });
        letters.put('R', new String[]{
            "██████╗ ",
            "██╔══██╗",
            "██████╔╝",
            "██╔══██╗",
            "██║  ██║",
            "╚═╝  ╚═╝"
        });
        letters.put('D', new String[]{
            "██████╗ ",
            "██╔══██╗",
            "██║  ██║",
            "██║  ██║",
            "██████╔╝",
            "╚═════╝ "
        });
        letters.put('E', new String[]{
            "███████╗",
            "██╔════╝",
            "█████╗  ",
            "██╔══╝  ",
            "███████╗",
            "╚══════╝"
        });

        return Collections.unmodifiableMap(letters);
    }

    /**
     * Entry point of the program.
     * @param args command-line arguments (unused).
     */
    public static void main(String[] args) {
        RunGame gameRunner = new RunGame();
        gameRunner.run();
    }

    /**
     * Constructor: initializes the main Scanner, accounts, managers, etc.
     */
    public RunGame() {
        // Use a single Scanner for the program's lifecycle.
        this.mainScanner = new Scanner(System.in);

        // Initialize the AccountFileManager.
        this.fileMgr = new AccountFileManager(this.mainScanner);
        
        // Handle login and obtain the current Account.
        LoginUI loginUI = new LoginUI(this.mainScanner, false);
        this.currentAccount = loginUI.showLoginMenu();
        
        // Save the account for persistence.
        try {
            fileMgr.save(currentAccount);
        } catch (IOException e) {
            System.out.println("Error saving account: " + e.getMessage());
        }

        // Initialize the local account list.
        this.accounts = new ArrayList<>();
        this.accounts.add(this.currentAccount);

        // Initialize GameManager.
        this.gameMgr = new GameManager(this.mainScanner);

        // Instantiate the FlairShop (purchases update Save.PG1 via the file manager).
        this.flairShop = new FlairShop(this.fileMgr);
    }

    /**
     * Primary run loop that offers the user the option to read the rulebook,
     * start the game, or access the flair shop.
     */
    private void run() {
        try {
            // Show parade animations on startup.
            ConsoleUtils.clear();
            printParadeAnimation();
            printParadeAnimationLoop();
            mainScanner.nextLine(); // Wait for user input before proceeding.

            boolean exitRequested = false;
            while (!exitRequested) {
                ConsoleUtils.clear();
                System.out.print("Enter 'R' to refer to the rulebook,\n" +
                        "      'S' to start the game, or\n" +
                        "      'SHOP' to open the flair shop\n> ");

                String command = mainScanner.nextLine().trim().toUpperCase();

                switch (command) {
                    case "R":
                        scrollRulebook("src/rulebook/rulebook.txt");
                        break;
                    case "S":
                        selectGameMode();
                        exitRequested = true; // Exit run() after starting a game.
                        break;
                    case "SHOP":
                        openFlairShopMenu(currentAccount);
                        break;
                    default:
                        System.out.println("Command not recognized.");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the Scanner only when the program is completely finished.
            mainScanner.close();
        }
    }

    /**
     * Displays a main menu to choose either Singleplayer or Multiplayer mode.
     */
    private void selectGameMode() {
        System.out.print("Enter 'S' for Singleplayer or 'M' for Multiplayer\n> ");
        String mode = mainScanner.nextLine().trim().toUpperCase();

        switch (mode) {
            case "S":
                startSinglePlayer();
                break;
            case "M":
                startMultiPlayer();
                break;
            default:
                System.out.println("Please only enter 'S' or 'M'.");
                selectGameMode(); // Retry if input is invalid.
        }
    }

    /**
     * Starts single-player mode (snippet-1 approach).
     */
    private void startSinglePlayer() {
        UserInterface ui = new SinglePlayerUI();
        gameMgr.start(ui, null);
    }

    /**
     * Starts multi-player mode by letting the user choose hosting or joining.
     */
    private void startMultiPlayer() {
        while (true) {
            System.out.print("Please enter 'H' to host, or 'J' to join\n> ");
            String subCmd = mainScanner.nextLine().trim().toUpperCase();

            if (subCmd.equals("H")) {
                hostMultiPlayer();
                return;
            } else if (subCmd.equals("J")) {
                joinMultiPlayer();
                return;
            } else {
                System.out.println("Unrecognized command. Please enter 'H' to host or 'J' to join.");
            }
        }
    }

    /**
     * Hosts a multi-player game.
     */
    private void hostMultiPlayer() {
        GameServerEndpoint serverEndpoint = new GameServerEndpoint();
        UserInterface ui = new MultiplayerUI(serverEndpoint);
        gameMgr.start(ui, serverEndpoint);
    }

    /**
     * Joins a multi-player game by prompting the user for a valid IP address.
     */
    private void joinMultiPlayer() {
        while (true) {
            try {
                System.out.print("Enter a valid IP Address\n> ");
                String ipAddress = mainScanner.nextLine().trim();
                URI serverURI = new URI("ws://" + ipAddress + "/game");

                GameClientEndpoint clientEndpoint = new GameClientEndpoint(serverURI, mainScanner);
                CountDownLatch latch = new CountDownLatch(1);
                clientEndpoint.setLatch(latch);
                latch.await();
                clientEndpoint.shutdown();
                return;
            } catch (URISyntaxException e) {
                System.out.println("Invalid IP address entered. Please try again.");
            } catch (InterruptedException e) {
                System.out.println("Connection interrupted. Please try again.");
                Thread.currentThread().interrupt(); // Restore interrupt flag.
            } catch (DeploymentException e) {
                System.out.println("Deployment error. Check the IP address and try again.");
            } catch (Exception e) {
                System.out.println("An unexpected error occurred. Please try again.");
            }
        }
    }

    /**
     * Displays the flair shop menu, letting the user purchase or select flairs.
     * @param account the account to update.
     */
    private void openFlairShopMenu(Account account) {
        while (true) {
            // Display account stats.
            System.out.println("Current Balance : " + account.getBalance());
            System.out.println("Wins            : " + account.getWins());
            System.out.println("Losses          : " + account.getLosses());

            List<Flair> availableFlairs = flairShop.getAvailableFlairs();
            System.out.println("\nAvailable Flairs:");
            for (int i = 0; i < availableFlairs.size(); i++) {
                Flair flair = availableFlairs.get(i);
                String status = "";
                if (account.hasFlair(flair.getFlairName())) {
                    List<String> ownedFlairs = account.getUnlockedFlairs();
                    if (!ownedFlairs.isEmpty() && ownedFlairs.get(0).equalsIgnoreCase(flair.getFlairName())) {
                        status = "[Wearing]";
                    } else {
                        status = "[OWNED]";
                    }
                }
                System.out.printf("%d) %s - Cost: %.2f, Required Wins: %d %s%n",
                        i + 1,
                        flair.getFlairName(),
                        flair.getCost(),
                        flair.getRequiredWins(),
                        status);
            }

            System.out.println("\nEnter the number of the flair to purchase or wear it, or 'Q' to quit shop\n> ");
            String input = mainScanner.nextLine().trim();
            if (input.equalsIgnoreCase("Q")) {
                System.out.println("Exiting shop menu.");
                break;
            }

            try {
                int choice = Integer.parseInt(input);
                if (choice < 1 || choice > availableFlairs.size()) {
                    System.out.println("Invalid choice. Please try again.");
                    continue;
                }

                Flair chosenFlair = availableFlairs.get(choice - 1);

                if (account.hasFlair(chosenFlair.getFlairName())) {
                    // Flair already owned: offer option to wear it.
                    List<String> ownedFlairs = account.getUnlockedFlairs();
                    if (!ownedFlairs.isEmpty() && ownedFlairs.get(0).equalsIgnoreCase(chosenFlair.getFlairName())) {
                        System.out.println("This flair is already being worn.");
                    } else {
                        System.out.print("You already own this flair. Do you want to wear it? (Y/N)\n> ");
                        String wearInput = mainScanner.nextLine().trim();
                        if (wearInput.equalsIgnoreCase("Y")) {
                            boolean setWorn = flairShop.selectFlairToWear(chosenFlair.getFlairName(), account);
                            System.out.println(setWorn
                                    ? "You are now wearing '" + chosenFlair.getFlairName() + "'."
                                    : "Failed to set flair as worn.");
                        }
                    }
                } else {
                    // Attempt to purchase the flair.
                    boolean purchased = flairShop.purchaseFlair(chosenFlair.getFlairName(), account);
                    if (purchased) {
                        System.out.println("Purchase successful! You now own '" + chosenFlair.getFlairName() + "'.");
                        System.out.print("Do you want to wear this flair now? (Y/N)\n> ");
                        String wearNowInput = mainScanner.nextLine().trim();
                        if (wearNowInput.equalsIgnoreCase("Y")) {
                            boolean setWorn = flairShop.selectFlairToWear(chosenFlair.getFlairName(), account);
                            System.out.println(setWorn
                                    ? "You are now wearing '" + chosenFlair.getFlairName() + "'."
                                    : "Failed to set flair as worn.");
                        }
                    } else {
                        System.out.println("Purchase failed. Requirements or balance may be insufficient.");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number or Q.\n");
            }
        }
    }

    /**
     * Prints a single-run "PARADE" animation using a purple color.
     * The animation speed increases (delay decreases) for each subsequent row.
     *
     * @throws InterruptedException if the thread is interrupted during sleep.
     */
    private void printParadeAnimation() throws InterruptedException {
        final String purpleColor = "\u001B[35m";
        final String word = "PARADE";
        int timer = 70; // initial delay in milliseconds

        // Each letter's ASCII art has 6 rows.
        for (int row = 0; row < 6; row++) {
            for (char letter : word.toCharArray()) {
                String[] letterArt = PARADE_LETTER_ART.get(letter);
                System.out.print(purpleColor + letterArt[row]);
                Thread.sleep(timer);
            }
            System.out.print(RESET_COLOR);
            System.out.println();
            timer /= 1.3; // speed up for the next row
        }
    }

    /**
     * Continuously prints the "PARADE" animation with cycling colors until input is available.
     * Clears the console on each iteration and hides the cursor for a cleaner display.
     *
     * @throws IOException if an I/O error occurs.
     * @throws InterruptedException if the thread is interrupted during sleep.
     */
    private void printParadeAnimationLoop() throws IOException, InterruptedException {
        final String[] colors = {
            "\u001B[31m", // Red
            "\u001B[91m", // Bright Red / Orange
            "\u001B[33m", // Yellow
            "\u001B[32m", // Green
            "\u001B[34m", // Blue
            "\u001B[35m"  // Purple
        };

        final String word = "PARADE";
        final int numRows = 6;
        int colorShift = 0;

        System.out.print("\u001B[?25l"); // Hide the cursor

        while (System.in.available() == 0) {
            ConsoleUtils.clear();

            // Build and print each row with cycling colors.
            for (int row = 0; row < numRows; row++) {
                StringBuilder line = new StringBuilder();
                int letterIndex = 0;
                for (char letter : word.toCharArray()) {
                    String[] letterArt = PARADE_LETTER_ART.get(letter);
                    String color = colors[(letterIndex - colorShift + colors.length) % colors.length];
                    line.append(color).append(letterArt[row]).append(RESET_COLOR).append(" ");
                    letterIndex++;
                }
                System.out.println(line);
            }

            System.out.print("\n             Press ENTER to start...");
            Thread.sleep(500);
            colorShift = (colorShift + 1) % colors.length;
        }

        System.out.print("\u001B[?25h"); // Show the cursor again
    }

    /**
     * Displays the rulebook file page by page.
     * This method uses the shared Scanner without closing it.
     *
     * @param filePath Path to the rulebook file.
     */
    public void scrollRulebook(String filePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            final int linesPerPage = 15;
            final int totalPages = (int) Math.ceil((double) lines.size() / linesPerPage);
            int currentPage = 0;

            while (true) {
                ConsoleUtils.clear();
                int start = currentPage * linesPerPage;
                int end = Math.min(start + linesPerPage, lines.size());

                System.out.println("Page " + (currentPage + 1) + " of " + totalPages + ":");
                for (int i = start; i < end; i++) {
                    System.out.println(lines.get(i));
                }

                int updatedPage = updateRulebookPage(currentPage, totalPages);
                if (updatedPage == -1) {
                    ConsoleUtils.clear();
                    System.out.println("Exited rulebook.\n");
                    break;
                }
                currentPage = updatedPage;
            }
        } catch (IOException e) {
            System.out.println("Error reading rulebook file: " + e.getMessage());
        }
    }

    /**
     * Helper method for rulebook navigation.
     * Prompts the user based on the current page and returns the updated page number,
     * or -1 if the user wishes to quit.
     *
     * @param currentPage the current page (0-indexed).
     * @param totalPages the total number of pages.
     * @return the updated page number, or -1 to signal quitting.
     */
    private int updateRulebookPage(int currentPage, int totalPages) {
        String prompt;
        if (currentPage == 0) {
            prompt = "\nEnter (N)ext or (Q)uit: ";
        } else if (currentPage == totalPages - 1) {
            prompt = "\nEnter (P)revious, (F)irst, or (Q)uit: ";
        } else {
            prompt = "\nEnter (N)ext, (P)revious, or (Q)uit: ";
        }

        System.out.print(prompt);
        if (!mainScanner.hasNextLine()) {
            System.out.println("No more input available.");
            return -1;
        }
        String input = mainScanner.nextLine().trim().toUpperCase();

        switch (input) {
            case "N":
                if (currentPage < totalPages - 1) {
                    return currentPage + 1;
                } else {
                    System.out.println("This is the last page.");
                    return currentPage;
                }
            case "P":
                if (currentPage > 0) {
                    return currentPage - 1;
                } else {
                    System.out.println("This is the first page.");
                    return currentPage;
                }
            case "F":
                return 0;
            case "Q":
                return -1;
            default:
                System.out.println("Invalid input. Please try again.");
                return currentPage;
        }
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }
}
