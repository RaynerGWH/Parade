package account;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a "shop" from which players can buy flairs. This class stores a list of
 * available {@link Flair} objects. An account can purchase a flair if:
 *
 *   1. The account's wins are >= the flair's required wins
 *   2. The account's balance is >= flair's cost
 *
 * Additionally, once a flair is successfully purchased, this class
 * will update the Save.PG1 file via AccountFileManager.
 */
public class FlairShop {
    /**
     * The list of flairs available for purchase.
     */
    private final List<Flair> availableFlairs;

    /**
     * A reference to the AccountFileManager, so we can save changes to Save.PG1.
     */
    private final AccountFileManager fileManager;

    /**
     * Constructs a FlairShop using the given AccountFileManager
     * and populates it with some sample flairs.
     *
     * @param fileManager the file manager used to save account updates
     */
    public FlairShop(AccountFileManager fileManager) {
        this.fileManager = fileManager;
        this.availableFlairs = new ArrayList<>();

        // Sample flairs (name, description, minWins, cost)
        availableFlairs.add(new Flair("grass toucher", "grass is horrified at your presence", 0, 0.0));
        availableFlairs.add(new Flair("i luv cat", "meow meow", 5, 100.0));
        availableFlairs.add(new Flair("i luv dawg", "roof roof", 5, 100.0));
        availableFlairs.add(new Flair("mr halfway there", "25 more wins to a pointless title", 25, 250.0));
        availableFlairs.add(new Flair("pointless title", "pls get a life :3", 50, 500.0));
        availableFlairs.add(new Flair("egg", "maybe there's an easter egg...", 100, 9999.0));
    }

    /**
     * Returns the list of flairs currently available in the shop.
     * @return a copy of the list of flairs
     */
    public List<Flair> getAvailableFlairs() {
        // Return a copy to preserve internal data
        return new ArrayList<>(availableFlairs);
    }

    /**
     * Attempts to purchase a flair (by name) for the specified account, then
     * saves the updated account data to Save.PG1 if successful.
     *
     * @param flairName the name of the flair to purchase
     * @param account   the account attempting the purchase
     * @return {@code true} if the flair was successfully purchased and unlocked,
     *         {@code false} otherwise
     */
    public boolean purchaseFlair(String flairName, Account account) {
        // Find the flair by name
        Flair flairToBuy = null;
        for (Flair f : availableFlairs) {
            if (f.getFlairName().equalsIgnoreCase(flairName)) {
                flairToBuy = f;
                break;
            }
        }
        // Flair not found
        if (flairToBuy == null) {
            return false;
        }

        // Check if the account already owns this flair
        if (account.hasFlair(flairToBuy.getFlairName())) {
            return false; // Already owned
        }

        // Check if the account meets requirements
        if (flairToBuy.canBePurchasedBy(account)) {
            // Deduct cost from account
            account.deductBalance(flairToBuy.getCost());

            // Unlock flair
            account.unlockFlair(flairToBuy.getFlairName());

            // Immediately save to file so the user’s Save.PG1 is updated
            try {
                fileManager.save(account);
            } catch (IOException e) {
                System.err.println("Failed to save updated account data: " + e.getMessage());
            }

            return true;
        }

        return false;
    }
}