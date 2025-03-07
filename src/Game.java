import java.rmi.StubNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Collections;
import java.util.Iterator;
import java.util.HashSet;
import java.util.TreeMap;

public class Game {

    private final List<HumanPlayer> players;
    private final List<BeginnerComputerPlayer> npcs;

    private Deck deck;
    Scanner scanner = new Scanner(System.in);
    private boolean gameIsOver = false;
    private ArrayList<Card> parade;

    public Game() {
        this.deck = new Deck();
        this.players = new ArrayList<>();
        this.npcs = new ArrayList<>();
    }

    public TreeMap<Integer, ArrayList<Player>> startGame(int numberOfPlayers, int numberOfHumanPlayers) {
        if (numberOfPlayers < 2 || numberOfPlayers > 8) {
            throw new IllegalArgumentException("At least 2 players are required to play the game.");
        }

        if (numberOfHumanPlayers < 0 || numberOfHumanPlayers > numberOfPlayers) {
            throw new IllegalArgumentException("Invalid number of human players.");
        }

        // Initialize players and other game components here...

        // Initialising players
        for (int i = 0; i < numberOfHumanPlayers; i++) {
            // initialise hand for every player
            ArrayList<Card> hand = new ArrayList<Card>();
            for (int j = 0; j < 5; j++) {
                hand.add(deck.drawCard());
            }

            // Create the human player with their own entered name.
            System.out.print("Enter name for human player " + (i + 1) + ": ");
            String inputName = scanner.nextLine().trim();

            // If inputName is empty, we'll simply give them a name like: "Player 1" or
            // "Player 2".
            if (inputName.isEmpty()) {
                inputName = "Player " + (i + 1);
            }

            HumanPlayer p = new HumanPlayer(hand, inputName, scanner);
            players.add(p);
        }

        // Create the computer player with the enum names.
        PlayerNameManager nameManager = new PlayerNameManager();

        // Initialise computer players until total players reach 'numberOfPlayers'
        int currentNumPlayers = players.size();

        while (currentNumPlayers < numberOfPlayers) {
            ArrayList<Card> hand = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                hand.add(deck.drawCard());
            }

            // Assign a unique name from the name manager
            PlayerName computerName = nameManager.assignName();

            // Create a computer player using the assigned name.
            BeginnerComputerPlayer cp = new BeginnerComputerPlayer(hand, computerName.getDisplayName());
            npcs.add(cp);
            currentNumPlayers++;
        }

        // Initialise parade
        parade = new ArrayList<Card>();

        for (int i = 0; i < 6; i++) {
            parade.add(deck.drawCard());
        }

        // Dice rolling logic if the game ONLY has human players.
        int startingIndex;
        if (numberOfHumanPlayers == numberOfPlayers) {
            // Convert players list to a List<Player> if necessary.
            List<Player> allHumanPlayers = new ArrayList<>(players);
            startingIndex = determineStartingPlayerIndex(allHumanPlayers, scanner);
            System.out.println(allHumanPlayers.get(startingIndex).getName() + " will start first.");
        } else {
            // For human vs npc games, just let the first human start.
            startingIndex = 0;
        }
        System.out.println(players.get(startingIndex).getName() + " will start first.");

        int currentPlayerIndex = startingIndex;

        // Combine human and npc players for easy iteration of player turns.
        List<Player> combinedPlayers = new ArrayList<>();
        combinedPlayers.addAll(players);
        combinedPlayers.addAll(npcs);

        ArrayList<Integer> scores = new ArrayList<Integer>();
        for (int i = 0; i < combinedPlayers.size(); i++) {
            scores.add(0);
        }

        while (!gameIsOver) {

            Player currentPlayer = combinedPlayers.get(currentPlayerIndex);
            gameIsOver = turn(currentPlayer, parade, deck);
            // Shift to next players' turn.
            currentPlayerIndex = (currentPlayerIndex + 1) % combinedPlayers.size();
        }

        // The only time we break out of the loop is when the game is over. now, we play
        // one final round, without drawing
        // to stop the drawing mechanic, we set the deck to be empty.

        deck.clearDeck();

        for (int i = 0; i < combinedPlayers.size() - 1; i++) {
            System.out.println("----------FINAL TURN: NO ONE CAN DRAW CARDS----------");

            // everyone EXCEPT the current player index at the last turn will move. no
            // drawing will be done here.
            Player p = combinedPlayers.get((i + currentPlayerIndex) % combinedPlayers.size());
            turn(p, parade, deck);
        }

        // here, the game is over. as per the rules, each player will discard 2 cards
        // from their hand
        System.out.println(
                "-----------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println(
                "---------Choose cards from your hand to discard! Your remaining cards in the hand will be added to the parade, so choose wisely!---------");
        System.out.println(
                "-----------------------------------------------------------------------------------------------------------------------------------------");

        for (Player p : combinedPlayers) {
            if (p instanceof HumanPlayer) {
                HumanPlayer hp = (HumanPlayer) p;
                hp.chooseCardToDiscard();
                hp.chooseCardToDiscard();
            } else {
                BeginnerComputerPlayer bcp = (BeginnerComputerPlayer) p;
                bcp.chooseCardToDiscard();
                bcp.chooseCardToDiscard();
            }

            // we add the rest of their hand into their river.
            ArrayList<Card> currentRiver = p.getRiver();
            ArrayList<Card> hand = p.getHand();
            for (Card c : hand) {
                currentRiver.add(c);
            }
            // print the river for each player, and their name
            Collections.sort(currentRiver, new CardComparator());
            System.out.println(String.format("River for %s: " + currentRiver.toString(), p.getName()));
            System.out.println();
        }

        // we calculate the score for each player
        ScoreCalculator scorer = new ScoreCalculator(combinedPlayers);
        return scorer.getScoreMap();
    }

    public static boolean turn(Player currentPlayer, ArrayList<Card> parade, Deck deck) {

        boolean gameIsOver = false;

        System.out.println("THE PARADE: " + parade.toString());
        System.out.println();

        // Let the current player make their move.
        Card choice = null;
        if (currentPlayer instanceof HumanPlayer) {
            HumanPlayer hp = (HumanPlayer) currentPlayer;
            choice = hp.chooseCardToPlay();
        } else {
            BeginnerComputerPlayer bcp = (BeginnerComputerPlayer) currentPlayer;
            choice = bcp.chooseCardToPlay();
        }

        int choiceValue = choice.getValue();
        Color choiceColor = choice.getColor();

        // Add the current card to the parade.
        parade.add(0, choice);

        // Process the parade for cards to be removed and added to the current player's
        // river.
        ArrayList<Card> currRiver = currentPlayer.getRiver();
        Iterator<Card> iterator = parade.iterator();

        for (int i = 0; i < choiceValue; i++) {
            if (iterator.hasNext()) {
                iterator.next();
            }
        }

        while (iterator.hasNext()) {
            Card checkCard = iterator.next();
            int checkValue = checkCard.getValue();
            Color checkColor = checkCard.getColor();

            if (checkColor.equals(choiceColor) || checkValue <= choiceValue) {
                currRiver.add(checkCard);
                iterator.remove();
            }
        }

        // game ends if the deck is empty OR the current river has one of each color
        if (currRiver.size() != 0) {
            Collections.sort(currRiver, new CardComparator());
        }

        if (currRiver.size() != 0) {
            // create a set to store the colors of the river
            HashSet<Color> checkColor = new HashSet<Color>();
            for (Card c : currRiver) {
                checkColor.add(c.getColor());
            }

            if (checkColor.size() == 6) {
                gameIsOver = true;
            }
        }

        System.out.println(currRiver.toString());

        Card toDraw = deck.drawCard();
        if (toDraw == null) {
            gameIsOver = true;
        } else {
            currentPlayer.drawCard(toDraw);
        }

        return gameIsOver;
    }

    // ------------------------------------------
    // STATIC DICE ROLLING METHODS (helper functions)
    // ------------------------------------------
    public static int determineStartingPlayerIndex(List<Player> players, Scanner scanner) {
        System.out.println("Rolling dice to determine who starts first...");
        List<Integer> diceRolls = new ArrayList<>();

        // Roll dice for each player.
        for (Player p : players) {
            if (p instanceof HumanPlayer) {
                System.out.print(p.getName() + ", press Enter to roll your dice...");
                scanner.nextLine();
            }
            int roll = rollDice();
            System.out.println(p.getName() + " rolled: " + roll);
            diceRolls.add(roll);
        }

        // Find highest roll.
        int highestRoll = Collections.max(diceRolls);
        List<Integer> tiedIndices = new ArrayList<>();
        for (int i = 0; i < diceRolls.size(); i++) {
            if (diceRolls.get(i) == highestRoll) {
                tiedIndices.add(i);
            }
        }

        if (tiedIndices.size() == 1) {
            int startingIndex = tiedIndices.get(0);
            System.out.println("The highest roll was " + highestRoll + ". "
                    + players.get(startingIndex).getName() + " will start first.");
            return startingIndex;
        } else {
            System.out.println("There's a tie between the following players:");
            List<Player> tiedPlayers = new ArrayList<>();
            for (int idx : tiedIndices) {
                tiedPlayers.add(players.get(idx));
                System.out.println("- " + players.get(idx).getName());
            }
            System.out.println("Re-rolling among tied players...");
            Player winner = tieBreaker(tiedPlayers, scanner);
            int finalIndex = players.indexOf(winner);
            System.out.println(winner.getName() + " wins the tie and will start first.");
            return finalIndex;
        }
    }

    private static Player tieBreaker(List<Player> candidates, Scanner scanner) {
        int highestRoll = -1;
        List<Player> winners = new ArrayList<>();

        for (Player p : candidates) {
            if (p instanceof HumanPlayer) {
                System.out.print(p.getName() + ", press Enter to roll your dice for the tie-breaker...");
                scanner.nextLine();
            }
            int roll = rollDice();
            System.out.println(p.getName() + " rolled: " + roll);

            if (roll > highestRoll) {
                highestRoll = roll;
                winners.clear();
                winners.add(p);
            } else if (roll == highestRoll) {
                winners.add(p);
            }
        }

        if (winners.size() == 1) {
            return winners.get(0);
        } else {
            System.out.println("There's still a tie. Re-rolling among tied players...");
            return tieBreaker(winners, scanner);
        }
    }

    private static int rollDice() {
        System.out.println("Dice Rolling.....");

        try {
            // Delay for 1 second (1000 milliseconds)
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); 
            System.err.println("Thread was interrupted, failed to complete delay");
        }

        int rollResult = (int) (Math.random() * 6) + 1;
        printDiceFace(rollResult);

        return rollResult;
    }

    // Dice Roll Ascii Art (this can be changed up later..)
    private static void printDiceFace(int roll) {
        switch (roll) {
            case 1:
                System.out.println("-----");
                System.out.println("|   |");
                System.out.println("| * |");
                System.out.println("|   |");
                System.out.println("-----");
                break;
            case 2:
                System.out.println("-----");
                System.out.println("|*  |");
                System.out.println("|   |");
                System.out.println("|  *|");
                System.out.println("-----");
                break;
            case 3:
                System.out.println("-----");
                System.out.println("|*  |");
                System.out.println("| * |");
                System.out.println("|  *|");
                System.out.println("-----");
                break;
            case 4:
                System.out.println("-----");
                System.out.println("|* *|");
                System.out.println("|   |");
                System.out.println("|* *|");
                System.out.println("-----");
                break;
            case 5:
                System.out.println("-----");
                System.out.println("|* *|");
                System.out.println("| * |");
                System.out.println("|* *|");
                System.out.println("-----");
                break;
            case 6:
                System.out.println("-----");
                System.out.println("|* *|");
                System.out.println("|* *|");
                System.out.println("|* *|");
                System.out.println("-----");
                break;
            default:
                System.out.println("Invalid roll");
        }
    }
}
