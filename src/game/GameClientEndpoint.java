package game;

import java.util.Scanner;
import account.*;
import java.io.*;
import jakarta.websocket.*;
import players.human.*;
import cards.*;

import java.net.URI;

@ClientEndpoint
public class GameClientEndpoint {
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;
    private HumanPlayer player;

    public GameClientEndpoint(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        Scanner sc = new Scanner(System.in);
        AccountFileManager acctMgr = new AccountFileManager(sc);
        Account a = acctMgr.initialize();

        try {
            objOut = new ObjectOutputStream(session.getBasicRemote().getSendStream());
            System.out.println("Connected to server");
            //Write account information to server
            objOut.writeObject(a);
            objOut.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(Object o) {
        if (o instanceof HumanPlayer) {
            HumanPlayer hp = (HumanPlayer)o;
            this.player = hp;
        } else if (o instanceof String) {
            String s = (String)o;
            System.out.println(s);
            if (s.contains(String.format("%s, enter the index of the card you want to ") + player.getName())) {
                Card c = player.chooseCardToPlay();
                
                //Send the object over
                sendMessage(c);
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        try {
            // Close the streams when the session ends
            if (objIn != null) objIn.close();
            if (objOut != null) objOut.close();
            System.out.println("Disconnected from server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Object o) {
        if (objOut == null) {
            System.err.println("Error: Output stream not initialized. Not connected to server.");
            return;
        }
        try {
            objOut.writeObject(o);
            objOut.flush();
        } catch (IOException e) {
            System.err.println("Failed to send message:");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Please enter a number!");
        }
    }
}
