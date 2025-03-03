import java.util.*;

/**
 * The Game class manages the main logic: deck creation/shuffling, dealing hands,
 * turns, card collection, and scoring.
 * It can run in single-player (human vs. NPC) or multiplayer mode.
 */
class Game {

    private static final int INITIAL_HAND_SIZE = 5;

    private final List<Player> players;
    private final List<Card> cardDeck;
    private final List<Card> parade;
    private int currentPlayerIndex;

    // ANSI Colors (duplicated here for convenience)
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";   // ADDED
    public static final String ANSI_PURPLE = "\u001B[35m"; // ADDED
    public static final String ANSI_CYAN = "\u001B[36m";

    /**
     * Constructs a Game.
     * @param singlePlayer If true, one human player plus an NPC player.
     * @param numberOfPlayers The number of human players (ignored if singlePlayer=true except must be >=1).
     */
    public Game(boolean singlePlayer, int numberOfPlayers) {
        this.players = new ArrayList<>();
        if (singlePlayer) {
            // Single-player mode: 1 real player + 1 NPC
            if (numberOfPlayers < 1) {
                throw new IllegalArgumentException("At least 1 human player is required in single-player mode.");
            }
            // Create one human
            players.add(new Player("You"));
            // Create one NPC
            players.add(new NPCPlayer("Mr. Whiskers"));
        } else {
            // Multiplayer mode
            if (numberOfPlayers < 2) {
                throw new IllegalArgumentException("At least 2 players are required for multiplayer.");
            }
            for (int i = 1; i <= numberOfPlayers; i++) {
                players.add(new Player("Player " + i));
            }
        }

        // Create and shuffle the deck
        this.cardDeck = createDeck();
        Collections.shuffle(this.cardDeck);

        // Initialize the parade and first player's turn
        this.parade = new ArrayList<>();
        this.currentPlayerIndex = 0;
    }

    /**
     * Builds the 66 cards (6 colors x 11 values) for the game.
     */
    private List<Card> createDeck() {
        final List<Card> newDeck = new ArrayList<>();
        for (Color color : Color.values()) {
            for (int value = 0; value <= 10; value++) {
                newDeck.add(new Card(color, value));
            }
        }
        return newDeck;
    }

    /**
     * Orchestrates the main game flow: dealing, turns, collecting, and scoring.
     */
    public void startGame(Scanner scanner) {
        dealInitialHands();
        startParade();
        runGameLoop(scanner);
        endRound();
    }

    /**
     * Deals the initial hands of 5 cards to each player.
     */
    private void dealInitialHands() {
        for (int i = 0; i < INITIAL_HAND_SIZE; i++) {
            for (Player player : players) {
                if (!cardDeck.isEmpty()) {
                    player.getHand().add(cardDeck.remove(0));
                }
            }
        }
    }

    /**
     * Places the top card of the deck into the parade to begin.
     */
    private void startParade() {
        if (!cardDeck.isEmpty()) {
            Card firstCard = cardDeck.remove(0);
            parade.add(firstCard);
            System.out.println(ANSI_CYAN + "The Parade begins with: " + firstCard + ANSI_RESET);
        }
    }

    /**
     * Main loop that continues until the deck is empty.
     */
    private void runGameLoop(Scanner scanner) {
        while (!cardDeck.isEmpty()) {
            final Player currentPlayer = players.get(currentPlayerIndex);

            System.out.println("\n" + ANSI_BOLD + currentPlayer.getName() + "'s turn" + ANSI_RESET);
            System.out.println("Your hand:");
            currentPlayer.showHand();

            // Different logic if the current player is an NPC
            final int chosenIndex;
            if (currentPlayer instanceof NPCPlayer) {
                chosenIndex = ((NPCPlayer) currentPlayer).chooseCardToPlay();
            } else {
                chosenIndex = getCardChoice(scanner, currentPlayer);
            }

            final Card chosenCard = currentPlayer.getHand().remove(chosenIndex);
            parade.add(chosenCard);
            System.out.println(ANSI_YELLOW + currentPlayer.getName() + " played " + chosenCard + ANSI_RESET);

            collectMatchingCards(chosenCard, currentPlayer);
            drawNewCard(currentPlayer);
            advanceToNextPlayer();
        }
    }

    /**
     * Prompts the user to pick a valid card index from their hand.
     */
    private int getCardChoice(Scanner scanner, Player player) {
        int choice = -1;
        while (choice < 0 || choice >= player.getHand().size()) {
            System.out.print(ANSI_BOLD + "Choose a card to play (0-" + (player.getHand().size() - 1) + "): " + ANSI_RESET);
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume leftover newline
            } else {
                scanner.nextLine(); // discard invalid input
            }
        }
        return choice;
    }

    /**
     * Draws the next card from the deck (if available) for the current player.
     */
    private void drawNewCard(Player currentPlayer) {
        if (!cardDeck.isEmpty()) {
            final Card drawnCard = cardDeck.remove(0);
            currentPlayer.getHand().add(drawnCard);
            System.out.println(ANSI_CYAN + currentPlayer.getName() + " draws a card." + ANSI_RESET);
        }
    }

    /**
     * Moves turn control to the next player in the sequence.
     */
    private void advanceToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    /**
     * Implements the Parade rule for collecting cards:
     * From the left to right, skip 'value' cards after the newly placed card,
     * then collect any card matching the placed card's color or having a value <= that card.
     */
    private void collectMatchingCards(Card playedCard, Player currentPlayer) {
        final int playedIndex = parade.size() - 1;
        final int skipCount = playedCard.getValue();

        final List<Integer> toCollectIndices = new ArrayList<>();
        int countSkipped = 0;
        boolean startedCollecting = false;

        for (int i = 0; i < parade.size(); i++) {
            if (i == playedIndex) {
                continue; // skip the card just played
            }

            if (!startedCollecting) {
                if (countSkipped < skipCount) {
                    countSkipped++;
                } else {
                    startedCollecting = true;
                }
            } else {
                final Card candidate = parade.get(i);
                if (candidate.getColor() == playedCard.getColor() || candidate.getValue() <= playedCard.getValue()) {
                    toCollectIndices.add(i);
                }
            }
        }

        // Sort indices in reverse so removal doesn't shift subsequent indices
        toCollectIndices.sort(Collections.reverseOrder());
        for (int idx : toCollectIndices) {
            final Card removed = parade.remove(idx);
            currentPlayer.getCollectedCards().add(removed);
            System.out.println(ANSI_RED + currentPlayer.getName() + " collected " + removed + ANSI_RESET);
        }
    }

    /**
     * Called when the deck is empty. Calculates scores, determines majorities, and announces results.
     */
    private void endRound() {
        System.out.println("\n" + ANSI_BOLD + "--- The Parade has ended! Tallying points... ---" + ANSI_RESET);

        final Map<Player, Map<Color, Integer>> colorCounts = tallyColorCounts();
        final Map<Color, Integer> maxColorCount = findMaxColorCounts(colorCounts);

        computePenaltyPoints(colorCounts, maxColorCount);
        displayResults();
    }

    /**
     * Builds a map of how many cards of each Color each player has collected.
     */
    private Map<Player, Map<Color, Integer>> tallyColorCounts() {
        final Map<Player, Map<Color, Integer>> colorCounts = new HashMap<>();
        for (Player player : players) {
            final Map<Color, Integer> counts = new HashMap<>();
            for (Card card : player.getCollectedCards()) {
                counts.put(card.getColor(), counts.getOrDefault(card.getColor(), 0) + 1);
            }
            colorCounts.put(player, counts);
        }
        return colorCounts;
    }

    /**
     * Determines the maximum number of cards of each color that any player holds.
     */
    private Map<Color, Integer> findMaxColorCounts(Map<Player, Map<Color, Integer>> colorCounts) {
        final Map<Color, Integer> maxColorCount = new HashMap<>();
        for (Color color : Color.values()) {
            int maxCount = 0;
            for (Player player : players) {
                final int count = colorCounts.get(player).getOrDefault(color, 0);
                if (count > maxCount) {
                    maxCount = count;
                }
            }
            maxColorCount.put(color, maxCount);
        }
        return maxColorCount;
    }

    /**
     * Calculates each player's final penalty points, with majorities counting as 1 point per card.
     */
    private void computePenaltyPoints(Map<Player, Map<Color, Integer>> colorCounts,
                                      Map<Color, Integer> maxColorCount) {
        for (Player player : players) {
            int totalPoints = 0;
            for (Card card : player.getCollectedCards()) {
                final int playerColorCount = colorCounts.get(player).getOrDefault(card.getColor(), 0);
                final int maxCount = maxColorCount.get(card.getColor());

                if (playerColorCount == maxCount && maxCount != 0) {
                    // Majority in that color -> 1 point per card
                    totalPoints += 1;
                } else {
                    // Otherwise, face value
                    totalPoints += card.getValue();
                }
            }
            player.setScore(totalPoints);
        }
    }

    /**
     * Displays each player's score and announces the winner.
     */
    private void displayResults() {
        Player winner = players.get(0);
        for (Player player : players) {
            System.out.println(ANSI_BOLD + player.getName() + "'s points = " + player.getScore() + ANSI_RESET);
            if (player.getScore() < winner.getScore()) {
                winner = player;
            }
        }
        System.out.println(ANSI_GREEN + "\nRound winner: " + winner.getName() + ANSI_RESET);
    }
}