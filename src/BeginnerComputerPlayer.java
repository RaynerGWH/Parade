import java.util.ArrayList;
import java.util.Random;

public class BeginnerComputerPlayer extends AbstractPlayer {
    private Random random;
    private String name;

    public BeginnerComputerPlayer(ArrayList<Card> hand, String name) {
        super(hand);
        this.name = name;
        this.random = new Random();
    }

    @Override
    public Card chooseCardToPlay() {
        if (hand.isEmpty()) {
            System.out.println(name + " has no cards to play.");
            return null;
        }
        // Completely random selection
        int index = random.nextInt(hand.size());
        Card card = playCard(index);
        System.out.println(name + " (Beginner) plays: " + card);
        return card;
    }

    public String getName() {
        return name;
    }
}
