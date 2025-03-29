package players.human;

import cards.*;
import players.AbstractPlayer;

import java.util.ArrayList;
import java.util.Scanner;

import jakarta.websocket.*;

public class HumanPlayer extends AbstractPlayer {
    private String name;
    private transient Session session;
    private Scanner sc;

    public HumanPlayer(ArrayList<Card> hand, String name, Session session, Scanner sc) {
        super(hand);
        this.name = name;
        this.session = session;
        this.sc = sc;
    }

    //Singleplayer handler
    @Override
    public Card chooseCardToPlay() {
        handleCardSelection("play");
        int index = -1;
        while (true) {
            String input = sc.nextLine();
            try {
                index = Integer.parseInt(input);
                if (index >= 0 && index < hand.size()) {
                    return playCard(index);
                } else {
                    System.out.println("Invalid index. Please enter a number between 0 and " + (hand.size() - 1) + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
    
    // Singleplayer handler
    @Override
    public Card chooseCardToDiscard() {
        handleCardSelection("discard");
        int index = -1;
        while (true) {
            String input = sc.nextLine();
            try {
                index = Integer.parseInt(input);
                if (index >= 0 && index < hand.size()) {
                    return playCard(index);
                } else {
                    System.out.println("Invalid index. Please enter a number between 0 and " + (hand.size() - 1) + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private void handleCardSelection(String action) {
        if (hand.isEmpty()) {
            System.out.println("You have no cards to " + action + ".");
            return;
        }
        
        promptForCardIndex(action);
    }

    public void displayHand() {

        // We check if the hand is null or empty
        if (hand == null || hand.isEmpty()) {
            System.out.println("Your hand: Empty");
        } else {
            System.out.println("Your hand:");
            CardPrinter.printCardRow(hand, false);
        }

    }

    private void promptForCardIndex(String action) {
        String prompt = name + ", enter the position of the card you want to " + action + " (0 - 4): ";
        System.out.print(prompt);
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