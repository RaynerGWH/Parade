package constants;

public class Constants {
    // Existing constants

    // Game setup
    public static final int INITIAL_PARADE_LENGTH = 6;

    // Time limits in milliseconds
    public static final long ONE_MINUTE_MILLIS = 60 * 1000;
    public static final long FIVE_MINUTES_MILLIS = 5 * 60 * 1000;
    public static final long TEN_MINUTES_MILLIS = 10 * 60 * 1000;

    // UI strings
    public static final String TIME_UP_MESSAGE = "\n═══ TIME'S UP! ═══\n";

    // UI Messages
    public static final String PRESS_ENTER_TO_START = "                                              [Enter] Start Game 🕹️ 🕹️";

    // Card limits
    public static final int MAX_HAND_SIZE = 5;
    public static final int INITIAL_HAND_SIZE = 5;

    // In timed mode, how many cards to discard at the end
    public static final int FINAL_DISCARD_COUNT = 2;

    // In timed mode, initial turnBonus
    public static final int INITIAL_TIMED_MODE_TURN_BONUS = 0;

    // Initial player count settings
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 6;

    // Classic Mode Message
    public static final String CLASSIC_MODE_MESSAGE = "\n" +
            " █████╗ ██╗      █████╗  ██████╗ ██████╗██╗ █████╗     ███╗   ███╗ █████╗ ██████╗ ███████╗\r\n" +
            "██╔══██╗██║     ██╔══██╗██╔════╝██╔════╝██║██╔══██╗    ████╗ ████║██╔══██╗██╔══██╗██╔════╝\r\n" +
            "██║  ╚═╝██║     ███████║╚█████╗ ╚█████╗ ██║██║  ╚═╝    ██╔████╔██║██║  ██║██║  ██║█████╗  \r\n" +
            "██║  ██╗██║     ██╔══██║ ╚═══██╗ ╚═══██╗██║██║  ██╗    ██║╚██╔╝██║██║  ██║██║  ██║██╔══╝  \r\n" +
            "╚█████╔╝███████╗██║  ██║██████╔╝██████╔╝██║╚█████╔╝    ██║ ╚═╝ ██║╚█████╔╝██████╔╝███████╗\r\n" +
            " ╚════╝ ╚══════╝╚═╝  ╚═╝╚═════╝ ╚═════╝ ╚═╝ ╚════╝     ╚═╝     ╚═╝ ╚════╝ ╚═════╝ ╚══════╝\n";

    // Timed Mode Message
    public static final String TIMED_MODE_MESSAGE = System.lineSeparator() +
            "████████╗██╗███╗   ███╗███████╗██████╗    ███╗   ███╗ █████╗ ██████╗ ███████╗" + System.lineSeparator() +
            "╚══██╔══╝██║████╗ ████║██╔════╝██╔══██╗   ████╗ ████║██╔══██╗██╔══██╗██╔════╝" + System.lineSeparator() +
            "   ██║   ██║██╔████╔██║█████╗  ██║  ██║   ██╔████╔██║██║  ██║██║  ██║█████╗  " + System.lineSeparator() +
            "   ██║   ██║██║╚██╔╝██║██╔══╝  ██║  ██║   ██║╚██╔╝██║██║  ██║██║  ██║██╔══╝  " + System.lineSeparator() +
            "   ██║   ██║██║ ╚═╝ ██║███████╗██████╔╝   ██║ ╚═╝ ██║╚█████╔╝██████╔╝███████╗" + System.lineSeparator() +
            "   ╚═╝   ╚═╝╚═╝     ╚═╝╚══════╝╚═════╝    ╚═╝     ╚═╝ ╚════╝ ╚═════╝ ╚══════╝" + System.lineSeparator();

    public static final int SEPARATOR_LENGTH = 62;
    public static final String SEPARATOR_SYMBOL = "=";
    public static final String SEPARATOR = SEPARATOR_SYMBOL.repeat(SEPARATOR_LENGTH);

    // Action Messages
    public static final String PLAY = "Play";
    public static final String DISCARD = "Discard";

    // Three second extension for timed mode due to countdown
    public static final long THREE_SECOND_EXTENSION = 3 * 1000;

    // Countdown Numbers
    public static final String COUNTDOWN_THREE = 
            "       █████    ██  \r\n" +
            "      ██   ██   ██  \r\n" +
            "           ██   ██  \r\n" +
            "       ██████   ██  \r\n" +
            "           ██   ██  \r\n" +
            "      ██   ██       \r\n" +
            "       █████    ██  \r\n";

    public static final String COUNTDOWN_TWO = 
            "       █████    ██  \r\n" +
            "      ██   ██   ██  \r\n" +
            "           ██   ██  \r\n" +
            "       █████    ██  \r\n" +
            "      ██        ██  \r\n" +
            "      ██            \r\n" +
            "      ███████   ██  \r\n";

    public static final String COUNTDOWN_ONE = 
            "         ██    ██  \r\n" +
            "       ████    ██  \r\n" +
            "         ██    ██  \r\n" +
            "         ██    ██  \r\n" +
            "         ██    ██  \r\n" +
            "         ██        \r\n" +
            "       ██████  ██  \r\n";

    // ANSI color codes for console text
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_ORANGE = "\u001B[91m"; // Bright red
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_HIDE_CURSOR = "\u001B[?25l";
    public static final String ANSI_SHOW_CURSOR = "\u001B[?25h";

    // Rainbow color array for easy access
    public static final String[] RAINBOW_COLORS = {
            ANSI_RED,
            ANSI_ORANGE,
            ANSI_YELLOW,
            ANSI_GREEN,
            ANSI_BLUE,
            ANSI_PURPLE
    };

    // ASCII art letters for "PARADE"
    public static final String[][] PARADE_LETTERS = {
            { // P"
                    "                              ██████╗ ",
                    "                              ██╔══██╗",
                    "                              ██████╔╝",
                    "                              ██╔═══╝ ",
                    "                              ██║     ",
                    "                              ╚═╝     "
            },
            { // A
                    " █████╗ ",
                    "██╔══██╗",
                    "███████║",
                    "██╔══██║",
                    "██║  ██║",
                    "╚═╝  ╚═╝"
            },
            { // R
                    "██████╗ ",
                    "██╔══██╗",
                    "██████╔╝",
                    "██╔══██╗",
                    "██║  ██║",
                    "╚═╝  ╚═╝"
            },
            { // A
                    " █████╗ ",
                    "██╔══██╗",
                    "███████║",
                    "██╔══██║",
                    "██║  ██║",
                    "╚═╝  ╚═╝"
            },
            { // D
                    "██████╗ ",
                    "██╔══██╗",
                    "██║  ██║",
                    "██║  ██║",
                    "██████╔╝",
                    "╚═════╝ "
            },
            { // E
                    "███████╗",
                    "██╔════╝",
                    "█████╗  ",
                    "██╔══╝  ",
                    "███████╗",
                    "╚══════╝"
            }
    };
}
