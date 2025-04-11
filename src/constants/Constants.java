package constants;

public class GameplayConstants {
    // Game setup
    public static final int INITIAL_PARADE_LENGTH = 6;

    // Time limits in milliseconds
    public static final long ONE_MINUTE_MILLIS = 60 * 1000;
    public static final long FIVE_MINUTES_MILLIS = 5 * 60 * 1000;
    public static final long TEN_MINUTES_MILLIS = 10 * 60 * 1000;

    // Card limits
    public static final int MAX_HAND_SIZE = 5;
    public static final int INITIAL_HAND_SIZE = 5;

    // In timed mode, how many cards to discard at the end
    public static final int FINAL_DISCARD_COUNT = 2;

    // In timed mode, initial turn bonus
    public static final int INITIAL_TIMED_MODE_TURN_BONUS = 0;

    // Initial player count settings
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 6;

    // Three second extension for timed mode due to countdown
    public static final long THREE_SECOND_EXTENSION = 3 * 1000;
}
