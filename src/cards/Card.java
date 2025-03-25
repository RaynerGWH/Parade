package cards;

import java.io.Serializable;

public class Card implements Serializable{

    // Instance Variables
    private final Color color;
    private final int value;
    
    // Constructors
    public Card(Color color, int value) {
        this.color = color;
        this.value = value;
    }

    // Instance Methods
    public Color getColor() {
        return color;
    }
    
    public int getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return color + "-" + value;
    }
}
