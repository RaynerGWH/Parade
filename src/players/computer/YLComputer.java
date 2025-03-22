package players.computer;

import players.*;
import cards.*;

import java.util.ArrayList;
import java.util.Random;

public class YLComputer extends AbstractPlayer {
    
    private final String NAME = "YL";
    
    //YL bot has no delays, improved AI.
    //Stakes for losing to YL bot increased
    //Stakes for winning YL bot increased.
    //YL only appears at a 1/100 chance.

    public YLComputer(ArrayList<Card> hand) {
        super(hand);
    }


    //overloaded method
    public Card chooseCardToPlay(int paradeLength) {
        if (hand.isEmpty()) {
            System.out.println(NAME + " has no cards to play.");
            return null;
        }
        
        //TODO: card logic!
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
