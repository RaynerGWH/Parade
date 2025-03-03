

import java.util.Random;

/**
 * Extended console art for the cat NPC with improved alignment and random 5-character faces.
 * Shaped like a friendly kitty. The doc comment here is purely descriptive.
 */
class NPCPlayer extends Player {

    private static final Random RANDOM = new Random();

    // Personality lines for the cat NPC.
    private static final String[] PERSONALITY_LINES = {
        "Purrr... I'm going to play this card, meow!",
        "Nyaa~ Let's see if you can keep up with me, human.",
        "Meow-meow! I sense a paw-sibility of victory.",
        "I'll just bat this card into place, nya hehe.",
        "Did I do well? I'm just a cute cat, don't judge me.",
        "Meeoow, let's shuffle and see what happens!"
    };

    // A set of 5-character emoticons to randomize.
    // Each includes parentheses + exactly 3 chars inside, for perfect 5 chars total.
    private static final String[] CAT_FACES = {
        "(O.o)",
        "(o.o)",
        "(O_O)",
        "(-_-)",
        "(^_^)",
        "(>.<)"
    };

    /**
     * Builds cat ASCII art dynamically by inserting a random cat face into the second line.
     * The spacing aims to form a neat kitty shape.
     */
    private String buildCatASCII() {
        // Pick a random face
        String face = CAT_FACES[RANDOM.nextInt(CAT_FACES.length)];
        // Combine the ASCII lines, ensuring proper escaping of backslashes
        return Game.ANSI_PURPLE
            + "   /\\___/\\\n"           // Ears:   /\___/\
            + "  (  " + face + "  )\n"   // Insert our random face
            + "   =\\~/=\n"              // Mouth   =\~/=
            + "  /  ^  \\\n"             // Body
            + " (  ( )  )\n"
            + "  \\  ~  /\n"
            + "   \\`-'/\n"
            + "    /_\\\n"
            + Game.ANSI_RESET;
    }

    public NPCPlayer(String name) {
        super(name);
    }

    /**
     * Chooses a card to play from the NPC's hand automatically.
     */
    public int chooseCardToPlay() {
        final int size = getHand().size();
        if (size == 0) {
            return 0; // no card to play, fallback
        }

        // NPC chooses a random card index
        final int chosenIndex = RANDOM.nextInt(size);

        // Generate the kitty ASCII with a random face
        final String catASCII = buildCatASCII();
        // Pick a random personality line
        final String line = PERSONALITY_LINES[RANDOM.nextInt(PERSONALITY_LINES.length)];

        // Print the cat art and dialogue
        System.out.println(catASCII);
        System.out.println(Game.ANSI_PURPLE + getName() + " [NPC Cat]: " + line + Game.ANSI_RESET);

        return chosenIndex;
    }
}
