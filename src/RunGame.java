import account.Account;
import account.AccountFileManager;
import account.FlairShop;
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
import ui.Header;
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

    /** The RulebookManager. **/
    private final RulebookManager rulebookManager;

    /** The Interface for FlairShop.**/
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

        // Initialize the RulebookManager with the path and shared scanner
        this.rulebookManager = new RulebookManager("src/rulebook/rulebook.txt", this.mainScanner);

        // Initialize the flairShopUI.
        this.flairShopUI = new FlairShopUI(flairShop, mainScanner);
    }

    /**
     * Primary run loop that offers the user the option to read the rulebook,
     * start the game, or access the flair shop.
     */
    private void run() {
        try {
            // Show parade animations on startup.
            ConsoleUtils.clear();
            ConsoleUtils.printParadeAnimation();
            ConsoleUtils.printParadeAnimationLoop();
            mainScanner.nextLine(); // Wait for user input before proceeding.

            boolean exitRequested = false;
            while (!exitRequested) {
                ConsoleUtils.clear();
                System.out.println(Header.renderHeader(List.of("[R] Read Rulebook 📖", "[S] Start Game 🎮", "[B] Buy Flairs ✨", "[Q] Quit ❌")));
                System.out.print("\n> ");

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
                        System.out.println("Exiting game. Goodbye!");
                        System.exit(0);
                    }
                    break;
                    default:
                    ConsoleUtils.clear();
                    System.out.println(Header.renderHeader(List.of(
                        "Invalid command. Please enter [R], [S], [B] or [Q].")));
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
            System.out.println(Header.renderHeader(List.of("SELECT GAMEMODE", "[S] Singleplayer 👤", "[M] Multiplayer 👥🎮", "[Q] Quit ❌")));
            System.out.print("\n> ");
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
                        System.out.println("Exiting game. Goodbye!");
                        System.exit(0);
                    }
                    break;
                default:
                    ConsoleUtils.clear();
                    System.out.println(Header.renderHeader(List.of(
                        "Invalid command. Please enter [S], [M], or [Q].")));
                    System.out.print("\nPress ENTER to try again...");
                    mainScanner.nextLine();
            }
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
            ConsoleUtils.clear();
            System.out.println(Header.renderHeader(List.of(
                "SELECT CONNECTION",
                "[H] Host Room 🛖",
                "[J] Join Room 🔗",
                "[Q] Quit ❌"
            )));
            System.out.print("\n> ");
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
                        System.out.println("Exiting game. Goodbye!");
                        System.exit(0);
                    }
                    break;
                default:
                    ConsoleUtils.clear();
                    System.out.println(Header.renderHeader(List.of(
                        "Invalid command. Please enter [H], [J], or [Q].")));
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
     * Asks user to confirm quit request.
     * @return true if user confirms quit, false otherwise
     */
    private boolean confirmQuit() {
        ConsoleUtils.clear();
        System.out.println(Header.renderHeader(List.of(
            "Are you sure you want to quit?",
            "Press [Q] to confirm, or press the ENTER key to stay in the game."
        )));
        System.out.print("\n> ");
        String confirm = mainScanner.nextLine().trim().toUpperCase();
        return confirm.equals("Q");
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }
}
