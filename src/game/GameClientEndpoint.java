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

    public GameClientEndpoint(URI endpointURI, Scanner sc) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, endpointURI);
            this.sc = sc;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to server");
        AccountFileManager acctMgr = new AccountFileManager(sc);
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

    @OnClose
    public void onClose(Session session) {
        System.out.println("Disconnected from server");
        latch.countDown();
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }
}
