package ui;

import jakarta.websocket.*;

import game.GameServerEndpoint;
import account.Account;

public class MultiplayerUI implements UserInterface {

    private GameServerEndpoint gse;

    public MultiplayerUI(GameServerEndpoint gse) {
        this.gse = gse;
    }

    @Override
    public void displayMessage(String message, Session s) {
        // Send message to the current player's WebSocket
        gse.sendToCurrentPlayer(message, s);
    }

    public void sendAccount(Account account, Session s) {
        gse.sendToCurrentPlayer(account, s);
    }

    @Override
    public void broadcastMessage(String message) {
        GameServerEndpoint.broadcast(message);
    }
    
}