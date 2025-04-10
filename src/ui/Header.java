package ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Header {
    private static final int WIDTH = 159;
    private static final String TITLE = "P A R A D E";
    private static final String BLUE = "\u001B[38;5;117m";
    private static final String WHITE = "\u001B[97m";
    private static final List<String> mayanSymbols = new ArrayList<>(
        List.of("𓂀", "𓋡", "𓃂", "𓁾", "𓃖", "𓏞", "𓎿", "𓏢", "𓆃", "𓅓")
    );

    public static String renderHeader(List<String> branches) {
        if (branches == null) {
            branches = List.of();
        }

        Collections.shuffle(mayanSymbols);
        String symbolsLine = padSides(String.join("  ", mayanSymbols), WIDTH + 10);
        StringBuilder sb = new StringBuilder();

        sb.append(BLUE);
        sb.append("╭").append("─".repeat(WIDTH)).append("╮\n");
        sb.append("│").append(centerText("⭒   " + TITLE + "   ⭒", WIDTH)).append("│\n");
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

        sb.append(WHITE); // reset color at the very end
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