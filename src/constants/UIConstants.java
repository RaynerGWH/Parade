package constants;

public class UIConstants {
    // UI strings
    public static final String TIME_UP_MESSAGE = "\n                                                 ═══ TIME'S UP! ═══\n";

    // UI Messages
    public static final String PRESS_ENTER_TO_START = "                                              [Enter] Start Game 🕹️ 🕹️";

    // Classic Mode Message
    public static final String CLASSIC_MODE_MESSAGE = "\n" +
            "                       █████╗ ██╗      █████╗  ██████╗ ██████╗██╗ █████╗     ███╗   ███╗ █████╗ ██████╗ ███████╗\r\n" +
            "                       ██╔══██╗██║     ██╔══██╗██╔════╝██╔════╝██║██╔══██╗    ████╗ ████║██╔══██╗██╔══██╗██╔════╝\r\n" +
            "                       ██║  ╚═╝██║     ███████║╚█████╗ ╚█████╗ ██║██║  ╚═╝    ██╔████╔██║██║  ██║██║  ██║█████╗  \r\n" +
            "                       ██║  ██╗██║     ██╔══██║ ╚═══██╗ ╚═══██╗██║██║  ██╗    ██║╚██╔╝██║██║  ██║██║  ██║██╔══╝  \r\n" +
            "                       ╚█████╔╝███████╗██║  ██║██████╔╝██████╔╝██║╚█████╔╝    ██║ ╚═╝ ██║╚█████╔╝██████╔╝███████╗\r\n" +
            "                        ╚════╝ ╚══════╝╚═╝  ╚═╝╚═════╝ ╚═════╝ ╚═╝ ╚════╝     ╚═╝     ╚═╝ ╚════╝ ╚═════╝ ╚══════╝\n";

    // Timed Mode Message
    public static final String TIMED_MODE_MESSAGE = System.lineSeparator() +
            "                       ████████╗██╗███╗   ███╗███████╗██████╗    ███╗   ███╗ █████╗ ██████╗ ███████╗" + System.lineSeparator() +
            "                       ╚══██╔══╝██║████╗ ████║██╔════╝██╔══██╗   ████╗ ████║██╔══██╗██╔══██╗██╔════╝" + System.lineSeparator() +
            "                          ██║   ██║██╔████╔██║█████╗  ██║  ██║   ██╔████╔██║██║  ██║██║  ██║█████╗  " + System.lineSeparator() +
            "                          ██║   ██║██║╚██╔╝██║██╔══╝  ██║  ██║   ██║╚██╔╝██║██║  ██║██║  ██║██╔══╝  " + System.lineSeparator() +
            "                          ██║   ██║██║ ╚═╝ ██║███████╗██████╔╝   ██║ ╚═╝ ██║╚█████╔╝██████╔╝███████╗" + System.lineSeparator() +
            "                          ╚═╝   ╚═╝╚═╝     ╚═╝╚══════╝╚═════╝    ╚═╝     ╚═╝ ╚════╝ ╚═════╝ ╚══════╝" + System.lineSeparator();

    // Separator for UI formatting
    public static final int SEPARATOR_LENGTH = 62;
    public static final String SEPARATOR_SYMBOL = "=";
    public static final String SEPARATOR = SEPARATOR_SYMBOL.repeat(SEPARATOR_LENGTH);

    // Countdown Numbers
    public static final String COUNTDOWN_THREE = 
            "                          █████    ██  \r\n" +
            "                         ██   ██   ██  \r\n" +
            "                              ██   ██  \r\n" +
            "                          ██████   ██  \r\n" +
            "                              ██   ██  \r\n" +
            "                         ██   ██       \r\n" +
            "                          █████    ██  \r\n";

    public static final String COUNTDOWN_TWO = 
            "                          █████    ██  \r\n" +
            "                         ██   ██   ██  \r\n" +
            "                              ██   ██  \r\n" +
            "                          █████    ██  \r\n" +
            "                         ██        ██  \r\n" +
            "                         ██            \r\n" +
            "                         ███████   ██  \r\n";

    public static final String COUNTDOWN_ONE = 
            "                            ██    ██  \r\n" +
            "                          ████    ██  \r\n" +
            "                            ██    ██  \r\n" +
            "                            ██    ██  \r\n" +
            "                            ██    ██  \r\n" +
            "                            ██        \r\n" +
            "                          ██████  ██  \r\n";

    // ASCII art letters for "PARADE"
    public static final String[][] PARADE_LETTERS = {
            { // "P"
                    "                               ██████╗ ",
                    "                               ██╔══██╗",
                    "                               ██████╔╝",
                    "                               ██╔═══╝ ",
                    "                               ██║     ",
                    "                               ╚═╝     "
            },
            { // "A"
                    " █████╗ ",
                    "██╔══██╗",
                    "███████║",
                    "██╔══██║",
                    "██║  ██║",
                    "╚═╝  ╚═╝"
            },
            { // "R"
                    "██████╗ ",
                    "██╔══██╗",
                    "██████╔╝",
                    "██╔══██╗",
                    "██║  ██║",
                    "╚═╝  ╚═╝"
            },
            { // "D"
                    "██████╗ ",
                    "██╔══██╗",
                    "██║  ██║",
                    "██║  ██║",
                    "██████╔╝",
                    "╚═════╝ "
            },
            { // "E"
                    "███████╗",
                    "██╔════╝",
                    "█████╗  ",
                    "██╔══╝  ",
                    "███████╗",
                    "╚══════╝"
            }
        };

        // UI Utils
        public static final String RESET_COLOR = "\u001B[0m";
        public static final String SHOW_CURSOR = "\u001B[?25h";
        public static final String HIDE_CURSOR = "\u001B[?25l";


        // Standard Colors for text
        public static final String BLACK = "\u001B[30m";
        public static final String RED = "\u001B[31m";
        public static final String GREEN = "\u001B[32m";
        public static final String YELLOW = "\u001B[33m";
        public static final String ORANGE = "\u001B[38;2;255;165;0m";
        public static final String BLUE = "\u001B[34m"; // Preferred Darker Blue
        public static final String PURPLE = "\u001B[35m";
        public static final String LIGHT_PURPLE = "\u001B[38;5;183m"; // Preferred Purple
        public static final String CYAN = "\u001B[36m"; // Preferred Light Blue
        public static final String WHITE = "\u001B[37m";
        public static final String CRISP_WHITE = "\u001B[97m";
        public static final String LIGHT_GREEN = "\u001B[92m"; // Preferred Green 
        public static final String EXTENDED_LIGHT_GREEN = "\u001B[38;5;120m";
        public static final String NEON_GREEN = "\u001B[38;2;57;255;20m";
        public static final String GRAY = "\u001B[38;5;238m";

        // RAINBOW COLOR
        // Example rainbow colors array for cycling effect
        public static final String[] RAINBOW_COLORS = {
            RED,  // Red
            YELLOW,  // Yellow
            GREEN,  // Green
            CYAN,  // Cyan
            BLUE,  // Blue
            PURPLE   // Purple
        };

        // Background Colors
        public static final String BLACK_BACKGROUND = "\u001B[40m";
        public static final String RED_BACKGROUND = "\u001B[41m";
        public static final String GREEN_BACKGROUND = "\u001B[42m";
        public static final String YELLOW_BACKGROUND = "\u001B[43m";
        public static final String BLUE_BACKGROUND = "\u001B[44m";
        public static final String PURPLE_BACKGROUND = "\u001B[45m";
        public static final String CYAN_BACKGROUND = "\u001B[46m";
        public static final String WHITE_BACKGROUND = "\u001B[47m";
}