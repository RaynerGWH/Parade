package cards;

import java.io.Serializable;

public class Card implements Serializable{

    // Instance Variables
    private final Color COLOR;
    private final int VALUE;
    
    // Constructors
    public Card(Color color, int value) {
        this.COLOR = color;
        this.VALUE = value;
    }

    // Instance Methods
    public Color getColor() {
        return COLOR;
    }
    
    public int getValue() {
        return VALUE;
    }
    
    @Override
    public String toString() {
        return COLOR + "-" + VALUE;
    }
}
