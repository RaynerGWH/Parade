package game;

import account.*;
import constants.UIConstants;

import java.io.*;
import jakarta.websocket.*;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.swing.UIClientPropertyKey;

@ClientEndpoint
public class GameClientEndpoint{
    private Session session;
    private Scanner sc;
    private CountDownLatch latch;
    private AccountFileManager acctMgr = new AccountFileManager();
    private boolean isShuttingDown = false;


    public GameClientEndpoint(URI endpointURI, Scanner sc) throws DeploymentException, IOException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, endpointURI);
        this.sc = sc;
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to server");

        // Instead of creating a LoginUI here (which causes NullPointerException),
        // load the existing account or create one if it doesn't exist
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos)){
            // Convert Account to byte array for sending
            Account currAccount = LoginManager.getCurrentAccount();

            System.out.println(currAccount.toString());

            oos.writeObject(currAccount);
            oos.flush();

            byte[] accountBytes = baos.toByteArray();
            session.getBasicRemote().sendBinary(ByteBuffer.wrap(accountBytes));
            
            System.out.println("Sent account: " + currAccount.getUsername());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread inputThread = new Thread(new InputReader());
        inputThread.setDaemon(true);  // So it won't block shutdown
        inputThread.start();
    }

    @OnMessage
    public void onMessage(String message) {
        // Check if this is a new turn marker and clear any pending input
        if (message.contains("TURN") && message.contains("===============")) {
            InputManager.clearInput();
            return;
        }

        if (message.contains("Your turn! Number of cards:") || message.contains("Your turn to discard! Number of cards:")) {
            // Handle the number of cards prompt
            String[] messageArr = message.split(":");
            int numCards = Integer.parseInt(messageArr[messageArr.length - 1].trim());
            
            new Thread(() -> {
                String input;
                int choice = -1;
                
                System.out.println(message);
                
                if (message.contains("discard")) {
                    System.out.print("Enter the position of the card you want to discard: " + UIConstants.ConsoleInput);
                } else {
                    System.out.print("Enter your input" + UIConstants.ConsoleInput);
                }
                
                try {
                    input = InputManager.waitForInputWithTimeout(30, TimeUnit.SECONDS);
                    
                    // Handle timeout case
                    if (input == null) {
                        System.out.println("\nTime's up! Using default action.");
                        session.getBasicRemote().sendText("0");
                        return;
                    }
                    
                    // Validate and parse input
                    if (!isNumeric(input)) {
                        System.out.println("❌ Invalid input (not a number). Using default action (0).");
                        session.getBasicRemote().sendText("0");
                        return;
                    }

                    int handSize = numCards;
                    choice = Integer.parseInt(input);

                    if (choice >= 0 && choice < handSize) {
                        session.getBasicRemote().sendText(String.valueOf(choice));
                    } else {
                        System.out.println("\n❌ Invalid choice. Using default action (0).");
                        session.getBasicRemote().sendText("0");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } else if (message.contains("[ENTER] End Turn")) {
            // Handle the turn advancement prompt
            System.out.println(message);
            
            new Thread(() -> {
                System.out.println("[ENTER] Continue (30s)");
                
                try {
                    // Wait for ENTER with timeout
                    String input = InputManager.waitForInputWithTimeout(30, TimeUnit.SECONDS);
                    
                    // If timeout occurred, auto-advance
                    if (input == null) {
                        System.out.println("\nAuto-advancing turn...");
                    } else {
                        // User pressed ENTER
                        clearTurnConsole();
                    }
                    
                    // In either case, send the end-turn signal
                    session.getBasicRemote().sendText("");
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } else if (message.contains("Any player can hit ENTER to continue...")) {
            // Handle the prompt allowing any player to hit ENTER to continue
            System.out.println(message);
            
            new Thread(() -> {
                System.out.println("[ENTER] Continue (30s)");
                
                try {
                    // Wait with timeout
                    String input = InputManager.waitForInputWithTimeout(30, TimeUnit.SECONDS);
                    
                    // Handle timeout or input
                    if (input == null) {
                        System.out.println("\nAuto-continuing...");
                    } else {
                        clearTurnConsole();
                    }
                    
                    // Send empty string to signal continuation
                    session.getBasicRemote().sendText("");
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            System.out.println(message);
        }
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
                    acctMgr.save(account);
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
        System.out.println("Disconnected from server");
        if (!isShuttingDown && latch != null && latch.getCount() > 0) {
            latch.countDown();
        }
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    /**
     * Clears the console for better readability between turns
     */
    private void clearTurnConsole() {
        try {
            final String os = System.getProperty("os.name");
            
            if (os.contains("Windows")) {
                // For Windows, try multiple methods
                try {
                    // First attempt: using cls command
                    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                } catch (Exception e) {
                    // Second attempt: using ANSI escape codes (works in newer Windows terminals)
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                }
            } else {
                // For Unix-based systems
                System.out.print("\033[H\033[2J");
                System.out.flush();
                
                // Alternative approach for Unix
                System.out.print("\033c");
            }
        } catch (Exception e) {
            // If all clearing methods fail, print multiple newlines as fallback
            for (int i = 0; i < 100; i++) {
                System.out.println();
            }
        }
        
        // Don't print the separator here - we'll get it from the server
    }

    public void shutdown() {
        isShuttingDown = true;
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (Exception e) {
                // Ignore errors during shutdown.
            }
        }
        // Ensure the latch is released so any waiting threads are unblocked.
        if (latch != null && latch.getCount() > 0) {
            latch.countDown();
        }
    }

    private static boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) return false;
        try {
            Integer.parseInt(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }    
}
