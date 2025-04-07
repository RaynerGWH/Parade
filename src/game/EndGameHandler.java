package game;

import cards.*;
import players.*;
import players.human.HumanPlayer;
import ui.*;

import java.util.*;

/**
 * Handles the final rounds of the game and scoring calculations.
 * This class is responsible for managing the end-game scenarios,
 * including final turns, card discards, and score calculation.
 */
public class EndGameHandler {
    private GameState gameState;
    private UserInterface ui;
    private TurnManager turnManager;
    private HashMap<Player, Integer> timeBonus;
    private boolean timedMode;

    /**
     * Creates a new EndGameHandler.
     * 
     * @param gameState   The current state of the game
     * @param ui          The user interface to display messages
     * @param turnManager The turn manager for executing final turns
     * @param timeBonus   Map of time bonus points per player (can be null if not in
     *                    timed mode)
     * @param timedMode   Whether the game is in timed mode
     */
    public EndGameHandler(GameState gameState, UserInterface ui, TurnManager turnManager,
            HashMap<Player, Integer> timeBonus, boolean timedMode) {
        this.gameState = gameState;
        this.ui = ui;
        this.turnManager = turnManager;
        this.timeBonus = timeBonus;
        this.timedMode = timedMode;
    }

    /**
     * Handles the final round of the game and calculates scores.
     * 
     * @param gameMode The current game mode
     * @return A map of scores to players, sorted by score in ascending order
     */
    public TreeMap<Integer, ArrayList<Player>> handleFinalRoundAndScoring(GameMode gameMode) {
        List<Player> players = gameState.getPlayers();
        gameState.getDeck().clearDeck();

        executeFinalTurns(gameMode, players);
        executeDiscardPhase(gameMode, players);

        // Calculate scores
        ScoreCalculator scorer = new ScoreCalculator(players);
        TreeMap<Integer, ArrayList<Player>> scoreMap = scorer.getScoreMap();

        displayGameOverBanner();

        // Apply time bonuses if in timed mode
        if (timedMode && timeBonus != null) {
            applyTimeBonuses(scoreMap, players);
        }

        return scoreMap;
    }

    /**
     * Executes final turns for each player except the last one.
     * 
     * @param gameMode The current game mode
     * @param players  List of players
     */
    private void executeFinalTurns(GameMode gameMode, List<Player> players) {
        // Final turns - each player gets one more turn except the last player
        for (int i = 0; i < players.size() - 1; i++) {
            ui.broadcastMessage("══════════════════════════════════════════════════════════════");
            ui.broadcastMessage("FINAL TURN: NO ONE CAN DRAW CARDS");
            ui.broadcastMessage("══════════════════════════════════════════════════════════════");

            Player p = players.get(i);
            turnManager.executeTurn(gameState, p, gameMode, "Play");
        }
    }

    /**
     * Executes the discard phase where players choose cards to discard
     * and remaining cards are added to their rivers.
     * 
     * @param gameMode The current game mode
     * @param players  List of players
     */
    private void executeDiscardPhase(GameMode gameMode, List<Player> players) {
        ui.broadcastMessage("══════════════════════════════════════════════════════════════\n");
        ui.broadcastMessage(
                "Choose cards from your hand to discard! The remaining cards in your hand will be added to your river, so choose wisely!\n");
        ui.broadcastMessage("══════════════════════════════════════════════════════════════\n");

        for (Player currentPlayer : players) {
            if (currentPlayer instanceof HumanPlayer) {
                HumanPlayer currentHumanPlayer = (HumanPlayer) currentPlayer;
                // Using the TurnManager for discard turns
                turnManager.executeTurn(gameState, currentHumanPlayer, gameMode, "Discards");
                turnManager.executeTurn(gameState, currentHumanPlayer, gameMode, "Discards");
            } else {
                Card firstDiscardedCard = currentPlayer.chooseCardToDiscard();
                turnManager.displayCardPlayedOrDiscarded(currentPlayer, firstDiscardedCard, "Discards");
                Card secondDiscardedCard = currentPlayer.chooseCardToDiscard();
                turnManager.displayCardPlayedOrDiscarded(currentPlayer, secondDiscardedCard, "Discards");
            }

            // Add remaining cards to player's river
            ArrayList<Card> currentPlayerRiver = currentPlayer.getRiver();
            ArrayList<Card> currentPlayerHand = currentPlayer.getHand();
            for (Card c : currentPlayerHand) {
                currentPlayerRiver.add(c);
            }
            currentPlayerHand.clear();

            // Display the final river
            Collections.sort(currentPlayerRiver, new CardComparator());
            ui.broadcastMessage("\n");
            ui.broadcastMessage(PlayerDisplayUtils.getDisplayName(currentPlayer) + "'s River: ");
            ui.broadcastMessage(CardPrinter.printCardRow(currentPlayerRiver, false));
            ui.broadcastMessage("\n");
        }
    }

    /**
     * Displays the game over banner.
     */
    private void displayGameOverBanner() {
        ui.broadcastMessage("\n");
        ui.broadcastMessage(" _____                        _____                 ");
        ui.broadcastMessage("|  __ \\                      |  _  |                ");
        ui.broadcastMessage("| |  \\/ __ _ _ __ ___   ___  | | | |_   _____ _ __  ");
        ui.broadcastMessage("| | __ / _ | '_  _ \\ / _ \\ | | | \\ \\ / / _ \\ '__| ");
        ui.broadcastMessage("| |_\\ \\ (_| | | | | | |  __/ \\ \\_/ /\\ V /  __/ |    ");
        ui.broadcastMessage(" \\____/\\__,_|_| |_| |_|\\___|  \\___/  \\_/ \\___|_|    ");
        ui.broadcastMessage("                                                     ");
        ui.broadcastMessage("\n");
    }

    /**
     * Applies time bonuses to player scores.
     * 
     * @param scoreMap The current score map
     * @param players  List of players
     */
    private void applyTimeBonuses(TreeMap<Integer, ArrayList<Player>> scoreMap, List<Player> players) {
        ui.broadcastMessage("\n--- TIME BONUS POINTS ---");
        for (Player player : players) {
            int bonus = timeBonus.get(player);
            if (bonus > 0) {
                int logDeduction = (int) (Math.log(bonus) / Math.log(2));
                ui.broadcastMessage(PlayerDisplayUtils.getDisplayName(player) + " earned " + bonus + " bonus points, resulting in a " +
                        logDeduction + " point deduction!");

                for (Integer score : scoreMap.keySet()) {
                    ArrayList<Player> playersWithScore = scoreMap.get(score);
                    if (playersWithScore.contains(player)) {
                        playersWithScore.remove(player);
                        if (playersWithScore.isEmpty()) {
                            scoreMap.remove(score);
                        }
                        int newScore = score - logDeduction;
                        if (!scoreMap.containsKey(newScore)) {
                            scoreMap.put(newScore, new ArrayList<>());
                        }
                        scoreMap.get(newScore).add(player);
                        break;
                    }
                }
            }
        }
        ui.broadcastMessage("\n");
    }
}
