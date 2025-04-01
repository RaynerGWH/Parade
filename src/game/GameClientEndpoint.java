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
        if (message.contains("-----Your turn-----")) {
            new Thread(() -> {
                System.out.print("Enter your input: ");
                String input = sc.nextLine();
                try {
                    session.getBasicRemote().sendText(input);
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
}
