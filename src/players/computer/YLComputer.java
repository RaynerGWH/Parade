package players.computer;

import players.*;
import cards.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class YLComputer extends AbstractPlayer {
    
    private final String NAME = "YL";
    
    //YL bot has no delays, improved AI.
    //Stakes for losing to YL bot increased
    //Stakes for winning YL bot increased.
    //YL only appears at a 1/100 chance.

    public YLComputer(ArrayList<Card> hand) {
        super(hand);
    }

    @Override
    public Card chooseCardToPlay() {
        if (hand.isEmpty()) {
            System.out.println(NAME + " has no cards to play.");
            return null;
        }

        // Always prioritises the card with the highest value
        //sort hand
        Collections.sort(hand, Comparator.comparing(Card::getValue));

        int index = hand.size() - 1;
        Card card = playCard(index);
        System.out.println(NAME + " (Intermediate) plays: " + card);
        return card;
    }

    //overloaded method
    public Card chooseCardToPlay(int paradeLength) {
        if (hand.isEmpty()) {
            System.out.println(NAME + " has no cards to play.");
            return null;
        }
        
        //TODO: card logic!
    }

    @Override
    public Card chooseCardToDiscard() {
        if (hand.isEmpty()) {
            System.out.println(NAME + " has no cards to play.");
            return null;
        }

        Collections.sort(hand, Comparator.comparing(Card::getValue));

        // Will discard cards with the highest value
        int index = hand.size() - 1;
        Card card = playCard(index);
        System.out.println(NAME + " (Intermediate) discards: " + card);
        CardPrinter.printCardRow(card, false);
        return card;
    }


    //overloaded method
    public Card chooseCardToDiscard(ArrayList<Card> allRivers) {
        if (hand.isEmpty()) {
            System.out.println(NAME + " has no cards to play.");
            return null;
        }

        //YL looks at the river of every player, checks to see if he will be the majority holder if he adds cards of a particular color
        //else, he will discard the max value card
        //TODO: discard logic!

    }

    public String getName() {
        return NAME;
    }
}
