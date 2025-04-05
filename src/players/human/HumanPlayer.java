package players.human;

import cards.*;
import players.AbstractPlayer;
import account.Account;  // Import the Account class

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import jakarta.websocket.*;

public class HumanPlayer extends AbstractPlayer {
    private String name;
    private transient Session session;
    private Scanner sc;
    
    // New field to hold the associated Account
    private Account account;

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
        int index = -1;
        while (true) {
            String input = sc.nextLine();
            try {
                index = Integer.parseInt(input) - 1;
                if (index >= 0 && index < hand.size()) {
                    return playCard(index);
                } else {
                    System.out.print("Invalid index. Please enter a number between 1 and " + hand.size() + "\n> ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number\n> ");
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
                index = Integer.parseInt(input) - 1;
                if (index >= 0 && index < hand.size()) {
                    return playCard(index);
                } else {
                    System.out.print("Invalid index. Please enter a number between 1 and " + hand.size() + "\n> ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid number\n> ");
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

    private void promptForCardIndex(String action) {
        String displayName = name;
        if (account != null) {
            List<String> flairs = account.getUnlockedFlairs();
            if (flairs != null && !flairs.isEmpty()) {
                displayName = account.getUsername() + " [" + flairs.get(0) + "]";
            } else {
                displayName = account.getUsername();
            }
        }
        
        String prompt = displayName + ", enter the position of the card you want to " + action + " (1 - 5)\n> ";
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
