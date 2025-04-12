package account;

import exceptions.CorruptFileException;
import ui.ConsoleUtils;

import java.io.IOException;
import java.util.*;

import constants.UIConstants;

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
     * Handles login by letting the user select from available accounts.
     */
    public Account handleLogin() {
        if (accounts.isEmpty()) {
            System.out.print("\n🧾 No account detected in the archives.\nShall we forge a new hero for the journey? (Y/N) ⚔️ ✨" + UIConstants.ConsoleInput);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y")) {
                handleAccountCreation();
                return null;
            } else {
                return null;
            }
        }

        ConsoleUtils.clear();
        List<Account> accountList = new ArrayList<>(accounts.values());
        System.out.println(UIConstants.RESET_COLOR + "\n📜 Choose your champion to rejoin the Parade!");
        for (int i = 0; i < accountList.size(); i++) {
            System.out.printf("[%d] %s%n", i + 1, accountList.get(i).getUsername());
        }

        System.out.print("\n🎯 Select your hero by number, or type [0] to summon a new one." + UIConstants.ConsoleInput);
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
