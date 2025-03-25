// package game;


// import jakarta.websocket.*;
// import jakarta.websocket.server.ServerEndpoint;

// import java.io.*;
// import java.nio.ByteBuffer;
// import java.util.Collections;
// import java.util.HashMap;
// import java.util.Map;

// import account.*;
// import cards.*;



// @ServerEndpoint("/game")
// public class GameServerEndpoint {

//     private ObjectOutputStream objOut;
//     private ObjectInputStream objIn;
//     private static Listener gameListener;
//     private static final Map<Session, Account> SESSIONS = Collections.synchronizedMap(new HashMap<>());

//     public static void setListener(Listener listener) {
//         gameListener = listener;
//     }

//     private void forwardCardToGame(Account acc, Card card) {
//         if (gameListener != null && acc != null) {
//             gameListener.onCardPlayed(acc, card);
//         }
//     }

//     @OnOpen
//     public void onOpen(Session session) {
//         try {
//             objOut = new ObjectOutputStream(session.getBasicRemote().getSendStream());
//             System.out.println("Connected: " + session.getId());
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     @OnMessage
//     public void onMessage(Session session, ByteBuffer byteBuffer) {
//         // System.out.println(SESSIONS.toString());
//         // System.out.println(session.toString());
//         try {
//             // Convert ByteBuffer to byte array
//             byte[] data = new byte[byteBuffer.remaining()];
//             byteBuffer.get(data);

//             // Deserialize the object from the byte array
//             try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
//                  ObjectInputStream ois = new ObjectInputStream(bais)) {
                
//                 Object obj = ois.readObject();

//                 if (obj instanceof Account) {
//                     Account account = (Account) obj;
//                     SESSIONS.put(session, account);
//                     System.out.println("Account received: " + account.getUsername());
                    
//                     // Safely broadcast without throwing exceptions
//                     broadcast(account.getUsername() + " has joined the game.");
//                 } else if (obj instanceof Card) {
//                     Card card = (Card) obj;
//                     Account account = SESSIONS.get(session);
                    
//                     if (account != null) {
//                         forwardCardToGame(account, card);
//                         System.out.println("Card received from: " + account.getUsername());
//                     } else {
//                         System.out.println("Card received from unknown session");
//                     }
//                 } else {
//                     System.out.println("Received unknown object type: " + obj.getClass().getName());
//                 }
//             }
//         } catch (IOException | ClassNotFoundException e) {
//             System.err.println("Error processing message: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }

//     @OnClose
//     public void onClose(Session session) {
//         try {
//             SESSIONS.remove(session);
//             System.out.println("Disconnected: " + session.getId());

//             // Broadcast a message to all clients
//             broadcast("User " + session.getId() + " has left the game.");

//             // Close the streams when the session ends
//             if (objIn != null) objIn.close();
//             if (objOut != null) objOut.close();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     public static void broadcast(String message) {
//         synchronized (SESSIONS) {
//             for (Session s : SESSIONS.keySet()) {
//                 try {
//                     if (s.isOpen()) {
//                         s.getBasicRemote().sendText(message);
//                         System.out.println("Broadcasted: " + message);
//                     }
//                 } catch (Exception e) {
//                     System.out.println(message);
//                     System.err.println("Failed to broadcast to a session: " + e.getMessage());
//                 }
//             }
//         }
//     }

//     public void sendToCurrentPlayer(Object o, Session s) {
//         if (s != null && s.isOpen()) {
//             try {
//                 if (o instanceof String) {
//                     s.getBasicRemote().sendText((String) o);
//                 } else {
//                     objOut.writeObject(o);
//                     objOut.flush();
//                 }
//             } catch (Exception e) {
//                 System.out.println(o.toString());
//                 System.err.println("Error sending message to current player: " + e.getMessage());
//             }
//         }
//     }

//     public static Map<Session, Account> getSessionPlayers() {
//         return SESSIONS;
//     }

//     public static int getNumPlayers() {
//         return SESSIONS.size();
//     }
// }

package game;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import account.*;
import cards.*;

@ServerEndpoint("/game")
public class GameServerEndpoint {
    // Use ConcurrentHashMap for better thread safety
    private static final Map<Session, Account> SESSIONS = new ConcurrentHashMap<>();
    private static Listener gameListener;

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
        System.out.println("WebSocket connection opened: " + session.getId());
        // Ensure the session is ready for communication
        session.setMaxIdleTimeout(0); // Disable idle timeout
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        System.out.println("Received text message: " + message);
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
                    Account account = (Account) obj;
                    SESSIONS.put(session, account);
                    System.out.println("Account received: " + account.getUsername());
                    
                    // Broadcast with explicit error handling
                    broadcast(account.getUsername() + " has joined the game.");
                } else if (obj instanceof Card) {
                    Card card = (Card) obj;
                    Account account = SESSIONS.get(session);
                    
                    if (account != null) {
                        System.out.println("Card received from: " + account.getUsername());
                        forwardCardToGame(account, card);
                    } else {
                        System.out.println("Card received from unknown session");
                    }
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
        System.out.println("Attempting to broadcast: " + message);
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
                    System.out.println("Sent text message to session: " + session.getId());
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

