package game;

import constants.GameplayConstants;
import constants.UIConstants;
import java.util.*;
import players.Player;
import ui.ConsoleUtils;
import ui.PlayerDisplayUtils;
import ui.UserInterface;

public class Game {

    private UserInterface ui;
    private GameServerEndpoint gse;
    private Scanner scanner;
    private GameState gameState;

    // Timer-related fields for timed mode
    private boolean timedMode = false;

    public Game(ArrayList<Player> players, UserInterface ui, GameServerEndpoint gse, Scanner scanner) {
        this.gameState = new GameState(players);
        // this.combinedPlayers = players;
        this.ui = ui;
        this.gse = gse;
        this.scanner = scanner;
    }

    /**
     * Helper method to get the TimedMode instance if the current game mode is TimedMode.
     * 
     * @param gameMode The current game mode
     * @return The TimedMode instance, or null if not in timed mode
     */
    private TimedMode getTimedGameMode(GameMode gameMode) {
        return (timedMode && gameMode instanceof TimedMode) ? (TimedMode) gameMode : null;
    }

    public TreeMap<Integer, ArrayList<Player>> startGame() {
        // Game mode selection with validation
        GameMode gameMode = new ClassicMode();
        boolean validGameMode = false;
        ConsoleUtils.clear();
        System.out.println(UIConstants.GAMEMODE_SCREEN);
        while (!validGameMode) {
            System.out.println(UIConstants.TEXT_COLOR + "\n\nGamemodes available:" + UIConstants.RESET_COLOR);
            System.out.println("    1. Classic 🍂");
            System.out.println("    2. Timed ⌛");
            System.out.print("\nEnter '1' or '2'" + UIConstants.ConsoleInput);
            String gameModeChoice = scanner.nextLine().trim();

            if (gameModeChoice.equals("2")) {
                gameMode = new TimedMode();
                validGameMode = true;
                timedMode = true;
                ConsoleUtils.clear();
                System.out.println(UIConstants.TIMED_MODE_MESSAGE);
                
                // Initialize the game mode
                gameMode.initialize(scanner);
                
                // Initialize time bonuses for all players
                TimedMode timedGameMode = getTimedGameMode(gameMode);
                if (timedGameMode != null) {
                    timedGameMode.initializeTimeBonuses(gameState.getPlayers());
                }
            } else if (gameModeChoice.equals("1")) {
                validGameMode = true;
                ConsoleUtils.clear();
                System.out.print(UIConstants.CLASSIC_MODE_MESSAGE);
                timedMode = false;
                
                // Initialize the game mode
                gameMode.initialize(scanner);
            } else {
                System.out.println(UIConstants.RESET_COLOR + "\n❌ Invalid choice. Please select 1 for Classic Mode or 2 for Timed Mode.");
            }
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
        TimedMode timedGameMode = getTimedGameMode(gameMode);
        if (timedGameMode != null) {
            ui.broadcastMessage("\n═══ TIMED MODE ACTIVE ═══\n");
            ui.broadcastMessage("Time limit: " + (timedGameMode.getTimeLimit() / 60000) + " minute(s)\n");
            ui.broadcastMessage("Bonus points will be awarded for quick moves!\n");
            ui.broadcastMessage("═══════════════════════════\n");
        }

        int currentPlayerIndex = 0;
        while (!gameState.isGameOver()) {
            Player currentPlayer = gameState.getPlayers().get(currentPlayerIndex);

            if (gameMode.isTimeUp()) {
                ui.broadcastMessage(UIConstants.TIME_UP_MESSAGE);
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
            timedGameMode = getTimedGameMode(gameMode);
            if (timedGameMode != null) {
                int bonus = timedGameMode.getLastTurnBonus();

                if (bonus > 0) {
                    ui.broadcastMessage("\n" + PlayerDisplayUtils.getDisplayName(currentPlayer) + " gets " + bonus
                            + " time bonus points for a quick move!");

                    // Display time progress using TimedMode's methods
                    long elapsedTime = timedGameMode.getElapsedTime();

                    ui.broadcastMessage(ConsoleUtils.displayTimeProgressBar(elapsedTime, timedGameMode.getTimeLimit()));
                    ui.broadcastMessage("\n");
                }
            }

            // Move to next player
            currentPlayerIndex = (currentPlayerIndex + 1) % gameState.getPlayers().size();
        }

        // EndGameHandler to handle the final round and scoring after the game is over.
        EndGameHandler endGameHandler;
        timedGameMode = getTimedGameMode(gameMode);
        if (timedGameMode != null) {
            endGameHandler = new EndGameHandler(gameState, ui, turnManager, timedGameMode.getTimeBonusMap(), timedMode);
        } else {
            endGameHandler = new EndGameHandler(gameState, ui, turnManager, new HashMap<>(), timedMode);
        }

        return endGameHandler.handleFinalRoundAndScoring(gameMode);
    }
}