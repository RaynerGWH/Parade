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
            int numCards = Integer.parseInt(messageArr[messageArr.length - 1]);
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
