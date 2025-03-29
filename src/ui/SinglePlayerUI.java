package ui;

import jakarta.websocket.*;

public class SinglePlayerUI implements UserInterface {

    //In singleplayer, session = null.

    @Override
    public void displayMessage(String message, Session s) {
        System.out.println(message);
    }

    @Override
    public void broadcastMessage(String message) {
        this.displayMessage(message, null);
    }
}