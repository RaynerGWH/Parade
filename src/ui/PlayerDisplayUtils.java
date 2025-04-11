package ui;

import account.Account;
import constants.UIConstants;
import java.util.List;
import players.Player;
import players.human.HumanPlayer;

/**
 * Utility class for player display operations.
 */
public class PlayerDisplayUtils {

    /**
     * Returns a player's display name with flair appended (if available).
     * 
     * @param player The player to get the display name for
     * @return Formatted display name, possibly including flair
     */
    public static String getDisplayName(Player player) {
        if (player instanceof HumanPlayer) {
            HumanPlayer hp = (HumanPlayer) player;
            Account account = hp.getAccount();
            if (account != null) {
                List<String> flairs = account.getUnlockedFlairs();
                if (flairs != null && !flairs.isEmpty()) {
                    return account.getUsername() + " [" + flairs.get(0) + "]";
                } else {
                    return account.getUsername();
                }
            }
        }
        return player.getName();
    }

    /**
     * Creates a formatted turn header showing all players with the current player
     * highlighted.
     * 
     * @param players       List of all players in the game
     * @param currentPlayer The player whose turn it currently is
     * @return Formatted turn header string
     */
    public static String createTurnHeader(List<Player> players, Player currentPlayer) {
        StringBuilder header = new StringBuilder("Current Turn: ");
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            String displayName = getDisplayName(p);
            if (p.equals(currentPlayer)) {
                // ANSI escape code for green text
                header.append(UIConstants.GREEN).append(displayName).append(UIConstants.RESET_COLOR);
            } else {
                header.append(displayName);
            }
            if (i < players.size() - 1) {
                header.append(" ▶ ");
            }
        }
        return header.toString();
    }

    /**
     * Creates a formatted turn announcement for the next player.
     * 
     * @param nextPlayer The player whose turn is next
     * @return Formatted turn announcement string
     */
    public static String createNextTurnAnnouncement(Player nextPlayer) {
        String playerName = getDisplayName(nextPlayer);
        StringBuilder announcement = new StringBuilder();

        // announcement.append("===============================================================\n");
        // announcement.append("                      ").append(playerName).append("'s TURN                \n");
        // announcement.append("===============================================================");

        return announcement.toString();
    }
}