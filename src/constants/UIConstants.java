package constants;

public class UIConstants {

        // General Standard Colors
        public static final String BLACK = "\u001B[30m";
        public static final String RED = "\u001B[31m";
        public static final String GREEN = "\u001B[32m";
        public static final String YELLOW = "\u001B[33m";
        public static final String ORANGE = "\u001B[38;2;255;165;0m";
        public static final String BLUE = "\u001B[34m"; // Preferred Darker Blue
        public static final String PURPLE = "\u001B[35m";
        public static final String LIGHT_PURPLE = "\u001B[38;5;183m"; // Preferred Purple
        public static final String CYAN = "\u001B[38;5;111m"; // Preferred Light Blue
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

        // Color Utils
        public static final String TEXT_COLOR = "\u001B[38;5;183m";
        public static final String FLOWER_COLOR = "\u001B[38;5;183m";
        public static final String BORDER_COLOR = "\u001B[38;5;189m";

        // UI Utils
        public static final String RESET_COLOR = "\u001B[0m";
        public static final String SHOW_CURSOR = "\u001B[?25h";
        public static final String HIDE_CURSOR = "\u001B[?25l";
        public static final String ConsoleInput = TEXT_COLOR + "\n> ";

        // UI strings
        public static final String TIME_UP_MESSAGE = "\n                                                 ═══ TIME'S UP! ═══\n";

        // UI Messages
        public static final String PRESS_ENTER_TO_START = RESET_COLOR + "                                              [Enter] Start Game 🕹️ 🕹️\n\n";

        // Classic Mode Message
        public static final String CLASSIC_MODE_MESSAGE = "\n" +
                "                  █████╗ ██╗      █████╗  ██████╗ ██████╗██╗ █████╗     ███╗   ███╗ █████╗ ██████╗ ███████╗\r\n" +
                "                 ██╔══██╗██║     ██╔══██╗██╔════╝██╔════╝██║██╔══██╗    ████╗ ████║██╔══██╗██╔══██╗██╔════╝\r\n" +
                "                 ██║  ╚═╝██║     ███████║╚█████╗ ╚█████╗ ██║██║  ╚═╝    ██╔████╔██║██║  ██║██║  ██║█████╗  \r\n" +
                "                 ██║  ██╗██║     ██╔══██║ ╚═══██╗ ╚═══██╗██║██║  ██╗    ██║╚██╔╝██║██║  ██║██║  ██║██╔══╝  \r\n" +
                "                 ╚█████╔╝███████╗██║  ██║██████╔╝██████╔╝██║╚█████╔╝    ██║ ╚═╝ ██║╚█████╔╝██████╔╝███████╗\r\n" +
                "                  ╚════╝ ╚══════╝╚═╝  ╚═╝╚═════╝ ╚═════╝ ╚═╝ ╚════╝     ╚═╝     ╚═╝ ╚════╝ ╚═════╝ ╚══════╝\n\n";

        // Timed Mode Message
        public static final String TIMED_MODE_MESSAGE = "\n" +
                "                        ████████╗██╗███╗   ███╗███████╗██████╗    ███╗   ███╗ █████╗ ██████╗ ███████╗\r\n" +
                "                        ╚══██╔══╝██║████╗ ████║██╔════╝██╔══██╗   ████╗ ████║██╔══██╗██╔══██╗██╔════╝\r\n" +
                "                           ██║   ██║██╔████╔██║█████╗  ██║  ██║   ██╔████╔██║██║  ██║██║  ██║█████╗  \r\n" +
                "                           ██║   ██║██║╚██╔╝██║██╔══╝  ██║  ██║   ██║╚██╔╝██║██║  ██║██║  ██║██╔══╝  \r\n" +
                "                           ██║   ██║██║ ╚═╝ ██║███████╗██████╔╝   ██║ ╚═╝ ██║╚█████╔╝██████╔╝███████╗\r\n" +
                "                           ╚═╝   ╚═╝╚═╝     ╚═╝╚══════╝╚═════╝    ╚═╝     ╚═╝ ╚════╝ ╚═════╝ ╚══════╝\r\n\n";

        // Separator for UI formatting
        public static final int SEPARATOR_LENGTH = 62;
        public static final String SEPARATOR_SYMBOL = "=";
        public static final String SEPARATOR = SEPARATOR_SYMBOL.repeat(SEPARATOR_LENGTH);

        // Countdown Numbers
        public static final String COUNTDOWN_THREE = LIGHT_GREEN +
                "                                                         █████    ██  \r\n" +
                "                                                         ██   ██   ██  \r\n" +
                "                                                              ██   ██  \r\n" +
                "                                                          ██████   ██  \r\n" +
                "                                                              ██   ██  \r\n" +
                "                                                              ██   ██       \r\n" +
                "                                                          █████    ██  \r\n";

        public static final String COUNTDOWN_TWO = YELLOW + 
                "                                                          █████    ██  \r\n" +
                "                                                         ██   ██   ██  \r\n" +
                "                                                              ██   ██  \r\n" +
                "                                                          █████    ██  \r\n" +
                "                                                         ██        ██  \r\n" +
                "                                                         ██            \r\n" +
                "                                                         ███████   ██  \r\n";

        public static final String COUNTDOWN_ONE = RED + 
                "                                                            ██    ██  \r\n" +
                "                                                          ████    ██  \r\n" +
                "                                                            ██    ██  \r\n" +
                "                                                            ██    ██  \r\n" +
                "                                                            ██    ██  \r\n" +
                "                                                            ██        \r\n" +
                "                                                          ██████  ██  \r\n" + RESET_COLOR;

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

                // Card
                public static final String CARD_LOGO = "❀";


                // Border
                public static final String BORDER_FLOWER = FLOWER_COLOR + "❀*̥˚" + BORDER_COLOR;
                public static final String LOGIN_BORDER = (BORDER_COLOR + "                          ^~^  \r\n" + //
                "                         ('Y')                                                          ^ ^\r\n" + //
                "                         /   \\/    ‿︵‿︵୨˚̣̣୧ " + TEXT_COLOR + "🎴 Welcome to the Parade! 🎭" + BORDER_COLOR + " ୨˚̣̣୧‿︵‿︵    (O,O)\r\n" + //
                "                        (\\|||/)                                                        (   )\r\n" + //
                "                    ╔══════════ " + BORDER_FLOWER + "═══ " + BORDER_FLOWER + "════════════════════════════════════════════════════╗\r\n" + //
                "                    ║ ┌─────────────────────────────────────────────────────────────────────┐ ║\r\n" + // 
                "                    ║ │                                                                     │ ║\r\n" + //    
                "                    ║ │   " + TEXT_COLOR + "Adventurer, you've arrived! " + BORDER_COLOR + "                                      │ ║\r\n" + //
                "                    ║ │                                                                     │ ║\r\n" + //
                "                    ║ │   " + TEXT_COLOR + "✨ Here's what awaits you:  " + BORDER_COLOR + "                                      │ ║\r\n" + //
                "                    ║ │   " + TEXT_COLOR + "🎨 Flair Shop — Win battles to unlock stylish customizations" + BORDER_COLOR + "      │ ║\r\n" + //
                "                    ║ │   " + TEXT_COLOR + "⏱️  Timed Mode — Outsmart the clock in fast-paced duels" + BORDER_COLOR + "            │ ║\r\n" + //
                "                    ║ │   " + TEXT_COLOR + "🎮 Gamemodes — Play solo, versus friends, or challenge smart bots" + BORDER_COLOR + " │ ║\r\n" + //
                "                    ║ │   " + TEXT_COLOR + "🌈 Visual Delight — Immerse yourself in vibrant, animated art   " + BORDER_COLOR + "  │ ║\r\n" + //
                "                    ║ │                                                                     │ ║\r\n" + //
                "                    ║ └─────────────────────────────────────────────────────────────────────┘ ║\r\n" + //
                "                    ╚═════════════════════════════════════════════════════ " + BORDER_FLOWER + "═══ " + BORDER_FLOWER + "═════════╝\r\n");

                public static final String MAIN_SCREEN = BORDER_COLOR + "\n" + 
                "                                ／＞　 フ  \r\n" + 
                "                               " + CYAN + "❀*̥" + BORDER_COLOR + "　_　_|   " + CYAN + " 🎴 Welcome, adventurer!"  + "!\r\n" + BORDER_COLOR + 
                "                              ／` ミ＿xノ  \r\n" + BORDER_COLOR +
                "                             /　　　　 | " + CYAN + "    Shuffle your fate... the Parade awaits your hand ~  \r\n" + BORDER_COLOR +
                "                            /　 ヽ　　 ﾉ\r\n\n\n" + RESET_COLOR +
                "                            [S] Start Game 🎮                  [B] Buy Flairs ✨\r\n\n" +
                "                            [R] Read Rulebook 📖               [Q] Quit ❌\r\n";

                public static final String GAMEMODE_SCREEN = 
                BORDER_COLOR + "      ___                 \r\n" +
                "     (o o)                            " + TEXT_COLOR + "🌙 One step further adventurer..." + BORDER_COLOR + " \r\n" +
                "    (  V  )\r\n" +
                "------m-m----------" + BORDER_FLOWER + "---- " + BORDER_FLOWER + "--------------------------------" + BORDER_FLOWER + BORDER_FLOWER + BORDER_FLOWER + BORDER_FLOWER + BORDER_FLOWER + BORDER_FLOWER + BORDER_FLOWER + "\r\n";

}