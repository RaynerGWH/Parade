import java.util.*;

/**
 * Represents a player with a name, a hand of cards, a list of collected cards, and a score.
 */
class Player {
    private final String name;
    private final List<Card> hand;
    private final List<Card> collectedCards;
    private int score;

    // ANSI color codes for card display
    private static final Map<Color, String> colorMap = new HashMap<>();

    static {
        colorMap.put(Color.RED, "\u001B[31m");
        colorMap.put(Color.BLUE, "\u001B[34m");
        colorMap.put(Color.GREEN, "\u001B[32m");
        colorMap.put(Color.YELLOW, "\u001B[33m");
        colorMap.put(Color.PURPLE, "\u001B[35m");
        colorMap.put(Color.ORANGE, "\u001B[36m");
    }

    private static final String ANSI_RESET = "\u001B[0m";

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.collectedCards = new ArrayList<>();
        this.score = 0;
    }

    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return hand;
    }

    public List<Card> getCollectedCards() {
        return collectedCards;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Displays the hand of cards, color-coded.
     */
    public void showHand() {
        for (int i = 0; i < hand.size(); i++) {
            final Card card = hand.get(i);
            String colorCode = colorMap.get(card.getColor());
            if (colorCode == null) {
                colorCode = ""; // fallback no color
            }
            System.out.println("[" + i + "] " + colorCode + card + ANSI_RESET);
        }
    }
}