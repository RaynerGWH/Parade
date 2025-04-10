package ui;

// import java.util.*;
// import cards.*;
// import game.GameState;
// import players.Player;
import jakarta.websocket.*;

public interface UserInterface {
    void displayMessage(String message, Session s);
    void broadcastMessage(String message);
}