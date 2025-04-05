package game;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.HashMap;

import org.glassfish.tyrus.server.*;
import jakarta.websocket.*;

import players.Player;
import players.PlayerManager;
import players.computer.BeginnerComputerPlayer;
import players.computer.IntermediateComputerPlayer;
import players.human.HumanPlayer;
import account.*;
import ui.*;

public class GameManager {
    private Server websocketServer;
    Scanner sc;
    PlayerManager playerMgr = new PlayerManager();
    UserInterface ui;
    GameServerEndpoint gse;
    private Map<Session, Account> sessions;
    int numBots;


    public GameManager(Scanner sc) {
        this.sc = sc;
    }

    public void start(UserInterface ui, GameServerEndpoint gse) {
        this.ui = ui;
        this.gse = gse;

        if (gse != null) {
            this.sessions = GameServerEndpoint.getSessionPlayers();
        } else {
            this.sessions = new HashMap<Session, Account>();
        }

        if (ui instanceof SinglePlayerUI) {
            singleplayerHandler();
        } else {
            multiplayerHandler();
        }

        Game g = new Game(playerMgr.getPlayers(), ui, gse, sc);
        TreeMap<Integer, ArrayList<Player>> scores = g.startGame();
        printRankings(scores);
        handleRewards(scores);
    }

    public void singleplayerHandler() {
        boolean isMulti = false;
        AccountFileManager acctMgr = new AccountFileManager();
        Account userAcct = acctMgr.initialize();
        sessions.put(null, userAcct);
        playerMgr.initializeHumanPlayers(sessions, isMulti);
        botHandler(1);
    }

    public void multiplayerHandler() {
        boolean isMulti = true;
        humanHandler();
        sessions = GameServerEndpoint.getSessionPlayers();
        playerMgr.initializeHumanPlayers(sessions, isMulti);

        while (true) {
            System.out.print("Do you want to add bots? (Y/N)\n> ");
            String input = sc.nextLine().trim().toUpperCase();
            if (input.equals("Y")) {
                botHandler(playerMgr.getPlayers().size());

            } else if (input.equals("N")) {
                break;

            } else {
                System.out.println("Invalid input. Please type Y or N.");
            }
        }
    }

    public void startWebSocketServer() {
        Map<String, Object> properties = Collections.emptyMap();
        // get the current host's public ip
        String hostIp = null;
        try {
            hostIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            hostIp = "127.0.0.1"; // Fallback to localhost
        }

        websocketServer = new Server(hostIp, 8080, "/", properties, GameServerEndpoint.class);
        try {
            websocketServer.start();
            ClearConsole.clear();
            System.out.println("WebSocket server is running...");
            System.out.println("==============================");
            System.out.println("Your Host IP: \u001B[32m" + hostIp + ":8080\u001B[0m");
            System.out.println("==============================");
            System.out.println("Give this to your friends to get them to join!");
        } catch (Exception e) {
            System.out.println("Unable to start server. Please restart the game and try again.");
            System.exit(-1);
        }
    }

    public void stopWebSocketServer() {
        if (websocketServer != null) {
            websocketServer.stop();
            System.out.println("WebSocket server stopped.");
        }
    }

    public int botHandler(int numPlayers) {
        int numBots = 0;
        int maximumNumberOfBots = 8 - numPlayers;
        int minimumNumberOfBots = 2 - numPlayers;

        while (true) {
            try {
                System.out.print("Enter number of Bots (" + minimumNumberOfBots + " - " + maximumNumberOfBots + ")\n> ");
                numBots = Integer.parseInt(sc.nextLine());
                if (numPlayers + numBots > 8 || numPlayers + numBots < 2) {
                    throw new NumberFormatException();
                }
                playerMgr.initializeComputerPlayers(numBots);
                return numBots;

            } catch (NumberFormatException e) {
                // Max number of bots able to be added : 8 - numplayers
                // Min number of bots to be added : 2 - numplayers
                System.out.println("Please enter a valid number! Number of total players must be between "
                                   + minimumNumberOfBots + " and " + maximumNumberOfBots);
            }
        }
    }

    public void humanHandler() {
        // We start the server only if there are other human players(besides yourself)
        // TODO: ADD CHECKING FUNCCTION TO PREVENT STARTING WITHOUT OTHER PLAYERS
        ClearConsole.clear();
        startWebSocketServer();
        while (true) {
            System.out.print("Waiting for players... Type \"START\" to start the game\n> ");
            String command = sc.nextLine();
            if (command.toUpperCase().trim().equals("START") && GameServerEndpoint.getNumPlayers() > 0) {
                return;
            }

            if (GameServerEndpoint.getNumPlayers() <= 1) {
                System.out.println("Invalid number of players. Make sure that there is more than one!");

            } else {
                System.out.println("Invalid command. Type \"START\" to start the game\n> ");
                
            }
        }
    }

    public void printRankings(TreeMap<Integer, ArrayList<Player>> scores) {
        ui.broadcastMessage("\n=== FINAL RANKINGS ===");
        int rank = 1;
        for (Map.Entry<Integer, ArrayList<Player>> entry : scores.entrySet()) {
            ArrayList<Player> players = entry.getValue();
            for (Player player : players) {
                ui.broadcastMessage(getOrdinal(rank) + ": " + player.getName() + " | Score: " + entry.getKey());
            }
            rank += players.size(); // Increase rank appropriately
        }
    }

    private static String getOrdinal(int rank) {
        if (rank % 100 >= 11 && rank % 100 <= 13) {
            return rank + "th";
        }
        return switch (rank % 10) {
            case 1 -> rank + "ST";
            case 2 -> rank + "ND";
            case 3 -> rank + "RD";
            default -> rank + "TH";
        };
    }

    private void handleRewards(TreeMap<Integer, ArrayList<Player>> scores) {
        boolean isMultiplayer = (ui instanceof MultiplayerUI);
        
        // Determine the winning score.
        if (!scores.isEmpty()) {
            int winningScore = scores.firstKey();
            ArrayList<Player> winners = scores.get(winningScore);
            
            if (isMultiplayer) {
                MultiplayerUI MUI = (MultiplayerUI)ui;
                ArrayList<Player> players = playerMgr.getPlayers();
                // Multiplayer: For each winning human player, add 1 win and bonus = 100 * number of players.
                int bonus = 100 * playerMgr.getPlayers().size();

                for (Player p : players) {
                    if (winners.contains(p) && p instanceof HumanPlayer) {
                        HumanPlayer hp = (HumanPlayer)p;
                        Account account = hp.getAccount();
                        account.incrementWins();
                        account.addBalance(bonus);
                        MUI.sendAccount(account,hp.getSession());
                        
                    } else if (p instanceof HumanPlayer) {
                        HumanPlayer hp = (HumanPlayer) p;
                        Account account = hp.getAccount();
                        account.incrementLosses();
                        MUI.sendAccount(account,hp.getSession());
                    }
                }
            } else {
                try {
                    ArrayList<BeginnerComputerPlayer> bcpList = new ArrayList<BeginnerComputerPlayer>();
                    ArrayList<IntermediateComputerPlayer> icpList = new ArrayList<IntermediateComputerPlayer>();
                    HumanPlayer humanPlayer = null;
                    AccountFileManager acctMgr = new AccountFileManager();
                    Account account = null;
    
                    for (Player player : playerMgr.getPlayers()) {
                        if (player instanceof BeginnerComputerPlayer) {
                            bcpList.add((BeginnerComputerPlayer)player);
                        } else if (player instanceof IntermediateComputerPlayer) {
                            icpList.add((IntermediateComputerPlayer)player);
                        } else {
                            humanPlayer = (HumanPlayer)player;
                        }
    
                        //did bro win? yes -> handle, no -> ignore
                        if (humanPlayer != null && winners.contains(humanPlayer)) {
                            account = humanPlayer.getAccount();
                            account.incrementWins();
                            account.addBalance(100 * bcpList.size());
                            account.addBalance(200 * icpList.size());
                            acctMgr.save(account);

                        } else {
                            account = humanPlayer.getAccount();
                            account.incrementLosses();
                            acctMgr.save(account);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error processing message: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
