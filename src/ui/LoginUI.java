package ui;

import account.Account;
import account.LoginManager;
import java.util.*;

/**
 * Handles UI presentation for login, registration, and guest access.
 */
public class LoginUI {
    private final Scanner scanner;
    private final LoginManager loginManager;
    private boolean isMultiplayer;
    final String BLUE = "\u001B[38;5;117m";
    final String PURPLE = "\u001B[38;5;183m";
    final String WHITE = "\u001B[97m";
    final String GRAY = "\u001B[38;5;250m";
    final String RESET = "\u001B[0m";

    private final List<String> titleVariants = List.of(
        "ᛈ ᚨ ᚱ ᚨ ᛞ ᛖ",
        "Π Α Ρ Α Δ Ε",
        "Ⲡ Ⲁ Ⲣ Ⲁ Ⲇ Ⲉ",
        "პ ა რ ა დ ე",
        "Պ Ա Ռ Ա Դ Ե",
        "פ א ר א ד ה",
        "ࠐ ࠁ ࠓ ࠁ ࠃ ࠄ",
        "ⴱ ⴰ ⵔ ⴰ ⴷ ⴻ"
    );

    private final List<String> mayanSymbols = new ArrayList<>(
        List.of("𓂀", "𓋡", "𓃂", "𓁾", "𓃖", "𓏞", "𓎿", "𓏢", "𓆃", "𓅓")
    );

    /**
     * Creates a new LoginUI.
     * 
     * @param scanner The scanner for input
     * @param isMultiplayer Whether this is for multiplayer mode
     */
    public LoginUI(Scanner scanner, boolean isMultiplayer) {
        this.scanner = scanner;
        this.loginManager = new LoginManager(scanner);
        this.isMultiplayer = isMultiplayer;
    }

    /**
     * Displays the login menu and handles user interaction.
     * 
     * @return The selected account
     */
    public Account showLoginMenu() {
        // Animate the title first
        for (int i = 0; i < titleVariants.size(); i++) {
            ConsoleUtils.clear();
            System.out.println(Header.renderHeader(
                titleVariants.get(i),
                mayanSymbols,
                Collections.emptyList()
            ));
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Final screen
        while (true) {
            ConsoleUtils.clear();
            System.out.println(BLUE + Header.renderHeader(
                "P A R A D E",
                mayanSymbols,
                List.of(
                    "[1] Login Existing Account",
                    "[2] Create New Account"
                )
            ) + WHITE);
            System.out.print("Choose your path, adventurer!\n> ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    Account loggedIn = loginManager.handleLogin();
                    if (loggedIn != null) {
                        return loggedIn;
                    }
                    break;
                case "2":
                    Account created = loginManager.handleAccountCreation();
                    if (created != null) {
                        return created;
                    }
                    break;
                default:
                    System.out.println("Invalid choice. Please key in '1' or '2' only.");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
            }
        }
    }
}