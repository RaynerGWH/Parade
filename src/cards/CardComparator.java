package cards;

import java.util.Comparator;

public class CardComparator implements Comparator<Card> {
    
    //Compare method. sorts them by color first(purple->blue->red->orange->grey->green), then face value(ascending).
    @Override
    public int compare(Card c1, Card c2) {

        int colorDifference = 0;

        colorDifference = getColorScore(c1.getColor()) - getColorScore(c2.getColor());

        if (colorDifference != 0) {
            return colorDifference;
        }

        // If the two card's colors are the same, we will then compare the values of the two cards.
        return c1.getValue() - c2.getValue();
    }

    private int getColorScore(CardColor card_color) {
        switch (card_color) {
            case PURPLE: return 1;
            case BLUE: return 2;
            case RED: return 3;
            case ORANGE: return 4;
            case GREY: return 5;
            case GREEN: return 6;
            default: throw new IllegalArgumentException("Unexpected color: " + card_color);
        }
    }
}
