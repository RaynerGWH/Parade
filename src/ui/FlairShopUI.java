package ui;

import account.Account;
import account.Flair;
import account.FlairShop;
import constants.UIConstants;
import ui.ConsoleUtils;

import java.util.List;
import java.util.Scanner;

/**
 * Manages the UI for the flair shop, allowing users to purchase and select flairs.
 */
public class FlairShopUI {
    private final FlairShop flairShop;
    private final Scanner scanner;

    public FlairShopUI(FlairShop flairShop, Scanner scanner) {
        this.flairShop = flairShop;
        this.scanner = scanner;
    }

    public void openFlairShopMenu(Account account) {
        while (true) {
            ConsoleUtils.clear();
            List<Flair> availableFlairs = flairShop.getAvailableFlairs();

            String padding = "                    ";
            int boxWidth = 78;
            System.out.println(padding + "╭" + "─".repeat(boxWidth) + "╮");
            System.out.println(padding + "│    ⬤  🔵  🟢  🟡" + " ".repeat(boxWidth - 19) + "  │");
            System.out.println(padding + "│" + " ".repeat(boxWidth) + "│");
            System.out.println(padding + "│" + centerText("🏪  WELCOME TO THE FLAIR SHOP  🏪", boxWidth) + "│");
            System.out.println(padding + "│" + " ".repeat(boxWidth) + "│");
            System.out.println(padding + "│" + padRight("  Flair Name               Status         Cost ($)    Required Wins", boxWidth) + "│");
            System.out.println(padding + "│" + " ".repeat(boxWidth) + "│");

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
                String flairLine = String.format("  [%d] %-20s %-12s %10.2f %16d", i + 1, flair.getFlairName(), status, flair.getCost(), flair.getRequiredWins());
                System.out.println(padding + "│" + padRight(flairLine, boxWidth) + "│");
            }

            System.out.println(padding + "│" + " ".repeat(boxWidth) + "│");
            System.out.println(padding + "│" + padRight("  [1-7] Buy/Wear 🎩      [Q] Quit Shop 🚪", boxWidth) + "│");
            System.out.println(padding + "│" + " ".repeat(boxWidth) + "│");
            String statsLine = String.format("  [Balance] $%-10.2f  [Wins] %-3d  [Losses] %-3d",
                    account.getBalance(), account.getWins(), account.getLosses());
            System.out.println(padding + "│" + padRight(statsLine, boxWidth) + "│");
            System.out.println(padding + "╰" + "─".repeat(boxWidth) + "╯");

            // Ensure prompt is visible right after the UI
            System.out.print("\n" + padding + UIConstants.ConsoleInput);
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

    private void handleExistingFlair(Account account, Flair flair) {
        List<String> ownedFlairs = account.getUnlockedFlairs();
        if (!ownedFlairs.isEmpty() && ownedFlairs.get(0).equalsIgnoreCase(flair.getFlairName())) {
            System.out.println("This flair is already being worn.");
        } else {
            System.out.print("You already own this flair. Do you want to wear it? (Y/N)" + UIConstants.ConsoleInput);
            String wearInput = scanner.nextLine().trim();
            if (wearInput.equalsIgnoreCase("Y")) {
                boolean setWorn = flairShop.selectFlairToWear(flair.getFlairName(), account);
                System.out.println(setWorn
                        ? "You are now wearing '" + flair.getFlairName() + "'."
                        : "Failed to set flair as worn.");
            }
        }
    }

    private void handleFlairPurchase(Account account, Flair flair) {
        boolean purchased = flairShop.purchaseFlair(flair.getFlairName(), account);
        if (purchased) {
            System.out.println("Purchase successful! You now own '" + flair.getFlairName() + "'.");
            System.out.print("Do you want to wear this flair now? (Y/N)" + UIConstants.ConsoleInput);
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

    private String padRight(String text, int width) {
        return String.format("%-" + width + "s", text);
    }

    private String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text + " ".repeat(Math.max(0, width - padding - text.length()));
    }
}