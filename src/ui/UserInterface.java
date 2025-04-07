package ui;

import jakarta.websocket.*;
import players.Player;

public interface UserInterface {
    void displayMessage(String message, Session s);
    void broadcastMessage(String message);
}