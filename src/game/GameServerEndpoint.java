package game;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.*;
import account.*;

@ServerEndpoint("/game")
public class GameServerEndpoint {
    private static final Map<Session, Account> SESSIONS = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket connection opened: " + session.getId());
        // Ensure the session is ready for communication
        session.setMaxIdleTimeout(0); // Disable idle timeout
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        InputManager.offerInput(message);
    }

    @OnMessage
    public void onMessage(Session session, ByteBuffer byteBuffer) {
        try {
            // Convert ByteBuffer to byte array
            byte[] data = new byte[byteBuffer.remaining()];
            byteBuffer.get(data);
    
            // Deserialize the object from the byte array
            try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
                 ObjectInputStream ois = new ObjectInputStream(bais)) {
                
                Object obj = ois.readObject();
    
                if (obj instanceof Account) {
                    Account originalAccount = (Account) obj;
                    String originalUsername = originalAccount.getUsername();
                    
                    // Ensures unique usernames for all players joining the game. 
                    String uniqueUsername = originalUsername;
                    int counter = 1;
    
                    // Check if this username already exists in any session
                    boolean isDuplicate = true;
                    while (isDuplicate) {
                        isDuplicate = false;
                        for (Account existingAccount : SESSIONS.values()) {
                            if (existingAccount.getUsername().equals(uniqueUsername)) {
                                uniqueUsername = originalUsername + "_" + counter++;
                                isDuplicate = true;
                                break;
                            }
                        }
                    }
                    
                    // Create a new account with the unique username upon duplicate name joining
                    Account accountToStore;
                    if (!uniqueUsername.equals(originalUsername)) {
                        // Create new account with unique username and copy properties
                        accountToStore = new Account(
                            originalAccount.getId(),
                            uniqueUsername,
                            originalAccount.getWins(),
                            originalAccount.getLosses(),
                            originalAccount.getBalance(),
                            new ArrayList<>(originalAccount.getUnlockedFlairs())
                        );
                    } else {
                        accountToStore = originalAccount;
                    }
                    
                    SESSIONS.put(session, accountToStore);
                    System.out.println("Account received: " + accountToStore.getUsername());
                    
                    // Broadcast with explicit error handling
                    broadcast(accountToStore.getUsername() + " has joined the game.");
                    System.out.println("Type \"START\" to start the game");
                } else {
                    System.out.println("Received unknown object type: " + obj.getClass().getName());
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    

    @OnClose
    public void onClose(Session session) {
        Account removedAccount = SESSIONS.remove(session);
        System.out.println("WebSocket connection closed: " + session.getId());
        
        if (removedAccount != null) {
            broadcast(removedAccount.getUsername() + " has left the game.");
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error for session " + session.getId());
        throwable.printStackTrace();
    }

    // Robust broadcast method with detailed error handling
    public static void broadcast(String message) {
        System.out.println("Total active sessions: " + SESSIONS.size());

        for (Map.Entry<Session, Account> entry : SESSIONS.entrySet()) {
            Session session = entry.getKey();
            Account account = entry.getValue();

            try {
                if (session != null && session.isOpen()) {
                    // Use sendText with a try-catch for each individual session
                    try {
                        session.getBasicRemote().sendText(message);
                        System.out.println("Broadcast sent to: " + 
                            (account != null ? account.getUsername() : "Unknown Account"));
                    } catch (IOException e) {
                        System.err.println("Failed to send broadcast to session: " + session.getId());
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Session is null or closed: " + 
                        (session != null ? session.getId() : "null session"));
                }
            } catch (Exception e) {
                System.err.println("Unexpected error in broadcast loop");
                e.printStackTrace();
            }
        }
    }

    // Method to send message to a specific session
    public void sendToCurrentPlayer(Object message, Session session) {
        if (session == null) {
            System.err.println("Attempt to send message to null session");
            return;
        }

        try {
            if (session.isOpen()) {
                if (message instanceof String) {
                    // Send text message
                    session.getBasicRemote().sendText((String) message);
                } else {
                    // Send serialized object
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(message);
                    oos.flush();

                    byte[] bytes = baos.toByteArray();
                    session.getBasicRemote().sendBinary(ByteBuffer.wrap(bytes));
                    System.out.println("Sent object message to session: " + session.getId());
                }
            } else {
                System.err.println("Cannot send message. Session is closed: " + session.getId());
            }
        } catch (IOException e) {
            System.err.println("Error sending message to session: " + session.getId());
            e.printStackTrace();
        }
    }

    public static Map<Session, Account> getSessionPlayers() {
        return SESSIONS;
    }

    public static int getNumPlayers() {
        return SESSIONS.size();
    }
}

