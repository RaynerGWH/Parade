package ui;

import account.Account;
import account.LoginManager;

import java.util.Scanner;

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
        while (true) {
            // Clear the console
            ConsoleUtils.clear();
            
            System.out.println("\n===== PARADE LOGIN =====");
            System.out.println("1. Login with existing account");
            System.out.println("2. Create new account");
            System.out.println("3. Continue as guest");
            System.out.print("> ");
            
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
                case "3":
                    return loginManager.createGuestAccount();
                default:
                    System.out.println("Invalid choice. Please try again.");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // Ignore
                    }
            }
        }
    }
} 