package game;


import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import account.*;
import cards.*;



@ServerEndpoint("/game")
public class GameServerEndpoint {

    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;
    private static Listener gameListener;
    private static final Map<Session, Account> SESSIONS = Collections.synchronizedMap(new HashMap<>());

    public static void setListener(Listener listener) {
        gameListener = listener;
    }

    private void forwardCardToGame(Account acc, Card card) {
        if (gameListener != null && acc != null) {
            gameListener.onCardPlayed(acc, card);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        try {
            objOut = new ObjectOutputStream(session.getBasicRemote().getSendStream());
            System.out.println("Connected: " + session.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(byte[] data, Session session) {
        Account acc = SESSIONS.get(session);
        try {
            Object o = objIn.readObject();
            if (o instanceof Account) {
                Account account = (Account)o;
                SESSIONS.put(session,account);
                broadcast(SESSIONS.get(session).getUsername() + " has joined the game.");

            } else if (o instanceof Card) {
                Card c = (Card)o;
                //Send c over to game
                forwardCardToGame(acc, c);
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        try {
            SESSIONS.remove(session);
            System.out.println("Disconnected: " + session.getId());

            // Broadcast a message to all clients
            broadcast("User " + session.getId() + " has left the game.");

            // Close the streams when the session ends
            if (objIn != null) objIn.close();
            if (objOut != null) objOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to broadcast a message to all connected clients
    public static void broadcast(String message) {
        synchronized (SESSIONS) { // Ensure thread safety
            for (Session s : SESSIONS.keySet()) {
                if (s.isOpen()) { // Check if the session is still open
                    try {
                        // Send the message as text
                        s.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("SERVER: " + message); // Print the message to the server console
    }

    public void sendToCurrentPlayer(Object o, Session s) {
        if (s != null && s.isOpen()) {
            try {
                if (o instanceof String) {
                    s.getBasicRemote().sendText((String) o);
                } else {
                    objOut.writeObject(o);
                    objOut.flush();
                }
            } catch (IOException e) {
                System.err.println("Error sending message to current player: " + e.getMessage());
            }
        }
    }

    public static Map<Session, Account> getSessionPlayers() {
        return SESSIONS;
    }

    public static int getNumPlayers() {
        return SESSIONS.size();
    }
}


