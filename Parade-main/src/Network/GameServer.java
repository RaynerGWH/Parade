package Network;

import java.io.*;
import java.net.*;
import java.util.*;
import account.*;
import players.*;

public class GameServer {
    private static final int PORT = 12345;
    private static final int MAX_PLAYERS = 8;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static boolean gameInProgress = false;
    
    ArrayList<Account> accounts = new ArrayList<Account>();
    
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("🎭 Parade Game Server Started on port " + PORT);
            System.out.println("Waiting for players to connect...");
            
            // Accept connections until enough players have joined or host starts the game
            while (clients.size() < MAX_PLAYERS && !gameInProgress ) {
                Socket socket = serverSocket.accept();
                System.out.println("New player connected: " + socket.getInetAddress().getHostAddress());
                
                ClientHandler client = new ClientHandler(socket);
                clients.add(client);
                new Thread(client).start();
                
                broadcast("Player " + clients.size() + " has joined the game.");
                
                if (clients.size() >= 2) {
                    broadcast("At least 2 players connected. Type 'START' to begin the game.");
                }
            }
            
            // Keep the server running to handle message broadcasting
            while (true) {
                Thread.sleep(1000);
            }
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    // Start the game with the current connected players
    public static void startGame() {
        if (clients.size() < 2) {
            broadcast("Cannot start game with less than 2 players.");
            return;
        }
        
        gameInProgress = true;
        broadcast("Starting game with " + clients.size() + " players...");
        
        try {
            // Get socket list for the player manager
            List<Socket> sockets = new ArrayList<>();
            for (ClientHandler client : clients) {
                sockets.add(client.getSocket());
            }
            
            // TODO: Start the game with the connected players
            // This would initialize the Game class with networked players
            // Game game = new Game(sockets, playerManager, accountManager);
            // TreeMap<Integer, ArrayList<Player>> scores = game.startGame(clients.size(), clients.size());
            
        } catch (Exception e) {
            broadcast("Error starting game: " + e.getMessage());
            gameInProgress = false;
        }
    }

    // Broadcast a message to all connected players
    public static void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }
    
    // // Broadcast to all players except one
    // public static void broadcastExcept(String message, ClientHandler except) {
    //     for (ClientHandler client : clients) {
    //         if (client != except) {
    //             client.sendMessage(message);
    //         }
    //     }
    // }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String playerName = "Unknown";
        private Account playerAccount;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public Socket getSocket() {
            return socket;
        }
        
        public String getPlayerName() {
            return playerName;
        }

        public void sendMessage(String message) {
            if (out != null) {
                out.println(message);
            }
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                
                // Authenticate player and get their account
                authenticatePlayer();
                
                out.println("Welcome, " + playerName + "! Waiting for other players...");
                
                String input;
                while ((input = in.readLine()) != null) {
                    System.out.println("Received from " + playerName + ": " + input);
                    
                    // Process commands
                    if (input.toUpperCase().equals("START") && !gameInProgress) {
                        broadcast(playerName + " is starting the game...");
                        startGame();
                    } else if (input.startsWith("PLAY ")) {
                        // Handle game moves
                        broadcastExcept(playerName + " plays: " + input.substring(5), this);
                    } else {
                        // Regular chat message
                        broadcastExcept(playerName + ": " + input, this);
                    }
                }
            } catch (IOException e) {
                System.out.println("Connection lost with " + playerName);
            } finally {
                try {
                    clients.remove(this);
                    socket.close();
                    broadcast(playerName + " has left the game.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        private void authenticatePlayer() {
            try {
                out.println("Please enter your username:");
                String username = in.readLine().trim();
                this.playerName = username;
                
                // TODO: Get or create account for this player
                // This would use the AccountFileManager to load or create an account
                // playerAccount = accountManager.getOrCreateAccount(username);
                
                out.println("Account authenticated successfully.");
            } catch (IOException e) {
                System.out.println("Error during authentication: " + e.getMessage());
            }
        }
    }
}
