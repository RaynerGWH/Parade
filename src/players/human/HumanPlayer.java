package players.human;

import cards.*;
import players.AbstractPlayer;

import java.util.ArrayList;

import jakarta.websocket.*;

public class HumanPlayer extends AbstractPlayer {
    private String name;
    private transient Session session;

    public HumanPlayer(ArrayList<Card> hand, String name, Session session) {
        super(hand);
        this.name = name;
        this.session = session;
    }

    //Not used by the multiplayer game
    @Override
    public Card chooseCardToPlay() {
        handleCardSelection("play");
        return null;
    }
    
    //Not used by multiplayer game
    @Override
    public Card chooseCardToDiscard() {
        handleCardSelection("discard");
        return null;
    }

    private void handleCardSelection(String action) {
        if (hand.isEmpty()) {
            System.out.println("You have no cards to " + action + ".");
            return;
        }

        // displayHand();
        promptForCardIndex(action);
    }

    public String displayHand() {
        String res = "Your hand:";
        for (int i = 0; i < hand.size(); i++) {
            res += i + ": " + hand.get(i);
        }

        return res;
    }

    private void promptForCardIndex(String action) {
        String prompt = name + ", enter the index of the card you want to " + action + ": ";
        System.out.println(prompt);
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