import java.util.Comparator;

public class CardComparator implements Comparator<Card> {

    public CardComparator(){};
    
    //Compare method. sorts them by color first(purple->blue->red->orange->grey->green), then face value(ascending).
    @Override
    public int compare(Card c1, Card c2) {
        int res = 0;
        int c1Score = 0;
        int c2Score = 0;

        switch (c1.getColor()) {
            case PURPLE:
                c1Score = 1;
                break;

            case BLUE:
                c1Score = 2;
                break;

            case RED:
                c1Score = 3;
                break;

            case ORANGE:
                c1Score = 4;
                break;

            case GREY:
                c1Score = 5;
                break;

            case GREEN:
                c1Score = 6;
                break;
        }

        switch (c2.getColor()) {
            case PURPLE:
                c2Score = 1;
                break;

            case BLUE:
                c2Score = 2;
                break;

            case RED:
                c2Score = 3;
                break;

            case ORANGE:
                c2Score = 4;
                break;

            case GREY:
                c2Score = 5;
                break;

            case GREEN:
                c2Score = 6;
                break;
        }

        res = c1Score - c2Score;
        if (res != 0) {
            return res;
        }

        res = c1.getValue() - c2.getValue();
        return res;
    }
}
