package constants;

public class UIConstants {
    // UI strings
    public static final String TIME_UP_MESSAGE = "\n═══ TIME'S UP! ═══\n";

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

    // Action Messages
    public static final String PLAY = "Play";
    public static final String DISCARD = "Discard";

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
            }
    }
}  