package cards;

import java.util.List;

public class CardPrinter {
    private static final String RESET = "\u001B[0m";
    private static final String LOGO = "❀";

    public static String printCardRow(List<Card> cards, boolean isParade) {
        try {
            if (cards == null || cards.isEmpty()) {
                return null;
            }
    
            int rowLimit = 12;
            int cardCount = 0;
    
            // Determine how many rows to use
            int numRows = isParade ? 5 : 6;
            StringBuilder[] lines = new StringBuilder[numRows];
            for (int i = 0; i < numRows; i++) {
                lines[i] = new StringBuilder();
            }

            StringBuilder output = new StringBuilder();

    
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
                    lines[5].append("   [").append(i).append("]    ");
                }
    
                cardCount++;
    
                // Wrap around after 12 cards (terminal visual limit)
                if (cardCount == rowLimit || i == cards.size() - 1) {
                    for (StringBuilder line : lines) {
                        output.append(line.toString()).append("\n");
                    }
                    cardCount = 0; // Reset counter for next batch
                }
            }
            return output.toString();
            
        } catch (Exception e) {
            System.out.println("⚠️ CardPrinter.printCardRow crashed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    

    private static String getColorCode(Color color) {
        return switch (color) {
            case RED -> "\u001B[1;31m";
            case BLUE -> "\u001B[1;34m";
            case PURPLE -> "\u001B[1;35m";
            case GREEN -> "\u001B[1;32m";
            case GREY -> "\u001B[1;37m";
            case ORANGE -> "\u001B[1;31m";
        };
    }
}