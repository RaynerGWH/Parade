package game;

import java.util.Scanner;
import account.*;
import java.io.*;
import jakarta.websocket.*;
import players.human.*;
import ui.MultiplayerUI;
import cards.*;

import java.net.URI;
import java.nio.ByteBuffer;


@ClientEndpoint
public class GameClientEndpoint{
    private HumanPlayer player;
    private Session session;
    private ByteArrayOutputStream baos;
    Scanner sc;

    public void setScanner(Scanner sc) {
        this.sc = sc;
    }

    public GameClientEndpoint(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to server");
        
        Scanner sc = new Scanner(System.in);
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
        System.out.println(message);
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

                if (obj instanceof HumanPlayer) {
                    HumanPlayer p = (HumanPlayer)obj;
                    this.player = p;
                    //flush scanner first
                    sc.nextLine();
                    player.setScanner(sc);
                    

                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(baos)){
                            
                            //Call the player object
                            Card c = player.chooseCardToPlay();

                            oos.writeObject(c);
                            oos.flush();

                            byte[] cardBytes = baos.toByteArray();
                            session.getBasicRemote().sendBinary(ByteBuffer.wrap(cardBytes));
                            
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    byte[] cardBytes = baos.toByteArray();
                    session.getBasicRemote().sendBinary(ByteBuffer.wrap(cardBytes));
                    //Send card back over

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
    }

    public int handleIdx(String input) {
        return Integer.parseInt(input);
    }
}
