package ui;

import account.Account;
import account.LoginManager;
import constants.UIConstants;
import java.util.*;

/**
 * Handles UI presentation for login, registration, and guest access.
 */
public class LoginUI {
    private final Scanner scanner;
    private final LoginManager loginManager;
    private boolean isMultiplayer;

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
        // Clear the console and print an empty header.
        ConsoleUtils.clear();
        System.out.println("");

        // Final screen: Display login menu options.
        while (true) {
            
            ConsoleUtils.clear();
            System.out.print(UIConstants.LOGIN_BORDER);

            System.out.print(UIConstants.RESET_COLOR + "\n            [1] 🔐 Rejoin with an Existing Account            [2] 🆕 Begin Anew — Create a New Account");
            System.out.print("\n\n🎭 The Parade drums echo in the distance..." + UIConstants.ConsoleInput);

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
                    System.out.println(UIConstants.RESET_COLOR + "\n❌ Invalid choice. Please enter [1] or [2] — only the chosen paths may proceed. 🎴✨");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
            }
        }
    }
}
