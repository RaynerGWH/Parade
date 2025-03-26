package account;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * Represents an account with a unique identifier, username, statistics (wins/losses),
 * balance, and a list of unlocked flairs.
 */
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    // Unique identifier for the account
    private UUID id;

    // Username of the account
    private String username;

    // Number of wins associated with this account
    private int wins;

    // Number of losses associated with this account
    private int losses;

    // Monetary balance for the account
    private double balance;

    // A list of flairs that this account has unlocked
    private List<String> unlockedFlairs;

    /**
     * Creates an Account with specified parameters.
     *
     * @param id             the unique ID of the account
     * @param username       the username of the account
     * @param wins           the number of wins
     * @param losses         the number of losses
     * @param balance        the account's balance
     * @param unlockedFlairs list of flairs unlocked by this account
     */
    public Account(UUID id, String username, int wins, int losses, double balance, List<String> unlockedFlairs) {
        this.id = id;
        this.username = username;
        this.wins = wins;
        this.losses = losses;
        this.balance = balance;
        this.unlockedFlairs = unlockedFlairs;
    }

    /**
     * Creates a new Account with default values (0 wins, 0 losses, 0 balance, empty flair list)
     * and a randomly generated UUID.
     *
     * @param username the username for the new account
     */
    public Account(String username) {
        this(UUID.randomUUID(), username, 0, 0, 0.0, new ArrayList<>());
    }

    /**
     * Returns the unique identifier for this account.
     *
     * @return the {@link UUID} representing this account's ID
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the username of this account.
     *
     * @return the username as a {@link String}
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the number of wins for this account.
     *
     * @return the number of wins
     */
    public int getWins() {
        return wins;
    }

    /**
     * Returns the number of losses for this account.
     *
     * @return the number of losses
     */
    public int getLosses() {
        return losses;
    }

    /**
     * Returns the current monetary balance of this account.
     *
     * @return the balance as a double
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Retrieves the list of currently unlocked flairs for this account.
     *
     * @return a list of flair strings
     */
    public List<String> getUnlockedFlairs() {
        return unlockedFlairs;
    }

    /**
     * Adds a positive amount to the current balance.
     *
     * @param amount the amount to add
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public void addBalance(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount to add cannot be negative.");
        }
        balance += amount;
    }

    /**
     * Deducts a positive amount from the current balance. If the balance goes below zero,
     * it is reset to zero.
     *
     * @param amount the amount to deduct
     * @throws IllegalArgumentException if {@code amount} is negative
     */
    public void deductBalance(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount to deduct cannot be negative.");
        }
        balance -= amount;
        if (balance < 0) {
            balance = 0;
        }
    }

    /**
     * Increments the wins for this account by 1.
     */
    public void incrementWins() {
        wins++;
    }

    /**
     * Increments the losses for this account by 1.
     */
    public void incrementLosses() {
        losses++;
    }

    /**
     * Checks if a given flair is already unlocked.
     *
     * @param flair the flair to check
     * @return {@code true} if the flair is unlocked, otherwise {@code false}
     */
    public boolean hasFlair(String flair) {
        return unlockedFlairs.contains(flair);
    }

    /**
     * Attempts to unlock a new flair for this account.
     * <p>
     * In a real-world scenario, there would be checks here to see if the account
     * meets certain conditions (e.g., sufficient wins, balance, etc.).
     *
     * @param flair the flair to unlock
     * @return {@code true} if the flair was successfully unlocked, or {@code false} if it was already unlocked
     */
    public boolean unlockFlair(String flair) {
        if (hasFlair(flair)) {
            return false;
        }
        unlockedFlairs.add(flair);
        return true;
    }

    /**
     * Returns a string representation of this account, used primarily for file saving.
     *
     * @return the string in the format: UUID/username/wins/losses/balance/[flairs]
     */
    @Override
    public String toString() {
        return String.format("%s/%s/%d/%d/%.0f/%s", id, username, wins, losses, balance, unlockedFlairs);
    }
}
