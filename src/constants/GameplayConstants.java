package constants;

public class GameplayConstants {
    // Game setup
    public static final int INITIAL_PARADE_LENGTH = 6;

    // Time limits in milliseconds
    public static final long ONE_SECOND_MILLIS = 1000;
    public static final long ONE_MINUTE_MILLIS = 60 * 1000;
    public static final long FIVE_MINUTES_MILLIS = 5 * 60 * 1000;
    public static final long TEN_MINUTES_MILLIS = 10 * 60 * 1000;

    // Number of seconds until a player times out
    public static final int NUM_SECONDS_TILL_TIMEOUT = 30;

    // Card limits
    public static final int MAX_HAND_SIZE = 5;
    public static final int INITIAL_HAND_SIZE = 5;
    public static final int NUM_DIFF_COLORS_OF_CARDS = 6;

    // Default card choice index for players
    public static final int DEFAULT_CARD_CHOICE_INDEX = 0;

    // In timed mode, how many cards to discard at the end
    public static final int FINAL_DISCARD_COUNT = 2;

    // In timed mode, respective bonus points that a player can gain
    public static final int MAX_BONUS_POINTS = 3;  
    public static final int MEDIUM_BONUS_POINTS = 2;  
    public static final int MIN_BONUS_POINTS = 1;  

    // In timed mode, initial turn bonus
    public static final int INITIAL_TIMED_MODE_TURN_BONUS = 0;

    // Initial player count settings
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 6;

    // Three second extension for timed mode due to countdown
    public static final long THREE_SECOND_EXTENSION = 3 * 1000;

    // Action Messages
    public static final String PLAY = "Play";
    public static final String DISCARD = "Discard";

    // Quantity of Players
    public static final int NUM_PLAYERS_IN_SINGLEPLAYER_MODE = 1;
    public static final int MAX_NUM_PLAYERS = 8;
    public static final int MIN_NUM_PLAYERS = 2;
}
