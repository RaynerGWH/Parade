package game;

import players.*;
import cards.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ScoreCalculator {
    private List<Player> players;
    private TreeMap<Integer, ArrayList<Player>> scoreMap;

    /**
     * Constructor takes the list of players participating in the game.
     */
    public ScoreCalculator(List<Player> players) {
        this.players = players;
        Map<Player, Set<Color>> majorityMap = calculateMajorities();
        
        TreeMap<Integer, ArrayList<Player>> scoreMap = new TreeMap<Integer, ArrayList<Player>>();

        for (int i = 0; i < players.size(); i++) {
            Player currentPlayer = players.get(i);
            ArrayList<Card> river = currentPlayer.getRiver();
            Set<Color> majColors = majorityMap.get(currentPlayer);
            
            if (river == null || river.size() == 0) {
                if (scoreMap.containsKey(0)) {
                    ArrayList<Player> pList = scoreMap.get(0);
                    pList.add(currentPlayer);
                    scoreMap.put(0, pList);
                } else {
                    ArrayList<Player> pList = new ArrayList<Player>();
                    pList.add(currentPlayer);
                    scoreMap.put(0, pList);
                }
            }
            
            int currScore = 0;

            for (Card c : river) {
                //for every car
                Color currColor = c.getColor();
                if (majColors.contains(currColor)) {
                    currScore++;
                } else {
                    currScore += c.getValue();
                }
            }

            if (scoreMap.containsKey(currScore)) {
                ArrayList<Player> pList = scoreMap.get(currScore);
                pList.add(currentPlayer);
                scoreMap.put(currScore, pList);
            } else {
                ArrayList<Player> pList = new ArrayList<Player>();
                pList.add(currentPlayer);
                scoreMap.put(currScore, pList);
            }
        }

        this.scoreMap = scoreMap;
    }

    public TreeMap<Integer, ArrayList<Player>> getScoreMap() {
        return scoreMap;
    }

    /**
     * Calculates the majority for each color among all players.
     * For two players, a player has the majority for a color only if they have
     * at least two more cards of that color than the other player.
     * For multiplayer games, if exactly one player has the highest count (and that count is > 0),
     * that player is assigned the majority for that color.
     *
     * @return A map where each key is a Player and the value is a set of Colors for which that player has a strict majority.
     */
    
    public Map<Player, Set<Color>> calculateMajorities() {
        // Initialize the map to hold majority colors for each player.
        Map<Player, Set<Color>> majorityMap = new HashMap<>();
        for (Player p : players) {
            majorityMap.put(p, new HashSet<>());
        }
    
        // Loop over each color
        for (Color color : Color.values()) {
            int maxCount = 0;
            List<Player> leaders = new ArrayList<>();
    
            // Determine the highest count of this color
            for (Player player : players) {
                int count = countColor(player.getRiver(), color);
                if (count > maxCount) {
                    maxCount = count;
                    leaders.clear();
                    leaders.add(player);
                } else if (count == maxCount && maxCount > 0) {
                    leaders.add(player);
                }
            }
    
            // Assign majority for this color to all leaders
            for (Player leader : leaders) {
                majorityMap.get(leader).add(color);
            }
        }
    
        return majorityMap;
    }
    

    /**
     * Helper method to count the number of cards of a specific color in a list.
     *
     * @param cards the list of cards (usually the player's river/score pile)
     * @param color the color to count
     * @return the count of cards matching the color
     */
    private int countColor(ArrayList<Card> cards, Color color) {
        int count = 0;
        for (Card c : cards) {
            if (c.getColor() == color) {
                count++;
            }
        }
        return count;
    }
}
