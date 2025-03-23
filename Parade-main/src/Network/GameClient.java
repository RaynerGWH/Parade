package Network;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import account.AccountFileManager;
import account.Account;

public class GameClient {
    private static final String DEFAULT_SERVER_IP = "127.0.0.1";  // localhost by default
    private static final int DEFAULT_PORT = 12345;
    Account account = new AccountFileManager().initialize();

    public static void main(String[] args) {
        String serverIP = DEFAULT_SERVER_IP;
        int port = DEFAULT_PORT;
        
        // Allow custom server IP input
        Scanner scanner = new Scanner(System.in);
        System.out.println("🎭 Parade Game Client 🎭");
        
        System.out.print("Enter server IP (or press Enter for default " + DEFAULT_SERVER_IP + "): ");
        String customIP = scanner.nextLine().trim();
        if (!customIP.isEmpty()) {
            serverIP = customIP;
        }
        
        try (Socket socket = new Socket(serverIP, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Connected to Parade Game Server at " + serverIP + ":" + port);
            
            // Start a separate thread to receive messages from the server
            Thread receiveThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });
            receiveThread.setDaemon(true);
            receiveThread.start();
            
            // Main thread handles user input and sending to server
            System.out.println("Type your messages or commands (type 'QUIT' to exit):");
            
            while (true) {
                String message = scanner.nextLine();
                
                if (message.equalsIgnoreCase("QUIT")) {
                    System.out.println("Disconnecting from server.");
                    break;
                }
                
                out.println(message);
            }

        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + serverIP);
        } catch (ConnectException e) {
            System.err.println("Connection refused. Make sure the server is running at " + serverIP + ":" + port);
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}