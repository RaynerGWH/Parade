package game;

import java.util.*;
import constants.GameplayConstants;
import constants.UIConstants;
import players.Player;
import ui.ConsoleUtils;
import ui.PlayerDisplayUtils;
import ui.UserInterface;

public class Game {
    // ASSUMPTION: GAME is only started by the host, except in singleplayer
    // instances.

    private UserInterface ui;
    private GameServerEndpoint gse;
    private Scanner scanner;
    private GameState gameState;

    // Timer-related fields for timed mode
    private boolean timedMode = false;
    private long gameStartTime;
    private long timeLimit; // in milliseconds
    private HashMap<Player, Integer> timeBonus = new HashMap<>();

    public Game(ArrayList<Player> players, UserInterface ui, GameServerEndpoint gse, Scanner scanner) {
        this.gameState = new GameState(players);
        // this.combinedPlayers = players;
        this.ui = ui;
        this.gse = gse;
        this.scanner = scanner;
    }

    public TreeMap<Integer, ArrayList<Player>> startGame() {
        // Game mode selection with validation
        GameMode gameMode = new ClassicMode();
        boolean validGameMode = false;
        ConsoleUtils.clear();
        System.out.println(UIConstants.GAMEMODE_SCREEN);
        while (!validGameMode) {
            System.out.println(UIConstants.TEXT_COLOR + "\nGamemodes available:" + UIConstants.RESET_COLOR);
            System.out.println("    1. Classic");
            System.out.println("    2. Timed");
            System.out.print("\nEnter '1' or '2'" + UIConstants.ConsoleInput);
            String gameModeChoice = scanner.nextLine().trim();

            if (gameModeChoice.equals("2")) {
                gameMode = new TimedMode();
                validGameMode = true;
                timedMode = true;
                System.out.println(UIConstants.TIMED_MODE_MESSAGE);
                
                // Initialize time bonus tracking
                timeBonus = new HashMap<>();
                for (Player player : gameState.getPlayers()) {
                    timeBonus.put(player, 0);
                }
                gameStartTime = System.currentTimeMillis() + GameplayConstants.THREE_SECOND_EXTENSION;

            } else if (gameModeChoice.equals("1")) {
                validGameMode = true;
                ConsoleUtils.clear();
                System.out.print(UIConstants.CLASSIC_MODE_MESSAGE);
                timedMode = false;
            } else {
                System.out.println(UIConstants.RESET_COLOR + "\n❌ Invalid choice. Please select 1 for Classic Mode or 2 for Timed Mode.");
            }

            gameMode.initialize(scanner);
        }

        System.out.println(UIConstants.PRESS_ENTER_TO_START);
        scanner.nextLine();
        // Countdown to game start after game mode selection
        ConsoleUtils.displayCountdown(ui);
        
        // Initialize parade with initial cards
        gameState.initializeParade(GameplayConstants.INITIAL_PARADE_LENGTH);
        
        // Create turn manager
        TurnManager turnManager = new TurnManager(ui, scanner);
        

        // Display timed mode info if applicable
        if (timedMode) {
            TimedMode timedGameMode = (TimedMode) (gameMode);
            timeLimit = timedGameMode.getTimeLimit();

            ui.broadcastMessage("\n═══ TIMED MODE ACTIVE ═══\n");
            ui.broadcastMessage("Time limit: " + (timeLimit / 60000) + " minute(s)\n");
            ui.broadcastMessage("Bonus points will be awarded for quick moves!\n");
            ui.broadcastMessage("═══════════════════════════\n");
        }

        int currentPlayerIndex = 0;
        while (!gameState.isGameOver()) {
            Player currentPlayer = gameState.getPlayers().get(currentPlayerIndex);

            if (gameMode.isTimeUp()) {
                ui.broadcastMessage("\n═══ TIME'S UP! ═══\n");
                gameState.setGameOver(true);
                break;
            }

            // Track turn start time for bonus calculations.
            long turnStartTime = System.currentTimeMillis();

            // Execute turn using turn manager
            boolean turnEndedGame = turnManager.executeTurn(gameState, currentPlayer, gameMode, GameplayConstants.PLAY);
            if (turnEndedGame) {
                gameState.setGameOver(true);
            }

            long turnDuration = System.currentTimeMillis() - turnStartTime;
            gameMode.updateAfterTurn(currentPlayer, turnDuration);

            // Update game mode after turn (for timing bonuses, etc.)
            if (timedMode) {
                TimedMode timedGameMode = (TimedMode) gameMode;
                int bonus = timedGameMode.getLastTurnBonus();

                if (bonus > 0) {
                    ui.broadcastMessage("\n" + PlayerDisplayUtils.getDisplayName(currentPlayer) + " gets " + bonus
                            + " time bonus points for a quick move!");

                    // Display time progress
                    long elapsedTime = System.currentTimeMillis() - gameStartTime;

                    // In ConsoleUtils.java, add to displayTimeProgressBar
                    ui.broadcastMessage(ConsoleUtils.displayTimeProgressBar(elapsedTime, timeLimit));
                    ui.broadcastMessage("\n");
                }
            }

            // Move to next player
            currentPlayerIndex = (currentPlayerIndex + 1) % gameState.getPlayers().size();
        }

        // If in timed mode, get the updated time bonus map from the TimedMode instance
        if (timedMode) {
            TimedMode timedGameMode = (TimedMode) gameMode;
            timeBonus = timedGameMode.getTimeBonusMap();
        }

        // EndGameHandler to handle the final round and scoring after the game is over.
        EndGameHandler endGameHandler = new EndGameHandler(gameState, ui, turnManager, timeBonus, timedMode);

        return endGameHandler.handleFinalRoundAndScoring(gameMode);
    }
}