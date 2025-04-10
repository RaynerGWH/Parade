package account;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * Manages the login process and authentication for multiple user accounts.
 */
public class LoginManager {
    private final Scanner scanner;
    private final Map<String, Account> accounts;
    private Account currentAccount;
    private final AccountFileManager fileManager;
    
    /**
     * Creates a new LoginManager with the provided scanner.
     *
     * @param scanner Scanner for user input
     */
    public LoginManager(Scanner scanner) {
        this.scanner = scanner;
        this.accounts = new HashMap<>();
        this.fileManager = new AccountFileManager(scanner);
        loadAccounts();
    }
    
    /**
     * Loads all existing accounts from storage.
     */
    private void loadAccounts() {
        try {
            // Use the initialize method which properly handles exceptions
            Account account = fileManager.initialize();
            if (account != null) {
                accounts.put(account.getUsername().toLowerCase(), account);
            }
        } catch (Exception e) {
            System.out.println("Error loading accounts: " + e.getMessage());
        }
    }
    
    /**
     * Displays the login UI and manages the authentication process.
     *
     * @return The authenticated account or a guest account
     */
    public Account login() {
        while (true) {
            System.out.println("\n===== PARADE LOGIN =====");
            System.out.println("1. Login with existing account");
            System.out.println("2. Create new account");
            System.out.println("3. Continue as guest");
            System.out.print("> ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    Account loggedIn = handleLogin();
                    if (loggedIn != null) {
                        return loggedIn;
                    }
                    break;
                case "2":
                    Account created = handleAccountCreation();
                    if (created != null) {
                        return created;
                    }
                    break;
                case "3":
                    return createGuestAccount();
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    /**
     * Handles the account login process.
     *
     * @return The authenticated account or null if login failed
     */
    public Account handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        
        // Simple implementation without passwords initially
        Account account = accounts.get(username.toLowerCase());
        
        if (account != null) {
            System.out.println("Login successful! Welcome back, " + account.getUsername());
            return account;
        } else {
            System.out.println("Account not found. Would you like to create a new account? (Y/N)");
            if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                return handleAccountCreation();
            }
            return null;
        }
    }
    
    /**
     * Handles the creation of a new account.
     *
     * @return The newly created account or null if creation was cancelled
     */
    public Account handleAccountCreation() {
        System.out.print("Enter a username (alphanumeric only): ");
        String username = scanner.nextLine().trim();
        
        // Validate username
        if (!username.matches("^[A-Za-z0-9]+$")) {
            System.out.println("Invalid username. Please use only alphanumeric characters.");
            return null;
        }
        
        // Check if username already exists
        if (accounts.containsKey(username.toLowerCase())) {
            System.out.println("Username already exists. Please choose another one.");
            return null;
        }
        
        Account newAccount = new Account(username);
        try {
            // Save the account to Save.PG1
            fileManager.save(newAccount);
            accounts.put(username.toLowerCase(), newAccount);
            System.out.println("Account created successfully!");
            return newAccount;
        } catch (IOException e) {
            System.out.println("Error creating account: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Creates a temporary guest account.
     *
     * @return A new guest account that won't be saved
     */
    public Account createGuestAccount() {
        String guestName = "Guest_" + (int)(Math.random() * 10000);
        System.out.println("Continuing as " + guestName);
        return new Account(guestName);
    }
    
    /**
     * Saves the current account state.
     *
     * @param account The account to save
     */
    public void saveAccount(Account account) {
        try {
            fileManager.save(account);
        } catch (IOException e) {
            System.out.println("Error saving account: " + e.getMessage());
        }
    }
} 