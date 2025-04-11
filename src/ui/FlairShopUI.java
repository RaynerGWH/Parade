package ui;

import account.Account;
import account.Flair;
import account.FlairShop;
import constants.UIConstants;

import java.util.List;
import java.util.Scanner;

/**
 * Manages the UI for the flair shop, allowing users to purchase and select flairs.
 */
public class FlairShopUI {
    private final FlairShop flairShop;
    private final Scanner scanner;

    /**
     * Creates a new FlairShopUI with the specified flair shop and scanner.
     *
     * @param flairShop The flair shop to use
     * @param scanner The scanner for user input
     */
    public FlairShopUI(FlairShop flairShop, Scanner scanner) {
        this.flairShop = flairShop;
        this.scanner = scanner;
    }

    /**
 * Displays the flair shop menu, letting the user purchase or select flairs.
 * 
 * @param account the account to update.
 */
public void openFlairShopMenu(Account account) {
    while (true) {
        // Clear the console and display account stats.
        ConsoleUtils.clear();

        List<Flair> availableFlairs = flairShop.getAvailableFlairs();
        System.out.println("\n                                             Welcome Adventurer!\n\n                                  Which flair would you like to buy today 🏪\n");
        for (int i = 0; i < availableFlairs.size(); i++) {
            Flair flair = availableFlairs.get(i);
            String status = "";
            if (account.hasFlair(flair.getFlairName())) {
                List<String> ownedFlairs = account.getUnlockedFlairs();
                if (!ownedFlairs.isEmpty() && ownedFlairs.get(0).equalsIgnoreCase(flair.getFlairName())) {
                    status = "[Wearing]";
                } else {
                    status = "[OWNED]";
                }
            }
            // The following format specifiers:
            // %-20s: left-aligns the flair name in a 20-character field.
            // %10.2f: right-aligns the cost in a 10-character field with 2 decimals.
            // %3d: right-aligns the required wins in a 3-character field.
            System.out.printf("                         [%d] %-20s  Cost: %10.2f  Required Wins: %3d  %s%n",
                    i + 1,
                    flair.getFlairName(),
                    flair.getCost(),
                    flair.getRequiredWins(),
                    status
            );
        }

        System.out.print("\n                                [1-7] Buy or Wear 🎩           [Q] Quit Shop 🚪\n");
        System.out.print("\n\n                             [Balance] $" + account.getBalance() + "           [Wins] " + account.getWins() + "           [Losses] " + account.getLosses() + "\n\n" + UIConstants.LIGHT_PURPLE + "> ");
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("Q")) {
            System.out.println("Exiting shop menu.");
            break;
        }
        

        try {
            int choice = Integer.parseInt(input);
            if (choice < 1 || choice > availableFlairs.size()) {
                System.out.println("\nInvalid choice. Please try again.");
                continue;
            }

            Flair chosenFlair = availableFlairs.get(choice - 1);

            if (account.hasFlair(chosenFlair.getFlairName())) {
                handleExistingFlair(account, chosenFlair);
            } else {
                handleFlairPurchase(account, chosenFlair);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number or Q.\n");
        }
    }
}

    
    /**
     * Handles the case where a user already owns a flair and wants to wear it.
     * 
     * @param account The user's account
     * @param flair The flair to handle
     */
    private void handleExistingFlair(Account account, Flair flair) {
        List<String> ownedFlairs = account.getUnlockedFlairs();
        if (!ownedFlairs.isEmpty() && ownedFlairs.get(0).equalsIgnoreCase(flair.getFlairName())) {
            System.out.println("This flair is already being worn.");
        } else {
            System.out.print("You already own this flair. Do you want to wear it? (Y/N)" + UIConstants.LIGHT_PURPLE + "\n> ");
            String wearInput = scanner.nextLine().trim();
            if (wearInput.equalsIgnoreCase("Y")) {
                boolean setWorn = flairShop.selectFlairToWear(flair.getFlairName(), account);
                System.out.println(setWorn
                        ? "You are now wearing '" + flair.getFlairName() + "'."
                        : "Failed to set flair as worn.");
            }
        }
    }
    
    /**
     * Handles the purchase of a new flair.
     * 
     * @param account The user's account
     * @param flair The flair to purchase
     */
    private void handleFlairPurchase(Account account, Flair flair) {
        boolean purchased = flairShop.purchaseFlair(flair.getFlairName(), account);
        if (purchased) {
            System.out.println("Purchase successful! You now own '" + flair.getFlairName() + "'.");
            System.out.print("Do you want to wear this flair now? (Y/N)" + UIConstants.LIGHT_PURPLE + "\n> ");
            String wearNowInput = scanner.nextLine().trim();
            if (wearNowInput.equalsIgnoreCase("Y")) {
                boolean setWorn = flairShop.selectFlairToWear(flair.getFlairName(), account);
                System.out.println(setWorn
                        ? "You are now wearing '" + flair.getFlairName() + "'."
                        : "Failed to set flair as worn.");
            }
        } else {
            System.out.println("Purchase failed. Requirements or balance may be insufficient.");
        }
    }
}