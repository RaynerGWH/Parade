package account;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a "shop" from which players can buy flairs. This class stores a list of
 * available {@link Flair} objects. An account can purchase a flair if:
 * 
 *   1. The account's wins are >= the flair's required wins</li>
 *   2. The account's balance is >= flair's cost</li>
 */
public class FlairShop {
    /**
     * The list of flairs available for purchase.
     */
    private final List<Flair> availableFlairs;

    /**
     * Constructs a FlairShop and populates it with some sample flairs.
     */
    public FlairShop() {
        this.availableFlairs = new ArrayList<>();
        // Flairs (name, description, minWins, cost)
        availableFlairs.add(new Flair("grass toucher", "grass is horrified at your presence", 0, 0.0));
        availableFlairs.add(new Flair("i luv cat", "meow meow", 5, 100.0));
        availableFlairs.add(new Flair("i luv dawg", "roof roof", 5, 100.0));
        availableFlairs.add(new Flair("mr halfway there", "25 more wins to a pointless title", 25, 250.0));
        availableFlairs.add(new Flair("pointless title", "pls get a life :3", 50, 500.0));
        availableFlairs.add(new Flair("egg", "maybe there's an easter egg...", 100, 9999.0));

    }

    /**
     * Returns the list of flairs currently available in the shop.
     * @return an unmodifiable view or a copy of the list of flairs
     */
    public List<Flair> getAvailableFlairs() {
        // Return a copy to preserve internal data
        return new ArrayList<>(availableFlairs);
    }

    /**
     * Attempts to purchase a flair (by name) for the specified account.
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
            return true;
        }

        return false;
    }
}
