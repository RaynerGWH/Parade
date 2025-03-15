import java.util.ArrayList;
import java.util.Collections;

public abstract class AbstractPlayer implements Player {
    // Store the player's current hand.
    protected List<Card> hand;
    
    // Store the player's river (score pile).
    protected List<Card> river;

    // Constructor
    public AbstractPlayer(ArrayList<Card> hand) {
        this.hand = hand;
        this.river = new ArrayList<Card>();
    }

    // Plays a card from the hand at the given index.
    @Override
    public Card playCard(int idx) {
        if (hand != null && idx >= 0 && idx < hand.size()) {
            return hand.remove(idx);
        }
        return null;
    }

    // Draws a card from the deck (adds it to the hand).
    @Override
    public void drawCard(Card newCard) {
        hand.add(newCard);
    }

    // Adds a card to the river and sorts the river.
    @Override
    public void addToRiver(Card toAdd) {
        river.add(toAdd);
        Collections.sort(river, new CardComparator());
    }
    
    // Getter for the hand.
    @Override
    public ArrayList<Card> getHand() {
        return hand;
    }
    
    // Getter for the river.
    @Override
    public ArrayList<Card> getRiver() {
        return river;
    }
}
