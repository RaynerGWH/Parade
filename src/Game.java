import java.util.ArrayList;
import java.util.List;

public class Game {
    private final List<Player> players;
    
    public Game(int numberOfPlayers) {
        if (numberOfPlayers < 2) {
            throw new IllegalArgumentException("At least 2 players are required to play the game.");
        }
        players = new ArrayList<>();
        // Initialize players and other game components here...
    }
    
    // Other methods...
    public static void main(String[] args) {
        System.out.println("Welcome to the Parade Card Game!");
    }
}
