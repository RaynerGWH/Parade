package cards;

import constants.UIConstants;
import java.util.List;

public class CardPrinter {

    /**
     * Returns a string representation of cards in a row for displaying via WebSockets
     * @param cards List of cards to render
     * @param isParade Whether this is the parade display or not
     * @return String representation of the cards
     */
    public static String printCardRow(List<Card> cards, boolean isParade) {
        StringBuilder result = new StringBuilder();
        try {
            if (cards == null || cards.isEmpty()) {
                return "";
            }
    
            int rowLimit = 12;
            int cardCount = 0;
    
            // Determine how many rows to use
            int numRows = isParade ? 5 : 6;
            StringBuilder[] lines = new StringBuilder[numRows];
            for (int i = 0; i < numRows; i++) {
                lines[i] = new StringBuilder();
            }
    
            for (int i = 0; i < cards.size(); i++) {
                Card card = cards.get(i);
                if (card == null) continue;
    
                String colorCode = getColorCode(card.getColor());
                String value = String.valueOf(card.getValue());
    
                String topLeft = value.length() == 1 ? value + "     " : value + "    ";
                String bottomRight = value.length() == 1 ? "    " + value + " " : "   " + value + " ";
    
                lines[0].append(colorCode).append("╔══─────╮").append(UIConstants.RESET_COLOR).append(" ");
                lines[1].append(colorCode).append("║ ").append(topLeft).append("│").append(UIConstants.RESET_COLOR).append(" ");
                lines[2].append(colorCode).append("│   ").append(UIConstants.CARD_LOGO).append("   │").append(UIConstants.RESET_COLOR).append(" ");
                lines[3].append(colorCode).append("│ ").append(bottomRight).append("│").append(UIConstants.RESET_COLOR).append(" ");
                lines[4].append(colorCode).append("╰───────╯").append(UIConstants.RESET_COLOR).append(" ");
    
                if (cards.size() > 1 && !isParade) {
                    if (numRows > 5) {  // Check if lines[5] exists
                        lines[5].append("   [").append(i).append("]    ");
                    } else {
                        lines[5].append("   [").append(i + 1).append("]    ");
                    }
                }
                cardCount++;
    
                // Wrap around after 12 cards (terminal visual limit)
                if (cardCount == rowLimit || i == cards.size() - 1) {
                    for (StringBuilder line : lines) {
                        result.append(line.toString()).append("\n");
                        line.setLength(0); // reset each line
                    }
                    cardCount = 0;
                }
            }
            
            return result.toString();
            
        } catch (Exception e) {
            return "⚠️ Error displaying cards: " + e.getMessage();
        }
    }
    
    /**
     * Gets the ANSI color code for a card color.
     * In a terminal environment, these will show colored text.
     * In a WebSocket environment, the client may need to interpret these.
     */
    private static String getColorCode(CardColor color) {
        // For direct console output
        if (color == CardColor.RED) {
            return UIConstants.RED; // Red
        } else if (color == CardColor.BLUE) {
            return UIConstants.BLUE; // Blue
        } else if (color == CardColor.PURPLE) {
            return UIConstants.PURPLE; // Purple
        } else if (color == CardColor.GREEN) {
            return UIConstants.GREEN; // Green
        } else if (color == CardColor.GREY) { // GREY
            return UIConstants.WHITE; // White
        } else {
            return UIConstants.ORANGE; // RGB for orange
        }
    }

}