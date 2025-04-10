package account;

import java.io.IOException;
import java.util.*;

import exceptions.CorruptFileException;

/**
 * Manages the login process and authentication for multiple user accounts.
 */
public class LoginManager {
    private final Scanner scanner;
    private final Map<String, Account> accounts;
    private final AccountFileManager fileManager;
    private static Account currAccount;

    public LoginManager(Scanner scanner) {
        this.scanner = scanner;
        this.accounts = new HashMap<>();
        this.fileManager = new AccountFileManager(scanner);
        loadAccounts();  // Load all .PG1 files at startup
    }

    /**
     * Loads all existing accounts from storage into the map.
     */
    private void loadAccounts() {
        try {
            List<Account> loaded = fileManager.loadAllAccounts();
            for (Account account : loaded) {
                accounts.put(account.getUsername().toLowerCase(), account);
            }
        } catch (IOException | RuntimeException | CorruptFileException e) {
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
            System.out.print("> ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    return handleLogin();
                case "2":
                    return handleAccountCreation();
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Handles login by letting the user select from available accounts.
     */
    public Account handleLogin() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts found. Would you like to create a new one? (Y/N)");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y")) {
                handleAccountCreation();
                return null;
            } else {
                return null;
            }
        }

        List<Account> accountList = new ArrayList<>(accounts.values());
        System.out.println("\nAvailable accounts:");
        for (int i = 0; i < accountList.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, accountList.get(i).getUsername());
        }

        System.out.print("Select account by number (or 0 to cancel): ");
        String input = scanner.nextLine().trim();
        try {
            int choice = Integer.parseInt(input);
            if (choice == 0) {
                return null;
            }
            if (choice > 0 && choice <= accountList.size()) {
                Account selected = accountList.get(choice - 1);
                System.out.println("Login successful! Welcome back, " + selected.getUsername());
                currAccount = selected;
                return selected;
            } else {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
        return null;
    }

    /**
     * Handles the creation of a new account.
     *
     * @return The newly created account or null if creation failed
     */
    public Account handleAccountCreation() {
        while (true) {
            System.out.print("Enter a username (alphanumeric only): ");
            String username = scanner.nextLine().trim();

            if (!username.matches("^[a-zA-Z0-9]+$")) {
                System.out.println("Invalid username. Please use only alphanumeric characters.(No spaces)");
                continue;
            }

            if (accounts.containsKey(username.toLowerCase())) {
                System.out.println("Username already exists.");
                continue;
            }

            Account newAccount = new Account(username);
            try {
                fileManager.save(newAccount);
                accounts.put(username.toLowerCase(), newAccount);
                System.out.println("Account created successfully!");
                currAccount = newAccount;
                return newAccount;
            } catch (IOException e) {
                System.out.println("Failed to save account: " + e.getMessage());
                return null;
            }
        }
    }

    /**
     * Saves changes to an account.
     *
     * @param account the account to save
     */
    public void saveAccount(Account account) {
        try {
            fileManager.save(account);
        } catch (IOException e) {
            System.out.println("Error saving account: " + e.getMessage());
        }
    }

    public static Account getCurrentAccount() {
        return currAccount;
    }
}
