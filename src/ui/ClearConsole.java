package ui;

public class ClearConsole {
    private static final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");

    public static void clear() {
        try {
            if (isWindows) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // Works for macOS and most Linux terminals that support ANSI escape codes
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("Could not clear console.");
        }
    }
}
