public class test {
        // Reset
        public static final String ANSI_RESET = "\u001B[0m";

        // Standard Colors for text
        public static final String BLACK = "\u001B[30m";
        public static final String RED = "\u001B[31m";
        public static final String GREEN = "\u001B[32m";
        public static final String YELLOW = "\u001B[33m";
        public static final String BLUE = "\u001B[34m";
        public static final String PURPLE = "\u001B[35m";
        public static final String CYAN = "\u001B[36m";
        public static final String WHITE = "\u001B[37m";
        public static final String LIGHT_GREEN = "\u001B[92m";
        public static final String EXTENDED_LIGHT_GREEN = "\u001B[38;5;120m";
        public static final String NEON_GREEN = "\u001B[38;2;57;255;20m";

        // Background Colors
        public static final String BLACK_BACKGROUND = "\u001B[40m";
        public static final String RED_BACKGROUND = "\u001B[41m";
        public static final String GREEN_BACKGROUND = "\u001B[42m";
        public static final String YELLOW_BACKGROUND = "\u001B[43m";
        public static final String BLUE_BACKGROUND = "\u001B[44m";
        public static final String PURPLE_BACKGROUND = "\u001B[45m";
        public static final String CYAN_BACKGROUND = "\u001B[46m";
        public static final String WHITE_BACKGROUND = "\u001B[47m";

        public static void main(String[] args) {
            // Demonstrate text colors
            System.out.println(BLACK + "This text is BLACK" + ANSI_RESET);
            System.out.println(RED + "This text is RED" + ANSI_RESET);
            System.out.println(GREEN + "This text is GREEN" + ANSI_RESET);
            System.out.println(YELLOW + "This text is YELLOW" + ANSI_RESET);
            System.out.println(BLUE + "This text is BLUE" + ANSI_RESET);
            System.out.println(PURPLE + "This text is PURPLE" + ANSI_RESET);
            System.out.println(CYAN + "This text is CYAN" + ANSI_RESET);
            System.out.println(WHITE + "This text is WHITE" + ANSI_RESET);
            System.out.println(LIGHT_GREEN + "This is LIGHT GREEN background " + ANSI_RESET);
            System.out.println(EXTENDED_LIGHT_GREEN + "This is EXTENDED LIGHT GREEN background " + ANSI_RESET);
            System.out.println(NEON_GREEN + "This is EXTENDED LIGHT GREEN background " + ANSI_RESET);

            
            System.out.println("\n");
            
            // Demonstrate background colors
            System.out.println(BLACK_BACKGROUND + " This is BLACK background " + ANSI_RESET);
            System.out.println(RED_BACKGROUND + " This is RED background " + ANSI_RESET);
            System.out.println(GREEN_BACKGROUND + " This is GREEN background " + ANSI_RESET);
            System.out.println(YELLOW_BACKGROUND + " This is YELLOW background " + ANSI_RESET);
            System.out.println(BLUE_BACKGROUND + " This is BLUE background " + ANSI_RESET);
            System.out.println(PURPLE_BACKGROUND + " This is PURPLE background " + ANSI_RESET);
            System.out.println(CYAN_BACKGROUND + " This is CYAN background " + ANSI_RESET);
        }

}
