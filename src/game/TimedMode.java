package game;

import constants.GameplayConstants;
import constants.UIConstants;
import java.util.*;
import players.Player;

public class TimedMode implements GameMode {
    private long gameStartTime;
    private long timeLimit;
    private int lastTurnBonus = GameplayConstants.INITIAL_TIMED_MODE_TURN_BONUS;
    private HashMap<Player, Integer> timeBonus = new HashMap<>();

    @Override
    public void initialize(Scanner scanner) {
        System.out.println(UIConstants.TEXT_COLOR + "Categories:" + UIConstants.RESET_COLOR);
        System.out.println("    1. 1-minute blitz");
        System.out.println("    2. 5-minute challenge");
        System.out.println("    3. 10-minute game");

        // Code to get time choice and set timeLimit
        // ...
        int timeChoice = 0;
        boolean validChoice = false;

        while (!validChoice) {
            try {
                System.out.print("\nEnter mode [1-3]" + UIConstants.ConsoleInput);
                timeChoice = Integer.parseInt(scanner.nextLine().trim());
                if (timeChoice >= 1 && timeChoice <= 3) {
                    validChoice = true;
                } else {
                    System.out.println("Please enter a number between 1 and 3.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        // Set time limit based on choice
        switch (timeChoice) {
            case 1:
                timeLimit = GameplayConstants.ONE_MINUTE_MILLIS;
                System.out.print(UIConstants.RESET_COLOR + "\n1-minute blitz selected!\n");
                break;
            case 2:
                timeLimit = GameplayConstants.FIVE_MINUTES_MILLIS;
                System.out.print(UIConstants.RESET_COLOR + "\n5-minute challenge selected!\n");
                break;
            case 3:
                timeLimit = GameplayConstants.TEN_MINUTES_MILLIS;
                System.out.print(UIConstants.RESET_COLOR + "\n10-minute game selected!\n");
                break;
        }

        gameStartTime = System.currentTimeMillis() + GameplayConstants.THREE_SECOND_EXTENSION;
    }

    @Override
    public boolean isTimeUp() {
        return System.currentTimeMillis() - gameStartTime >= timeLimit;
    }

    // Other method implementations...
    public boolean applyModeSpecificRules(GameState state) {
        // No false. No special rules for timed mode
        return false;
    }

    public void updateAfterTurn(Player player, long turnDuration) {
        // Calculate bonus points for quick turns
        lastTurnBonus = calculateTimeBonus(turnDuration);

        // Store the bonus for this player
        if (lastTurnBonus > 0) {
            int currentBonus = timeBonus.getOrDefault(player, 0);
            timeBonus.put(player, currentBonus + lastTurnBonus);
        }
    }

    /**
     * Gets the time limit in milliseconds.
     * 
     * @return The time limit for this game mode
     */
    public long getTimeLimit() {
        return timeLimit;
    }

    /**
     * Gets the bonus earned in the last turn.
     * 
     * @return The bonus points for the most recent turn
     */
    public int getLastTurnBonus() {
        return lastTurnBonus;
    }

    /**
     * Gets the map of time bonuses for all players.
     * 
     * @return A map of players to their accumulated time bonuses
     */
    public HashMap<Player, Integer> getTimeBonusMap() {
        return timeBonus;
    }

    /**
     * Gets the total time bonus for a specific player.
     * 
     * @param player The player to get bonuses for
     * @return The total accumulated time bonus
     */
    public int getPlayerTimeBonus(Player player) {
        return timeBonus.getOrDefault(player, 0);
    }

    /**
     * Calculates the time bonus based on how quickly a turn was completed.
     * 
     * @param turnDuration The duration of the turn in milliseconds
     * @return The bonus points earned for this turn
     */
    private int calculateTimeBonus(long turnDuration) {
        if (timeLimit <= GameplayConstants.ONE_MINUTE_MILLIS) {
            // For 1-minute games, faster bonuses
            if (turnDuration < 8 * GameplayConstants.ONE_SECOND_MILLIS)
                return GameplayConstants.MAX_BONUS_POINTS;
            if (turnDuration < 11 * GameplayConstants.ONE_SECOND_MILLIS)
                return GameplayConstants.MEDIUM_BONUS_POINTS;
            if (turnDuration < 15 * GameplayConstants.ONE_SECOND_MILLIS)
                return GameplayConstants.MIN_BONUS_POINTS;
        } else {
            // For longer games, slightly more relaxed timing
            if (turnDuration < 10 * GameplayConstants.ONE_SECOND_MILLIS)
                return GameplayConstants.MAX_BONUS_POINTS;
            if (turnDuration < 15 * GameplayConstants.ONE_SECOND_MILLIS)
                return GameplayConstants.MEDIUM_BONUS_POINTS;
            if (turnDuration < 20 * GameplayConstants.ONE_SECOND_MILLIS)
                return GameplayConstants.MIN_BONUS_POINTS;
        }
        return 0;
    }

    /**
     * Gets time remaining in the game.
     * 
     * @return Time remaining in milliseconds
     */
    public long getTimeRemaining() {
        long elapsedTime = System.currentTimeMillis() - gameStartTime;
        return Math.max(0, timeLimit - elapsedTime);
    }

    /**
     * Gets the elapsed time since the game started.
     * 
     * @return Elapsed time in milliseconds
     */
    public long getElapsedTime() {
        return System.currentTimeMillis() - gameStartTime;
    }

    /**
     * Initializes time bonuses for all players in the game.
     * Should be called after players are added to the game state.
     * 
     * @param players List of players in the game
     */
    public void initializeTimeBonuses(List<Player> players) {
        // Initialize time bonus tracking for all players
        timeBonus.clear();
        for (Player player : players) {
            timeBonus.put(player, 0);
        }
    }
}
