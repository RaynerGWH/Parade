package ui;

import java.util.Collections;
import java.util.List;

public class Header {
    private static final int WIDTH = 159;

    public static String renderHeader(String titleLine, List<String> symbols, List<String> branches) {
        Collections.shuffle(symbols);
        String symbolsLine = padSides(String.join("  ", symbols), WIDTH + 10);
        StringBuilder sb = new StringBuilder();

        sb.append("╭").append("─".repeat(WIDTH)).append("╮\n");
        sb.append("│").append(centerText("⭒   " + titleLine + "   ⭒", WIDTH)).append("│\n");
        sb.append("│").append(symbolsLine).append("│\n");

        if (!branches.isEmpty()) {
            sb.append("╰─┬").append("─".repeat(WIDTH - 2)).append("╯\n");
            sb.append("  │\n");
        } else {
            sb.append("╰").append("─".repeat(WIDTH)).append("╯\n");
        }

        for (int i = 0; i < branches.size(); i++) {
            String prefix;
            if (i == branches.size() - 1) {
                prefix = "  ╰─ ";
            } else {
                prefix = "  ├─ ";
            }
            sb.append(prefix).append(branches.get(i)).append("\n");
            if (i < branches.size() - 1) {
                sb.append("  │\n");
            }
        }

        return sb.toString();
    }

    private static String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text + " ".repeat(Math.max(0, width - text.length() - padding));
    }

    private static String padSides(String text, int width) {
        int totalPadding = Math.max(0, width - text.length());
        int left = totalPadding / 2;
        int right = totalPadding - left;
        return " ".repeat(left) + text + " ".repeat(right);
    }
}