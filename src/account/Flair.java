package account;

/**
 * Represents a flair that can be unlocked by an account. Additional fields and logic
 * can be added here to manage different flair requirements, names, and descriptions.
 * 
 * Example usage:
 *   1. Flair name (e.g. "High Roller", "Veteran", etc.)
 *   2. Description of the flair
 *   3. Any numeric thresholds needed to unlock (e.g., certain wins, balance, etc.)
 * 
 */
public class Flair {
    /**
     * The name of the flair (e.g. "High Roller", "Veteran").
     */
    private final String FLAIRNAME;

    /**
     * A short description of what this flair represents.
     */
    private final String DESCRIPTION;

    /**
     * The minimum number of wins required to unlock this flair.
     */
    private final int REQUIREDWINS;

    /**
     * The cost to purchase this flair from the account's balance.
     */
    private final double COST;

    /**
     * Constructs a new Flair with the specified characteristics.
     *
     * @param flairName    the name of the flair
     * @param description  a short description of the flair
     * @param requiredWins the minimum number of wins required to be eligible for purchase
     * @param cost         the cost in currency to buy this flair
     */
    public Flair(String flairName, String description, int requiredWins, double cost) {
        this.FLAIRNAME = flairName;
        this.DESCRIPTION = description;
        this.REQUIREDWINS = requiredWins;
        this.COST = cost;
    }

    /**
     * Returns the name of this flair.
     *
     * @return the flair name
     */
    public String getFlairName() {
        return FLAIRNAME;
    }

    /**
     * Returns the description of this flair.
     *
     * @return the flair description
     */
    public String getDescription() {
        return DESCRIPTION;
    }

    /**
     * Returns the minimum number of wins required to unlock this flair.
     *
     * @return the required wins
     */
    public int getRequiredWins() {
        return REQUIREDWINS;
    }

    /**
     * Returns the cost to purchase this flair.
     *
     * @return the cost
     */
    public double getCost() {
        return COST;
    }

    /**
     * Checks if this flair can be purchased by the given account, i.e. the account has enough wins
     * and enough balance to afford the cost.
     *
     * @param account the account to evaluate
     * @return {@code true} if the account meets the required wins and has sufficient balance;
     *         otherwise {@code false}
     */
    public boolean canBePurchasedBy(Account account) {
        return account.getWins() >= REQUIREDWINS && account.getBalance() >= COST;
    }

    /**
     * Provides a string representation of this flair, useful for logging or debugging.
     *
     * @return a string describing this flair
     */
    @Override
    public String toString() {
        return String.format(
            "Flair[name=\"%s\", desc=\"%s\", requiredWins=%d, cost=%.2f]",
            FLAIRNAME, DESCRIPTION, REQUIREDWINS, COST
        );
    }
}
