package cards;

import java.io.Serializable;

public class Card implements Serializable{

    // Instance Variables
    private final CardColor CARD_COLOR;
    private final int VALUE;
    
    // Constructors
    public Card(CardColor color, int value) {
        this.CARD_COLOR = color;
        this.VALUE = value;
    }

    // Instance Methods
    public CardColor getColor() {
        return CARD_COLOR;
    }
    
    public int getValue() {
        return VALUE;
    }
    
    @Override
    public String toString() {
        return CARD_COLOR + "-" + VALUE;
    }
}