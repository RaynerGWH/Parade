package players;

import cards.*;

import java.io.Serializable;
import java.util.ArrayList;


public interface Player extends Serializable {
    // Plays a card from the hand at the given index.
    Card playCard(int idx);
    
    // Draws a card (adds a card to the hand).
    void drawCard(Card newCard);
    
    // Adds a card to the player's river (score pile) and sorts it.
    void addToRiver(Card toAdd);
    
    // Returns the player's current hand.
    ArrayList<Card> getHand();
    
    // Returns the player's river.
    ArrayList<Card> getRiver();

    String getName();

    // Return card to play
    public abstract Card chooseCardToPlay();

    // Return card to discard
    public abstract Card chooseCardToDiscard();
}
