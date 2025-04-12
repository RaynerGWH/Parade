import account.Account;
import account.AccountFileManager;
import account.FlairShop;
import constants.UIConstants;
import game.GameClientEndpoint;
import game.GameManager;
import game.GameServerEndpoint;
import jakarta.websocket.DeploymentException;
import rulebook.RulebookManager;
import ui.ConsoleUtils;
import ui.FlairShopUI;
import ui.LoginUI;
import ui.MultiplayerUI;
import ui.SinglePlayerUI;
import ui.UserInterface;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
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
 *   <li>Parade animation is now shown before the login page.</li>
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

    /** The RulebookManager. */
    private final RulebookManager rulebookManager;

    /** The Interface for FlairShop. */
    private final FlairShopUI flairShopUI;

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
     * Note: Login is deferred until after the parade animation.
     */
    public RunGame() {
        // Use a single Scanner for the program's lifecycle.
        this.mainScanner = new Scanner(System.in);

        // Initialize the AccountFileManager.
        this.fileMgr = new AccountFileManager(this.mainScanner);

        // Initialize the local account list.
        this.accounts = new ArrayList<>();

        // Initialize GameManager.
        this.gameMgr = new GameManager(this.mainScanner);

        // Instantiate the FlairShop.
        this.flairShop = new FlairShop(this.fileMgr);

        // Initialize the RulebookManager with the path and shared scanner.
        this.rulebookManager = new RulebookManager("src/rulebook/rulebook.txt", this.mainScanner);

        // Initialize the FlairShopUI.
        this.flairShopUI = new FlairShopUI(flairShop, mainScanner);
    }

    /**
     * Primary run loop that first displays the parade animation,
     * then shows the login page, and subsequently offers the user the option
     * to read the rulebook, start the game, or access the flair shop.
     */
    private void run() {
        try {
            // Show parade animations on startup.
            ConsoleUtils.clear();
            ConsoleUtils.printParadeAnimation();
            ConsoleUtils.printParadeAnimationLoop();
            mainScanner.nextLine(); // Wait for user input before proceeding.

            // Now, display the login page.
            LoginUI loginUI = new LoginUI(this.mainScanner, false);
            this.currentAccount = loginUI.showLoginMenu();

            // Save the account for persistence.
            try {
                fileMgr.save(currentAccount);
            } catch (IOException e) {
                System.out.println("Error saving account: " + e.getMessage());
            }

            // Add the logged-in account to the local accounts list.
            this.accounts.add(this.currentAccount);

            boolean exitRequested = false;
            while (!exitRequested) {
                ConsoleUtils.clear();

                System.out.print(UIConstants.MAIN_SCREEN + "\n");
                System.out.println(UIConstants.RESET_COLOR + String.join("\n", List.of(
                        "[S] Start Game 🎮",
                        "[B] Buy Flairs ✨",
                        "[R] Read Rulebook 📖",
                        "[Q] Quit ❌"
                )));
                System.out.print(UIConstants.ConsoleInput);

                String command = mainScanner.nextLine().trim().toUpperCase();

                switch (command) {
                    case "R":
                        rulebookManager.displayRulebook();
                        break;
                    case "S":
                        selectGameMode();
                        exitRequested = true; // Exit run() after starting a game.
                        break;
                    case "B":
                        flairShopUI.openFlairShopMenu(currentAccount);
                        break;
                    case "Q":
                        if (confirmQuit()) {
                            System.out.println("\n🚪 You’ve left the Parade.\nUntil next time, traveler! 🎴🌙");
                            System.exit(0);
                        }
                        break;
                    default:
                        ConsoleUtils.clear();
                        System.out.println(String.join("\n", List.of(
                            "Invalid command. Please enter [R], [S], [B] or [Q]."
                        )));
                        System.out.print("\nPress ENTER to try again...");
                        mainScanner.nextLine();
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
        while (true) {
            ConsoleUtils.clear();

            System.out.println(UIConstants.GAMEMODE_SCREEN + UIConstants.RESET_COLOR);

            System.out.println(String.join("\n", List.of(
                "SELECT GAMEMODE",
                "[S] Singleplayer 👤",
                "[M] Multiplayer 👥🎮",
                "[Q] Quit ❌"
            )));
            System.out.print(UIConstants.ConsoleInput);
            String mode = mainScanner.nextLine().trim().toUpperCase();

            switch (mode) {
                case "S":
                    startSinglePlayer();
                    return;
                case "M":
                    startMultiPlayer();
                    return;
                case "Q":
                    if (confirmQuit()) {
                        System.out.println("\n🚪 You’ve left the Parade.\nUntil next time, traveler! 🎴🌙");
                        System.exit(0);
                    }
                    break;
                default:
                    ConsoleUtils.clear();
                    System.out.println(String.join("\n", List.of(
                        "Invalid command. Please enter [S], [M], or [Q]."
                    )));
                    System.out.print("\nPress ENTER to try again...");
                    mainScanner.nextLine();
            }
        }
    }

    /**
     * Starts single-player mode.
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
            ConsoleUtils.clear();
            System.out.println(String.join("\n", List.of(
                "SELECT CONNECTION",
                "[H] Host Room 🛖",
                "[J] Join Room 🔗",
                "[Q] Quit ❌"
            )));
            System.out.print(UIConstants.ConsoleInput);
            String subCmd = mainScanner.nextLine().trim().toUpperCase();

            switch (subCmd) {
                case "H":
                    hostMultiPlayer();
                    return;
                case "J":
                    joinMultiPlayer();
                    return;
                case "Q":
                    if (confirmQuit()) {
                        System.out.println("\n🚪 You’ve left the Parade.\nUntil next time, traveler! 🎴🌙");
                        System.exit(0);
                    }
                    break;
                default:
                    ConsoleUtils.clear();
                    System.out.println(String.join("\n", List.of(
                        "Invalid command. Please enter [H], [J], or [Q]."
                    )));
                    System.out.print("\nPress ENTER to try again...");
                    mainScanner.nextLine();
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
                System.out.print("Enter a valid IP Address"  + UIConstants.ConsoleInput);
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
     * Asks the user to confirm a quit request.
     * @return true if user confirms quit, false otherwise
     */
    private boolean confirmQuit() {
        ConsoleUtils.clear();
        System.out.println(UIConstants.RESET_COLOR + "❓ Are you certain you wish to leave the Parade?");
        System.out.println(String.join("\n", List.of(
            "",
            "[Enter] March onward with the crowd.",
            "[Q] Quit your journey."
        )));
        System.out.print(UIConstants.ConsoleInput);
        String confirm = mainScanner.nextLine().trim().toUpperCase();
        return confirm.equals("Q");
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }
}