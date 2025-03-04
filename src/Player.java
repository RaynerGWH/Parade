import java.util.ArrayList;

public interface Player {
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
}

// import java.util.*;

// public class Player {

//     //to store the player's current hand
//     private ArrayList<Card> hand;
//     private ArrayList<Card> river;

//     //constructor
//     public Player(ArrayList<Card> hand) {
//         this.hand = hand;
        
//         //initialise river
//         ArrayList<Card> river = new ArrayList<Card>();
//         this.river = river;
//     }

//     //instance methods

//     //Takes in index of card that should be played, returns the card that the player plays(card at that index) PARAMETERS: int, RETURNS: Card
//     public Card playCard(int idx) {
//         if (hand != null && idx >= 0 && idx < hand.size()) {
//             return hand.remove(idx);
//         }

//         return null;
//     }

//     //draw card from deck. takes in Card object, adds it to arraylist. PARAMETERS: Card, RETURNS: void
//     public void drawCard(Card newCard) {
//         hand.add(newCard);
//     }

//     //add card to river. takes in Card object, adds it to river arraylist. PARAMETERS: Card, RETURNS: void
//     public void addToRiver(Card toAdd) {
//         river.add(toAdd);

//         //sort river to make it neater
//         Collections.sort(river, new CardComparator());
//     }
// }
