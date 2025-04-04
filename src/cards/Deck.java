package cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Instance Variables
// Constructors
// Instance Methods

public class Deck {

    // Instance Variables
    private final List<Card> CARDS;

    // Constructors
    public Deck() {
        CARDS = new ArrayList<>();
        // Initialize the deck with 66 cards; adjust as needed based on game rules.
        // For each color and value combination, add a new Card.
        for (Color color : Color.values()) {
            for (int value = 0; value <= 10; value++) {
                // Adjust the number of cards per value if needed.
                CARDS.add(new Card(color, value));
            }
        }
        shuffle();
    }

    // Instance Methods

    // 1. shuffle() method to shuffle the deck of cards.
    public void shuffle() {
        Collections.shuffle(CARDS);
    }

    // 2. drawCard() to remove cards when a card is drawn.
    public Card drawCard() {
        if (CARDS.isEmpty()) {
            return null;
        }
        return CARDS.remove(0);
    }

    public boolean isEmpty() {
        return CARDS.isEmpty();
    }

    public void clearDeck() {
        CARDS.clear();
    }
}
