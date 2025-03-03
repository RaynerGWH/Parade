import java.util.*;

public class Player {

    //to store the player's current hand
    private ArrayList<Card> hand;
    private ArrayList<Card> river;

    //constructor
    public Player(ArrayList<Card> hand) {
        this.hand = hand;
        
        //initialise river
        ArrayList<Card> river = new ArrayList<Card>();
        this.river = river;
    }

    //instance methods

    //Takes in index of card that should be played, returns the card that the player plays(card at that index) PARAMETERS: int, RETURNS: Card
    public Card playCard(int idx) {
        if (hand != null && idx >= 0 && idx < hand.size()) {
            return hand.remove(idx);
        }

        return null;
    }

    //draw card from deck. takes in Card object, adds it to arraylist. PARAMETERS: Card, RETURNS: void
    public void drawCard(Card newCard) {
        hand.add(newCard);
    }

    //add card to river. takes in Card object, adds it to river arraylist. PARAMETERS: Card, RETURNS: void
    public void addToRiver(Card toAdd) {
        river.add(toAdd);

        //sort river to make it neater
        Collections.sort(river, new CardComparator());
    }
}
