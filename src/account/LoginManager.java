package account;

import constants.UIConstants;
import exceptions.CorruptFileException;
import java.io.IOException;
import java.util.*;
import ui.ConsoleUtils;

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
        loadAccounts(); 
        if (accounts.isEmpty()) {
            System.out.print(UIConstants.RESET_COLOR + "\n🧾 No account detected in the archives.\nShall we forge a new hero for the journey? (Y/N) ⚔️ ✨" + UIConstants.ConsoleInput);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y")) {
                handleAccountCreation();
                return null;
            } else {
                System.out.println(UIConstants.RESET_COLOR + "\n❌ Invalid choice. Please enter [Y] or [N] — only the chosen paths may proceed. 🎴✨");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
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
                return handleAccountCreation();
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
        loadAccounts(); 
        while (true) {
            System.out.print(UIConstants.RESET_COLOR + "\n💬 Choose your name, adventurer (letters & numbers only)" + UIConstants.ConsoleInput);
            String username = scanner.nextLine().trim();

            if (!username.matches("^[a-zA-Z0-9]+$")) {
                System.out.println(UIConstants.RESET_COLOR + "\n❌ That name breaks the magic! Please use only letters and numbers — no spaces or symbols.");
                continue;
            }

            if (accounts.containsKey(username.toLowerCase())) {
                System.out.print(UIConstants.RESET_COLOR + "\n❗ That name's already taken by another adventurer.\n");
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
