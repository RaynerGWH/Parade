import account.Account;
import account.AccountFileManager;
import account.Flair;
import account.FlairShop;
import account.LoginManager;
import game.GameClientEndpoint;
import game.GameManager;
import game.GameServerEndpoint;
import jakarta.websocket.DeploymentException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import ui.ConsoleUtils;
import ui.LoginUI;
import ui.MultiplayerUI;
import ui.SinglePlayerUI;
import ui.UserInterface;

/**
 * Revised RunGame class applying clean code principles:
 * 1) Single Scanner usage for user input to avoid NoSuchElementException.
 * 2) Avoid closing the scanner inside scrollRulebook.
 * 3) Clear method and variable names.
 * 4) Minimal catch blocks & clear error messages.
 * 5) Sufficient commentary explaining logic.
 *
 * Now updated to include a FlairShop option in the menu,
 * and to save purchases to Save.PG1 via AccountFileManager.
 */
public class RunGame {

    /** Single Scanner instance for the entire program's console input. */
    private final Scanner mainScanner;

    /** Example storage of accounts if single-player or local usage. */
    private final ArrayList<Account> accounts;

    /** A PlayerManager with no-arg constructor for potential expansions. */
    // private final PlayerManager playerMgr;

    /** Our main GameManager from snippet 1. */
    private final GameManager gameMgr;

    /** The FlairShop to allow users to purchase flairs. */
    private final FlairShop flairShop;

    /** The AccountFileManager used to load/save data to Save.PG1. */
    private final AccountFileManager fileMgr;

    /**
     * Entry point of the program.
     * @param args command-line arguments (unused).
     */
    public static void main(String[] args) {
        RunGame rg = new RunGame();
        rg.run();
    }

    /**
     * Constructor: initializes the main Scanner, accounts, managers, etc.
     */
    public RunGame() {
        // Single Scanner for the entire lifecycle.
        this.mainScanner = new Scanner(System.in);

        // Create the AccountFileManager first
        this.fileMgr = new AccountFileManager(this.mainScanner);
        
        // Create LoginUI and get an Account
        LoginUI loginUI = new LoginUI(this.mainScanner, false);
        Account currentAccount = loginUI.showLoginMenu();
        
        // Save the account to ensure persistence
        try {
            fileMgr.save(currentAccount);
        } catch (IOException e) {
            System.out.println("Error saving account: " + e.getMessage());
        }

        // Put that Account into our local list.
        this.accounts = new ArrayList<>();
        this.accounts.add(currentAccount);

        // No-arg constructor for PlayerManager.
        // this.playerMgr = new PlayerManager();

        // Our snippet-1 style GameManager that takes the main scanner.
        this.gameMgr = new GameManager(this.mainScanner);

        // Instantiate the FlairShop with the file manager, so successful purchases save
        // to Save.PG1.
        this.flairShop = new FlairShop(this.fileMgr);
    }

    /**
     * Primary run loop that offers the user the choice to read the rulebook or start the game.
     */
    private void run() {
        try {
            // Fancy animation on startup.
            ConsoleUtils.clear();
            printParadeAnimation();
            printParadeAnimationLoop();
            mainScanner.nextLine();

            while (true) {
                ConsoleUtils.clear();
                System.out.print("Enter 'R' to refer to the rulebook,\n" +
                        "      'S' to start the game, or\n" +
                        "      'SHOP' to open the flair shop\n> ");
                String command = mainScanner.nextLine().trim().toUpperCase();

                if (command.equals("R")) {
                    // Show the rulebook using the same scanner.
                    scrollRulebook(mainScanner, "src/rulebook/rulebook.txt");

                } else if (command.equals("S")) {
                    // Start the game: single or multi?
                    System.out.print("Enter 'S' for Singleplayer or 'M' for Multiplayer\n> ");
                    String mode = mainScanner.nextLine().trim().toUpperCase();

                    if (mode.equals("S")) {
                        startSinglePlayer();
                        return; // after single-player, exit the run() method.

                    } else if (mode.equals("M")) {
                        startMultiPlayer();
                        return; // after multi-player, exit run().
                    } else {
                        System.out.println("Please only enter 'S' or 'M'.\n> ");
                    }

                } else if (command.equals("SHOP")) {
                    // Show the shop menu (for demonstration, we pick the first account)
                    Account currentAccount = accounts.get(0);
                    openFlairShopMenu(currentAccount);

                } else {
                    System.out.println("Command not recognized.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // We can close the scanner once we are truly done with the program.
            mainScanner.close();
        }
    }

    /**
     * Displays the FlairShop menu, showing available flairs and letting the user purchase them.
     * Also allows the user to select (wear) one of their owned flairs.
     * @param account the account that wants to browse/purchase flairs
     */
    private void openFlairShopMenu(Account account) {
        while (true) {

            // Display current account stats
            System.out.println("Current Balance : " + account.getBalance());
            System.out.println("Wins            : " + account.getWins());
            System.out.println("Losses          : " + account.getLosses());

            List<Flair> availableFlairs = flairShop.getAvailableFlairs();
            System.out.println("\nAvailable Flairs:");
            for (int i = 0; i < availableFlairs.size(); i++) {
                Flair flair = availableFlairs.get(i);
                boolean alreadyOwned = account.hasFlair(flair.getFlairName());
                String status = "";
                if (alreadyOwned) {
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
                    // Already owned: offer the option to wear it
                    List<String> ownedFlairs = account.getUnlockedFlairs();
                    if (!ownedFlairs.isEmpty() && ownedFlairs.get(0).equalsIgnoreCase(chosenFlair.getFlairName())) {
                        System.out.println("This flair is already being worn.");
                    } else {
                        System.out.println("You already own this flair. Do you want to wear it? (Y/N)\n> ");
                        String wearInput = mainScanner.nextLine().trim();
                        if (wearInput.equalsIgnoreCase("Y")) {
                            boolean setWorn = flairShop.selectFlairToWear(chosenFlair.getFlairName(), account);
                            if (setWorn) {
                                System.out.println("You are now wearing '" + chosenFlair.getFlairName() + "'.");
                            } else {
                                System.out.println("Failed to set flair as worn.");
                            }
                        }
                    }
                } else {
                    // Attempt to purchase the flair
                    boolean purchased = flairShop.purchaseFlair(chosenFlair.getFlairName(), account);
                    if (purchased) {
                        System.out.println("Purchase successful! You now own '" + chosenFlair.getFlairName() + "'.");
                        System.out.println("Do you want to wear this flair now? (Y/N)\n> ");
                        String wearNowInput = mainScanner.nextLine().trim();
                        if (wearNowInput.equalsIgnoreCase("Y")) {
                            boolean setWorn = flairShop.selectFlairToWear(chosenFlair.getFlairName(), account);
                            if (setWorn) {
                                System.out.println("You are now wearing '" + chosenFlair.getFlairName() + "'.");
                            } else {
                                System.out.println("Failed to set flair as worn.");
                            }
                        }
                    } else {
                        System.out.println("Purchase failed. You may not meet the requirements or have enough balance.");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number or Q.\n");
            }
        }
    }

    /**
     * Launches single-player mode using the snippet-1 approach (GameManager.start).
     */
    private void startSinglePlayer() {
        UserInterface ui = new SinglePlayerUI();
        gameMgr.start(ui, null);
    }

    /**
     * Launches multi-player mode, hosting (H) or joining (J).
     * If hosting, we create a GameServerEndpoint; if joining, we create a GameClientEndpoint.
     */
    private void startMultiPlayer() {
        while (true) {
            System.out.print("Please enter 'H' to host, or 'J' to join\n> ");
            String subCmd = mainScanner.nextLine().trim().toUpperCase();

            if (subCmd.equals("H")) {
                // Host
                GameServerEndpoint gse = new GameServerEndpoint();
                UserInterface ui = new MultiplayerUI(gse);
                gameMgr.start(ui, gse);
                return;
            } else if (subCmd.equals("J")) {
                // Join
                while (true) {
                    try {
                        String uriString = "ws://";
                        System.out.print("Enter a valid IP Address\n> ");
                        uriString += mainScanner.nextLine();
                        uriString += "/game";
                        URI uri = new URI(uriString);
                        GameClientEndpoint gce = new GameClientEndpoint(uri, mainScanner);
                        CountDownLatch latch = new CountDownLatch(1);
                        gce.setLatch(latch);
                        latch.await();
                        gce.shutdown();
                        return;
                    } catch (URISyntaxException e) {
                        System.out.println("Invalid IP address entered. Please try again.");
                    } catch (InterruptedException e) {
                        System.out.println("Connection interrupted. Please try again.");
                    } catch (DeploymentException e) {
                        System.out.println("An error has occurred. Check the IP address that you entered.");
                    } catch (Exception e) {
                        System.out.println("An error has occurred. Please try again.");
                    }
                }
            } else {
                System.out.println("Unrecognized command. Please enter 'H' to host or 'J' to join.");
            }
        }
    }

    /**
     * Prints the fancy 'PARADE' animation seen in snippet 1/2.
     * @throws InterruptedException if the thread is interrupted during sleep.
     */
    private void printParadeAnimation() throws InterruptedException {
        String purpleColor = "\u001B[35m";
        String resetColor = "\u001B[0m";
        String[] paradeLetterP = {
                "██████╗ ",
                "██╔══██╗",
                "██████╔╝",
                "██╔═══╝ ",
                "██║     ",
                "╚═╝     " };

        String[] paradeLetterA1 = {
                " █████╗ ",
                "██╔══██╗",
                "███████║",
                "██╔══██║",
                "██║  ██║",
                "╚═╝  ╚═╝" };

        String[] paradeLetterR = {
                "██████╗ ",
                "██╔══██╗",
                "██████╔╝",
                "██╔══██╗",
                "██║  ██║",
                "╚═╝  ╚═╝" };

        // We reuse the letter A
        String[] paradeLetterA2 = paradeLetterA1;
        String[] paradeLetterD = {
                "██████╗ ",
                "██╔══██╗",
                "██║  ██║",
                "██║  ██║",
                "██████╔╝",
                "╚═════╝ " };

        String[] paradeLetterE = {
                "███████╗",
                "██╔════╝",
                "█████╗  ",
                "██╔══╝  ",
                "███████╗",
                "╚══════╝" };

        String[][] letters = {
                paradeLetterP, paradeLetterA1, paradeLetterR, paradeLetterA2, paradeLetterD, paradeLetterE
        };

        int timer = 70;
        for (int row = 0; row < 6; row++) {
            for (String[] letter : letters) {
                System.out.print(purpleColor + letter[row]);
                Thread.sleep(timer);
            }
            System.out.print(resetColor);
            System.out.println();
            timer /= 1.3; // speed up each row
        }
    }

    private void printParadeAnimationLoop() throws IOException, InterruptedException {
        String[] colors = {
                "\u001B[31m", // Red
                "\u001B[91m", // Orange (bright red)
                "\u001B[33m", // Yellow
                "\u001B[32m", // Green
                "\u001B[34m", // Blue
                "\u001B[35m" // Purple
        };
        String resetColor = "\u001B[0m";

        String[][] letters = {
                {
                        "██████╗ ",
                        "██╔══██╗",
                        "██████╔╝",
                        "██╔═══╝ ",
                        "██║     ",
                        "╚═╝     "
                },
                {
                        " █████╗ ",
                        "██╔══██╗",
                        "███████║",
                        "██╔══██║",
                        "██║  ██║",
                        "╚═╝  ╚═╝"
                },
                {
                        "██████╗ ",
                        "██╔══██╗",
                        "██████╔╝",
                        "██╔══██╗",
                        "██║  ██║",
                        "╚═╝  ╚═╝"
                },
                {
                        " █████╗ ",
                        "██╔══██╗",
                        "███████║",
                        "██╔══██║",
                        "██║  ██║",
                        "╚═╝  ╚═╝"
                },
                {
                        "██████╗ ",
                        "██╔══██╗",
                        "██║  ██║",
                        "██║  ██║",
                        "██████╔╝",
                        "╚═════╝ "
                },
                {
                        "███████╗",
                        "██╔════╝",
                        "█████╗  ",
                        "██╔══╝  ",
                        "███████╗",
                        "╚══════╝"
                }
        };

        int colorShift = 0;
        int numRows = letters[0].length;

        System.out.print("\u001B[?25l"); // hide cursor

        while (System.in.available() == 0) {
            ConsoleUtils.clear();

            // Assign each letter a color based on the current shift
            for (int row = 0; row < numRows; row++) {
                StringBuilder line = new StringBuilder();
                for (int i = 0; i < letters.length; i++) {
                    String color = colors[(i - colorShift + colors.length) % colors.length];
                    line.append(color).append(letters[i][row]).append(resetColor).append(" ");
                }
                System.out.println(line);
            }

            System.out.print("\nPress ENTER to start...");
            Thread.sleep(500);
            colorShift = (colorShift + 1) % colors.length;
        }

        System.out.print("\u001B[?25h"); // show cursor again
    }

    /**
     * Scrolls through the rulebook file, using the same Scanner as the main program.
     * We do NOT close the scanner here, so that main program remains interactive afterwards.
     *
     * @param sc The shared Scanner from RunGame.
     * @param filePath Path to the rulebook file.
     */
    public static void scrollRulebook(Scanner sc, String filePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            int linesPerPage = 15;
            int totalPages = (int) Math.ceil((double) lines.size() / linesPerPage);
            int currentPage = 0;

            while (true) {
                int start = currentPage * linesPerPage;
                int end = Math.min(start + linesPerPage, lines.size());

                // We clear the console
                ConsoleUtils.clear();

                // Print the page
                System.out.println("Page " + (currentPage + 1) + " of " + totalPages + ":");
                for (int i = start; i < end; i++) {
                    System.out.println(lines.get(i));
                }

                if (currentPage == 0) {
                    System.out.print("\nEnter (N)ext or (Q)uit: ");
                } else if (currentPage == totalPages - 1) {
                    System.out.print("\nEnter (P)revious, (F)irst, or (Q)uit: ");
                } else {
                    System.out.print("\nEnter (N)ext, (P)revious, or (Q)uit: ");
                }

                if (!sc.hasNextLine()) {
                    System.out.println("No more input available.");
                    return;
                }

                String input = sc.nextLine().trim().toUpperCase();
                switch (input) {
                    case "N":
                        if (currentPage < totalPages - 1) {
                            currentPage++;
                        } else {
                            System.out.println("This is the last page.");
                            boolean userQuit = navigateEndOfRulebook(sc, totalPages, currentPage);
                            if (userQuit) {
                                // user pressed Q in sub-nav => exit entire rulebook
                                return;
                            }
                        }
                        break;
                    case "P":
                        if (currentPage > 0) {
                            currentPage--;
                        } else {
                            System.out.println("This is the first page.");
                        }
                        break;
                    case "Q":
                        ConsoleUtils.clear();
                        System.out.println("Exited rulebook.\n");
                        return;
                    default:
                        System.out.println("Invalid input. Please try again.");
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading rulebook file: " + e.getMessage());
        }
    }

    /**
     * Handles user navigation after they reach the last page.
     * @return true if user selected Q (quit entirely), false otherwise.
     */
    private static boolean navigateEndOfRulebook(Scanner sc, int totalPages, int currentPage) {
        while (true) {
            System.out.print("Enter (P)revious, (F)irst, or (Q)uit: ");

            if (!sc.hasNextLine()) {
                System.out.println("No more input.");
                return false;
            }

            String subInput = sc.nextLine().trim().toUpperCase();
            switch (subInput) {
                case "P":
                    if (currentPage > 0) {
                        currentPage--;
                    }
                    return false; // means keep going in scrollRulebook
                case "F":
                    currentPage = 0;
                    return false;
                case "Q":
                    System.out.println("Exiting rulebook.");
                    return true;  // user chose to quit => exit entire method
                default:
                    System.out.println("Invalid input.");
                    // remain in loop until valid choice
                    break;
            }
        }
    }
}