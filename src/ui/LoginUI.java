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
    final String BLUE = "\u001B[38;5;117m";
    final String PURPLE = "\u001B[38;5;183m";
    final String WHITE = "\u001B[97m";
    final String GRAY = "\u001B[38;5;250m";
    final String RESET = "\u001B[0m";
    
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
        // Clear the console
        ConsoleUtils.clear();
        while (true) {
            
        System.out.println(
            BLUE + 
            "                                                                ╱|      \n" +
            "                                                             ♡ (` - 7.        \n" +
            "                                                               |、⁻〵      \n" + 
            BLUE +
            "❦❧༺═──⟡⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯じしˍ,)ノ⎯⎯⎯⎯⎯⟡──═༻\n" +
            " ❦║                                                                              ║\n" +
            " ❦║" + WHITE + "                       ☆ WELCOME TO THE PARADE PARADISE ☆                     " + BLUE + "║\n" +
            " ❦║                                                                              ║\n" +
            " ❦║" + WHITE + "               Only the worthy may proceed beyond this screen...              " + BLUE + "║\n" +
            " ❦║                                                                              ║\n" +
            " ❦༺═──⟡⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⟡──═༻" + RESET

        );

        // "꧁║" + PURPLE + "             ╭─────────────────────╮     ╭────────────────────────╮           " + BLUE + "║\n" +
        // "꧁║" + PURPLE + "             │ [1] Login Existing  │     │ [2] Create New Account │           " + BLUE + "║\n" +
        // "꧁║" + PURPLE + "             ╰─────────────────────╯     ╰────────────────────────╯           " + BLUE + "║\n" +
        // "꧁║                                                                              ║\n" +
        // "꧁║" + PURPLE + "                         ╭─────────────────────────╮                          " + BLUE + "║\n" +
        // "꧁║" + PURPLE + "                         │  [3] Continue as Guest  │                          " + BLUE + "║\n" +
        // "꧁║" + PURPLE + "                         ╰─────────────────────────╯                          " + BLUE + "║\n" +
        // "꧁║                                                                              ║\n" +

        System.out.print(BLUE + "꧁ " + WHITE + "Choose your path, adventurer! \n> " + RESET);
            
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
            // Clear the console
            ConsoleUtils.clear();
        }
    }
} 