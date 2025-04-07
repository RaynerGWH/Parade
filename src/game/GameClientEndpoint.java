package game;

import account.*;
import java.io.*;
import jakarta.websocket.*;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CountDownLatch;


@ClientEndpoint
public class GameClientEndpoint{
    private Session session;
    private Scanner sc;
    private CountDownLatch latch;
    private AccountFileManager acctMgr = new AccountFileManager();

    public GameClientEndpoint(URI endpointURI, Scanner sc) throws DeploymentException, IOException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, endpointURI);
        this.sc = sc;
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to server");
        Account a = acctMgr.initialize();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos)){
            // Convert Account to byte array for sending
            oos.writeObject(a);
            oos.flush();

            byte[] accountBytes = baos.toByteArray();
            session.getBasicRemote().sendBinary(ByteBuffer.wrap(accountBytes));
            
            System.out.println("Sent account: " + a.getUsername());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message) {
        // Check if this is a new turn marker and clear the console
        if (message.contains("TURN") && message.contains("===============")) {
            System.out.println(message);
            return;
        }
    
        if (message.contains("Your turn! Number of cards:") || message.contains("Your turn to discard! Number of cards:")) {
            //handle number of cards
            String[] messageArr = message.split(":");
            int numCards = Integer.parseInt(messageArr[messageArr.length - 1]);
            new Thread(() -> {
                String input;
                int choice = -1;
                while (true) {
                    if (message.contains("discard")) {
                        System.out.print("Enter the position of the card you want to discard: ");
                    } else {
                        System.out.print("Enter your input: ");
                    }
                    input = sc.nextLine();

                    try {
                        choice = Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a number.");
                        continue;
                    }

                    int handSize = numCards;
                    if (choice >= 0 && choice < handSize) {
                        break;
                    } else {
                        System.out.println("Invalid choice. Enter a number between 0 and " + (handSize - 1) + ".");
                    }
                }

                try {
                    session.getBasicRemote().sendText(String.valueOf(choice));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } else if (message.contains("Hit \"ENTER\" to end turn!")) {
            // Handle the turn advancement prompt
            System.out.println(message);
            new Thread(() -> {
                System.out.print("Press ENTER to end your turn...");
                sc.nextLine(); // Wait for ENTER key
                
                // Clear the console immediately when the user presses Enter
                clearConsole();
                
                try {
                    // Send empty string to indicate ENTER key press
                    session.getBasicRemote().sendText("");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } else if (message.contains("Any player can hit ENTER to continue...")) {
            // Handle the bot turn advancement prompt
            System.out.println(message);
            new Thread(() -> {
                System.out.print("Press ENTER to continue...");
                sc.nextLine(); // Wait for ENTER key
                
                // Clear the console immediately when the user presses Enter
                clearConsole();
                
                try {
                    // Send empty string to indicate ENTER key press
                    session.getBasicRemote().sendText("");
                } catch (IOException e) {
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
        latch.countDown();
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    /**
     * Clears the console for better readability between turns
     */
    private void clearConsole() {
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
}
=======
package game;

import account.*;
import java.io.*;
import jakarta.websocket.*;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CountDownLatch;


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
        Account a = acctMgr.initialize();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos)){
            // Convert Account to byte array for sending
            oos.writeObject(a);
            oos.flush();

            byte[] accountBytes = baos.toByteArray();
            session.getBasicRemote().sendBinary(ByteBuffer.wrap(accountBytes));
            
            System.out.println("Sent account: " + a.getUsername());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message) {
        if (message.contains("Your turn! Number of cards:")) {
            //handle number of cards
            String[] messageArr = message.split(":");
            int numCards = Integer.parseInt(messageArr[messageArr.length - 1].trim());
            new Thread(() -> {
                String input;
                int choice = -1;
                while (true) {
                    System.out.print("Enter your input: ");
                    input = sc.nextLine();

                    try {
                        choice = Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a number.");
                        continue;
                    }

                    int handSize = numCards;
                    if (choice >= 0 && choice < handSize) {
                        break;
                    } else {
                        System.out.println("Invalid choice. Enter a number between 0 and " + (handSize - 1) + ".");
                    }
                }

                try {
                    session.getBasicRemote().sendText(String.valueOf(choice));
                } catch (IOException e) {
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

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }
}
