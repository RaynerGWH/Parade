package players;

import cards.*;

import java.util.ArrayList;
import java.util.Scanner;

public class HumanPlayer extends AbstractPlayer {
    private Scanner scanner;
    private String name;

    public HumanPlayer(ArrayList<Card> hand, String name, Scanner scanner) {
        super(hand);
        this.name = name;
        // Use the passed-in scanner rather than creating a new one
        this.scanner = scanner;
    }

    @Override
    public Card chooseCardToPlay() {
        if (hand.isEmpty()) {
            System.out.println("You have no cards to play.");
            return null;
        }

        displayHand();
        int index = promptForCardIndex("play");
        Card card = playCard(index);
        System.out.println("You played: " + card);
        return card;
    }

    @Override
    public Card chooseCardToDiscard() {
        if (hand.isEmpty()) {
            System.out.println("You have no cards to discard.");
            return null;
        }

        displayHand();
        int index = promptForCardIndex("discard");
        Card card = playCard(index);
        System.out.println("You discarded: " + card);
        return card;
    }

    public String getName() {
        return name;
    }

    // Displays the current hand of the player.
    private void displayHand() {
        System.out.println("Your hand:");
        for (int i = 0; i < hand.size(); i++) {
            System.out.println(i + ": " + hand.get(i));
        }
    }

    // This method asks the player for which card he wants to Play/Discard and
    // ensures that it is a valid index.
    private int promptForCardIndex(String action) {
        int index = -1;
        while (true) {
            System.out.print(name + ", enter the index of the card you want to " + action + ": ");
            String input = scanner.nextLine();
            try {
                index = Integer.parseInt(input);
                if (index >= 0 && index < hand.size()) {
                    return index;
                } else {
                    System.out.println("Invalid index. Please enter a number between 0 and " + (hand.size() - 1) + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

}
