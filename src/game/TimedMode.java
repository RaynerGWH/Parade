package game;

import java.util.*;

import constants.Constants;
import players.*;

public class TimedMode implements GameMode {
    private long gameStartTime;
    private long timeLimit;
    private int lastTurnBonus = Constants.INITIAL_TIMED_MODE_TURN_BONUS;
    private HashMap<Player, Integer> timeBonus = new HashMap<>();

    @Override
    public void initialize(Scanner scanner) {
        System.out.println("Categories:");
        System.out.println("    1. 1-minute blitz");
        System.out.println("    2. 5-minute challenge");
        System.out.println("    3. 10-minute game");

        // Code to get time choice and set timeLimit
        // ...
        int timeChoice = 0;
        boolean validChoice = false;

        while (!validChoice) {
            try {
                System.out.print("Enter category (1-3)\n> ");
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
                timeLimit = 60 * 1000; // 1 minute in milliseconds
                System.out.print("\n1-minute blitz selected!\n");
                break;
            case 2:
                timeLimit = 5 * 60 * 1000; // 5 minutes in milliseconds
                System.out.print("\n5-minute challenge selected!\n");
                break;
            case 3:
                timeLimit = 10 * 60 * 1000; // 10 minutes in milliseconds
                System.out.print("\n10-minute game selected!\n");
                break;
        }
        
        gameStartTime = System.currentTimeMillis() + Constants.THREE_SECOND_EXTENSION;
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
        if (timeLimit <= 60 * 1000) {
            // For 1-minute games, faster bonuses
            if (turnDuration < 3000)
                return 3;
            if (turnDuration < 6000)
                return 2;
            if (turnDuration < 10000)
                return 1;
        } else {
            // For longer games, slightly more relaxed timing
            if (turnDuration < 5000)
                return 3;
            if (turnDuration < 10000)
                return 2;
            if (turnDuration < 15000)
                return 1;
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
}
