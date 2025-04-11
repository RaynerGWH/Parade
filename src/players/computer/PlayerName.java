package players.computer;

// Note: The game will only support up to EIGHT players. 
// Hence, only 8 available player names.

public enum PlayerName {
    ALICE("Alice"),
    THE_HATTER("The Hatter"),
    CHESHIRE("Cheshire"),
    WHITE_RABBIT("White Rabbit"),
    QUEEN_OF_HEARTS("Queen of Hearts"),
    CATERPILLAR("Caterpillar"),
    MARCH_HARE("March Hare"),
    DORMOUSE("Dormouse");


    // Declaring it as final guarantees that once it is assigned a value (in the constructor), 
    // that value cannot be changed. 
    // This is important because each enum constant should have a constant, unmodifiable display name 
    // throughout the lifetime of the program.
    private final String DISPLAYNAME;

    PlayerName(String displayName) {
        this.DISPLAYNAME = displayName;
    }

    public String getDisplayName() {
        return DISPLAYNAME;
    }
}