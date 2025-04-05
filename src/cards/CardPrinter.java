package cards;

import java.util.List;

public class CardPrinter {
    private static final String RESET = "\u001B[0m";
    private static final String LOGO = "❀";

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
    
                lines[0].append(colorCode).append("╔══─────╮").append(RESET).append(" ");
                lines[1].append(colorCode).append("║ ").append(topLeft).append("│").append(RESET).append(" ");
                lines[2].append(colorCode).append("│   ").append(LOGO).append("   │").append(RESET).append(" ");
                lines[3].append(colorCode).append("│ ").append(bottomRight).append("│").append(RESET).append(" ");
                lines[4].append(colorCode).append("╰───────╯").append(RESET).append(" ");
    
                if (cards.size() > 1 && !isParade) {
                    if (numRows > 5) {  // Check if lines[5] exists
                        lines[5].append("   [").append(i).append("]    ");
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
    private static String getColorCode(Color color) {
        // Add a special marker to recognize colors in WebSocket transmission
        String colorName;
        switch (color) {
            case RED:
                colorName = "RED";
                break;
            case BLUE:
                colorName = "BLUE";
                break;
            case PURPLE:
                colorName = "PURPLE";
                break;
            case GREEN:
                colorName = "GREEN";
                break;
            case GREY:
                colorName = "GREY";
                break;
            case ORANGE:
                colorName = "ORANGE";
                break;
            default:
                colorName = "NONE";
        }
        
        // For direct console output
        if (color == Color.RED || color == Color.ORANGE) {
            return "\u001B[1;31m"; // Red
        } else if (color == Color.BLUE) {
            return "\u001B[1;34m"; // Blue
        } else if (color == Color.PURPLE) {
            return "\u001B[1;35m"; // Purple
        } else if (color == Color.GREEN) {
            return "\u001B[1;32m"; // Green
        } else { // GREY
            return "\u001B[1;37m"; // White
        }
    }
}