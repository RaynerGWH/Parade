package game;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.glassfish.tyrus.server.*;
import jakarta.websocket.*;

import players.Player;
import players.PlayerManager;
import account.*;
import ui.*;

public class GameManager {
    private Server websocketServer;
    Scanner sc;
    PlayerManager playerMgr = new PlayerManager();
    UserInterface ui;
    int numBots;
    GameServerEndpoint gse;
    private Map<Session, Account> sessions;


    public GameManager(Scanner sc) {
        this.sc = sc;
    }

    public void start(int numBots, UserInterface ui, GameServerEndpoint gse) {
        this.ui = ui;
        this.numBots = numBots;
        this.gse = gse;
        this.sessions = GameServerEndpoint.getSessionPlayers();

        if (ui instanceof SinglePlayerUI) {
            singleplayerHandler();
        } else {
            multiplayerHandler();
        }

        Game g = new Game(playerMgr.getPlayers(), ui, gse, sc);
        TreeMap<Integer, ArrayList<Player>> scores = g.startGame();
        printRankings(scores);

        //Handle rewards distribution here
    }

    public void singleplayerHandler() {
        boolean isMulti = false;
        playerMgr.initializeHumanPlayers(GameServerEndpoint.getSessionPlayers(), isMulti);
        botHandler(1);
    }

    public void multiplayerHandler() {
        boolean isMulti = true;
        humanHandler();
        playerMgr.initializeHumanPlayers(GameServerEndpoint.getSessionPlayers(), isMulti);

        // mapPlayers();

        if (sessions.size() < 8) {
            numBots = botHandler(sessions.size());
        }
    }

    public void startWebSocketServer() {
        Map<String, Object> properties = Collections.emptyMap();
        // Start the WebSocket server on localhost:8080
        websocketServer = new Server("localhost", 8080, "/", properties, GameServerEndpoint.class);
        try {
            websocketServer.start();
            System.out.println("WebSocket server is running...");
        } catch (Exception e) {
            e.printStackTrace();
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

        while (true) {
            try {
                System.out.print("Enter number of Bots: ");
                numBots = Integer.parseInt(sc.nextLine());
                if (numPlayers + numBots > 8 || numPlayers + numBots < 2) {
                    throw new NumberFormatException();
                }
                playerMgr.initializeComputerPlayers(numBots);
                return numBots;

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number! Number of total players must be < 9 and > 1.");
            }
        }
    }

    public void humanHandler() {
        // We start the server only if there are other human players(besides yourself)

        // TODO: ADD CHECKING FUNCCTION TO PREVENT STARTING WITHOUT OTHER PLAYERS

        startWebSocketServer();
        System.out.println("Waiting for players... Type \"START\" to start the game");
        String command = sc.nextLine();
        while (!command.equals("START")) {
            System.out.println("Invalid command.");
        }
    }

    public static void printRankings(TreeMap<Integer, ArrayList<Player>> scores) {
        System.out.println("\n=== FINAL RANKINGS ===");
        int rank = 1;
        for (Map.Entry<Integer, ArrayList<Player>> entry : scores.entrySet()) {
            ArrayList<Player> players = entry.getValue();
            for (Player player : players) {
                System.out.println(getOrdinal(rank) + ": " + player.getName() + " | Score: " + entry.getKey());
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
}
