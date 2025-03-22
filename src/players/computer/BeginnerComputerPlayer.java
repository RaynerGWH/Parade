package players.computer;

import players.*;
import cards.*;

import java.util.ArrayList;
import java.util.Random;
import java.lang.Thread;
import java.lang.InterruptedException;

public class BeginnerComputerPlayer extends AbstractPlayer {
    private Random random;
    private String name;
    private final int ACTION_DELAY = 500; //hard coded pause value(for the blitz gamemodes)

    public BeginnerComputerPlayer(ArrayList<Card> hand, String name) {
        super(hand);
        this.name = name;
        this.random = new Random();
    }

    @Override
    public Card chooseCardToPlay() {
        try {
            Thread.sleep(ACTION_DELAY);
            if (hand.isEmpty()) {
                System.out.println(name + " has no cards to play.");
                return null;
            }
            // Completely random selection
            int index = random.nextInt(hand.size());
            Card card = playCard(index);
            System.out.println(name + " (Beginner) plays: " + card);
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
        // Completely random selection
        int randomCardIndex = random.nextInt(hand.size());
        Card card = playCard(randomCardIndex);
        System.out.println(name + " (Beginner) discards: " + card);
        return card;
    }

    public String getName() {
        return name;
    }
}
