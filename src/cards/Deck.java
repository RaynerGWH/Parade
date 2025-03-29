package cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Instance Variables
// Constructors
// Instance Methods

public class Deck {

    // Instance Variables
    private final List<Card> cards;

    // Constructors
    public Deck() {
        cards = new ArrayList<>();
        // Initialize the deck with 66 cards; adjust as needed based on game rules.
        // For each color and value combination, add a new Card.
        for (Color color : Color.values()) {
            for (int value = 0; value <= 10; value++) {
                // Adjust the number of cards per value if needed.
                cards.add(new Card(color, value));
            }
        }
        shuffle();
    }

    // Instance Methods

    // 1. shuffle() method to shuffle the deck of cards.
    public void shuffle() {
        Collections.shuffle(cards);
    }

    // 2. drawCard() to remove cards when a card is drawn.
    public Card drawCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    // Simulates cutting the deck.
    public void cutDeck() {
        if (cards.size() < 2) {
            return; // No need to cut if there are fewer than 2 cards.
        }

        // Determine a random cut index between 1 and size-1.
        int cutIndex = (int) (Math.random() * (cards.size() - 1)) + 1;

        // Split the deck into two parts.
        List<Card> topHalf = new ArrayList<>(cards.subList(0, cutIndex));
        List<Card> bottomHalf = new ArrayList<>(cards.subList(cutIndex, cards.size()));

        // Reassemble the deck by placing the bottom half on top of the top half.
        cards.clear();
        cards.addAll(bottomHalf);
        cards.addAll(topHalf);
    }

    public void clearDeck() {
        cards.clear();
    }
}
