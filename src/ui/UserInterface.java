package ui;

// import java.util.*;
// import cards.*;
// import game.GameState;
// import players.Player;
import jakarta.websocket.*;

public interface UserInterface {
    void displayMessage(String message, Session s);
    void broadcastMessage(String message);
    // void displayGameState(GameState state, Player currentPlayer);
    // void displayCountdown();
    // void displayCardAction(Player player, Card card, String action);
    // void displayTurnHeader(Player player);
    // void displayGameOverMessage();
    // void displayFinalScores(TreeMap<Integer, ArrayList<Player>> scores);
}