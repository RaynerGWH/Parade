package ui;

import jakarta.websocket.*;

public interface UserInterface {
    void displayMessage(String message, Session s);
    void broadcastMessage(String message);
}