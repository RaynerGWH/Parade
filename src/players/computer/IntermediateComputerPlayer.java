package players.computer;

import cards.*;
import players.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.lang.Thread;
import java.lang.InterruptedException;

public class IntermediateComputerPlayer extends AbstractPlayer {
    private String name;
    private final int ACTION_DELAY = 250; //hard coded action delay

    public IntermediateComputerPlayer(ArrayList<Card> hand, String name) {
        super(hand);
        this.name = name;
    }

    @Override
    public Card chooseCardToPlay() {
        try {
            Thread.sleep(ACTION_DELAY);
            if (hand.isEmpty()) {
                System.out.println(name + " has no cards to play.");
                return null;
            }
    
            // Always prioritises the card with the highest value
            //sort hand
            Collections.sort(hand, Comparator.comparing(Card::getValue));
    
            int index = hand.size() - 1;
            Card card = playCard(index);
            System.out.println(name + " (Intermediate) plays: " + card);
            return card;
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Card chooseCardToDiscard() {
        if (hand.isEmpty()) {
            System.out.println(name + " has no cards to play.");
            return null;
        }

        Collections.sort(hand, Comparator.comparing(Card::getValue));

        // Will discard cards with the highest value
        int index = hand.size() - 1;
        Card card = playCard(index);
        System.out.println(name + " (Intermediate) discards: " + card);
        return card;
    }

    public String getName() {
        return name;
    }
}
