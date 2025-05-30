package players.human;

import cards.*;
import players.AbstractPlayer;
import constants.UIConstants;
import account.Account;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.websocket.*;

public class HumanPlayer extends AbstractPlayer {

    private String name;
    private transient Session session;
    private Scanner sc;

    // New field to hold the associated Account
    private Account account;
    // Timeout duration in seconds
    private static final int TIMEOUT_SECONDS = 30;

    // New field for LoginManager
    // New field for accounts

    public HumanPlayer(ArrayList<Card> hand, String name, Session session, Scanner sc) {
        super(hand);
        this.name = name;
        this.session = session;
        this.sc = sc;
    }

    // Getter for the Account object
    public Account getAccount() {
        return account;
    }

    // Setter for the Account object
    public void setAccount(Account account) {
        this.account = account;
    }

    // Singleplayer handler
    @Override
    public Card chooseCardToPlay() {
        handleCardSelection("play");

        // Setup timeout mechanism
        final AtomicBoolean timeoutOccurred = new AtomicBoolean(false);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            timeoutOccurred.set(true);
            // Add a newline to ensure scanner doesn't block
            System.out.println(UIConstants.RESET_COLOR + "\n\n⌛ Timeout reached! Automatically playing first card. Press enter to continue.");
        }, TIMEOUT_SECONDS, TimeUnit.SECONDS);

        int index = -1;
        while (!timeoutOccurred.get()) {
            try {
                if (sc.hasNextLine()) {
                    String input = sc.nextLine();
                    try {
                        index = Integer.parseInt(input);
                        if (index >= 0 && index < hand.size()) {
                            executor.shutdownNow();
                            return playCard(index);
                        } else {
                            System.out.print("Invalid index. Please enter a number between 0 and " + (hand.size() - 1) + UIConstants.ConsoleInput);
                        }
                    } catch (NumberFormatException e) {
                        System.out.print(UIConstants.RESET_COLOR + "❌ Invalid input. Please enter a valid number.\n" + UIConstants.LIGHT_PURPLE + "> ");
                    }
                }
                // Small sleep to prevent CPU hogging
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // If timeout occurred, play the first card
        if (timeoutOccurred.get()) {
            index = 0; // Play the first card
        }

        executor.shutdownNow();
        return playCard(index);
    }

    // Singleplayer handler
    @Override
    public Card chooseCardToDiscard() {
        handleCardSelection("discard");

        // Setup timeout mechanism
        final AtomicBoolean timeoutOccurred = new AtomicBoolean(false);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            timeoutOccurred.set(true);
            // Add a newline to ensure scanner doesn't block
            System.out.println(UIConstants.RESET_COLOR + "\n\n⌛ Timeout reached! Automatically discarding first card.");
        }, TIMEOUT_SECONDS, TimeUnit.SECONDS);

        int index = -1;
        while (!timeoutOccurred.get()) {
            try {
                if (sc.hasNextLine()) {
                    String input = sc.nextLine();
                    try {
                        index = Integer.parseInt(input);
                        if (index >= 0 && index < hand.size()) {
                            executor.shutdownNow();
                            return playCard(index);
                        } else {
                            System.out.println("Invalid index. Please enter a number between 0 and " + (hand.size() - 1) + ".");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(UIConstants.RESET_COLOR + "❌ Invalid input. Please enter a valid number.");
                    }
                }
                // Small sleep to prevent CPU hogging
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // If timeout occurred, discard the first card
        if (timeoutOccurred.get()) {
            index = 0; // Discard the first card
        }

        executor.shutdownNow();
        return playCard(index);
    }

    private void handleCardSelection(String action) {
        if (hand.isEmpty()) {
            System.out.println("You have no cards to " + action + ".");
            return;
        }

        promptForCardIndex(action);
    }

    private void promptForCardIndex(String action) {
        String displayName = name;
        if (account != null) {
            // Check the worn flair instead of the first in the unlocked list
            String worn = account.getWornFlair();
            if (worn != null && !worn.isEmpty()) {
                displayName = account.getUsername() + " [" + worn + "]";
            } else {
                displayName = account.getUsername();
            }
        }

        String prompt = displayName + ", enter the position of the card you want to " + action + " (0 - " + (hand.size() - 1) +")" + UIConstants.ConsoleInput;

        // Only print directly to console in non-multiplayer contexts
        // In multiplayer mode, the Game class will handle communication with clients
        if (session == null) {
            System.out.print(prompt);
        }
    }

    /**
     * Displays a prompt to hit ENTER to end the turn and waits for user input.
     * Only the current player should see this prompt.
     */
    public void waitForEnterToEndTurn() {
        System.out.println("[ENTER] End Turn");

        // Wait for the ENTER key
        try {
            sc.nextLine();
        } catch (Exception e) {
            // Handle any potential exceptions
        }
    }

    /**
     * Static method that can be called to wait for any player to press ENTER to
     * advance after a bot has played.
     *
     * @param scanner The scanner to use for input
     */
    public static void waitForAnyPlayerToAdvance(Scanner scanner) {
        System.out.println("[ENTER] Continue");

        try {
            scanner.nextLine();
        } catch (Exception e) {
            // Handle any potential exceptions
        }
    }

    public String getName() {
        return name;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session newSession) {
        this.session = newSession;
    }
    
}
