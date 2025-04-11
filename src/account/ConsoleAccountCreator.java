package account;

import java.util.Scanner;
import java.io.IOException;

/**
 * Handles console-based user interaction for creating new accounts.
 * 
 */
public class ConsoleAccountCreator {

    /** Regex to validate alphanumeric usernames. */
    private static final String USERNAME_REGEX = "^[A-Za-z0-9]+$";

    /**
     * Prompts the user to create a new account.
     *
     * @param scanner a {@link Scanner} attached to an input stream (e.g., System.in)
     * @return a newly created {@link Account}
     * @throws IOException if the user chooses not to create an account
     */
    public static Account createNewAccount(Scanner scanner) throws IOException {
        System.out.print("No existing account found. Would you like to create a new account? (Y/N) > ");
        String response = scanner.nextLine().trim().toUpperCase();

        while (!response.equals("Y") && !response.equals("N")) {
            System.out.print("Invalid input. Please type 'Y' or 'N': ");
            response = scanner.nextLine().trim().toUpperCase();
        }

        if (response.equals("Y")) {
            String username;
            while (true) {
                System.out.print("🎮 Choose your username (letters & numbers only):\n> ");
                username = scanner.nextLine().trim().toUpperCase();

                if (validateUsername(username)) {
                    break;
                }
                System.out.println("⚠️ Invalid Username!\nOnly letters and numbers allowed — no spaces, symbols, or fancy stuff. Try again, adventurer! 🧙‍♂️✨");
            }
            return new Account(username);
        } else {
            throw new IOException("Account creation aborted. An account is required to proceed.");
        }
    }

    /**
     * Validates a username against the {@link #USERNAME_REGEX}.
     *
     * @param username the username to validate
     * @return {@code true} if the username is valid; otherwise, {@code false}
     */
    private static boolean validateUsername(String username) {
        return username.matches(USERNAME_REGEX);
    }
}
