
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Random;
import java.util.Collections;

public class Game {

    private final List<HumanPlayer> players;
    private final List<BeginnerComputerPlayer> npcs;

    private Deck deck = new Deck();
    Scanner scanner = new Scanner(System.in);
    private boolean gameIsOver = false;
    private ArrayList<Card> parade;

    public Game(int numberOfPlayers, int numberOfHumanPlayers) {

        if (numberOfPlayers < 2 || numberOfPlayers > 8) {
            throw new IllegalArgumentException("At least 2 players are required to play the game.");
        }

        if (numberOfHumanPlayers < 0 || numberOfHumanPlayers > numberOfPlayers) {
            throw new IllegalArgumentException("Invalid number of human players.");
        }

        players = new ArrayList<>();

        Deck deck = new Deck();

        // Initialize players and other game components here...

        // Initialising players
        for (int i = 0; i < numberOfHumanPlayers; i++) {
            // initialise hand for every player
            ArrayList<Card> hand = new ArrayList<Card>();
            for (int j = 0; j < 5; j++) {
                hand.add(deck.drawCard());
            }

            // Create the human player with their own entered name.
            System.out.println("Enter name for human player " + (i + 1) + ":");
            String inputName = scanner.nextLine().trim();

            // If inputName is empty, we'll simply give them a name like: "Player 1" or "Player 2".
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
        npcs = new ArrayList<>();

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
        for (

                int i = 0; i < 6; i++) {
            parade.add(deck.drawCard());
        }

        System.out.println(parade.toString());

        // TODO: IMPLEMENT DECIDE PLAYER LOGIC

        Random random = new Random();
        Collections.shuffle(players, random);

        while (!gameIsOver) {

            // we iterate until we flag the game is over
            for (Player p : players) {
                // player p makes a move
                Card choice = null;
                if (p instanceof HumanPlayer) {
                    HumanPlayer hp = (HumanPlayer) p;
                    choice = hp.chooseCardToPlay();
                } else {
                    BeginnerComputerPlayer bcp = (BeginnerComputerPlayer) p;
                    choice = bcp.chooseCardToPlay();
                }

                int choiceValue = choice.getValue();
                Color choiceColor = choice.getColor();

                // we now check the parade, highlighting cards that should be removed and added
                // to your river
                ArrayList<Card> currRiver = p.getRiver();

                // for removal of the cards, we count up (VALUE) cards (excluding the card we
                // played)
                // from there onwards, the cards that have
                // a) a lower value than our card
                // b) same color as our card
                // will be added to the player's river

                for (int i = choiceValue; i < parade.size(); i++) {
                    Card checkCard = parade.get(i);
                    int checkValue = checkCard.getValue();
                    Color checkColor = checkCard.getColor();

                    if (checkColor.equals(choiceColor) || checkValue <= choiceValue) {
                        // TODO: remove card from parade(most likely need to create a copy of it), add
                        // to the current player's river
                    }
                }

                // add the current card to the parade
                parade.add(choice);

                // TODO: implement checks for game end logic
            }
        }
    }

    // public void setCard() {
    // if (!deck.isEmpty()) {
    // Card newCard = deck.drawCard();
    // players.drawCard(newCard);
    // }
    // }
    // Other methods...
    public static void main(String[] args) {
        System.out.println("Welcome to the Parade Card Game!");
        Scanner scanner = new Scanner(System.in);
        // single plyaer or multiplayer (fancy console art)

        System.out.println("Would you like to play Single Player or Multi Player");
        //
        System.out.print("Enter 'R' to refer to the rulebook: ");
        System.out.println("Enter 'S' to start the game!");
        String command = scanner.nextLine().trim().toUpperCase();
        if (command.equals("R")) {
            scrollRulebook("rulebook.txt");

        } else {
            System.out.println("Command not recognized.");
        }
    }

    public static void scrollRulebook(String filePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            Scanner scanner = new Scanner(System.in);
            int linesPerPage = 15;
            int totalPages = (int) Math.ceil((double) lines.size() / linesPerPage);
            int currentPage = 0;
            String input;

            while (true) {
                // Display the current page
                int start = currentPage * linesPerPage;
                int end = Math.min(start + linesPerPage, lines.size());
                System.out.println("\nPage " + (currentPage + 1) + " of " + totalPages + ":");
                for (int i = start; i < end; i++) {
                    System.out.println(lines.get(i));
                }

                // Prompt user for input
                System.out.print("\nEnter (N)ext, (P)revious, or (Q)uit: ");
                input = scanner.nextLine().trim().toUpperCase();

                switch (input) {
                    case "N":
                        if (currentPage < totalPages - 1) {
                            currentPage++;
                        } else {
                            System.out.println("This is the last page.");
                        }
                        break;
                    case "P":
                        if (currentPage > 0) {
                            currentPage--;
                        } else {
                            System.out.println("This is the first page.");
                        }
                        break;
                    case "Q":
                        System.out.println("Exiting rulebook.");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid input. Please try again.");
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading rulebook file: " + e.getMessage());
        }
    }

    // ------------------------------------------
    // STATIC DICE ROLLING METHODS
    // ------------------------------------------

    /*
     * Rolls dice for each player and returns the index of the player
     * who wins. If there's a tie, it re-rolls among the tied players.
     */
    // public static int determineStartingPlayerIndex(List<Player> players, Scanner
    // scanner) {
    // System.out.println("Rolling dice to determine who starts first...");
    // List<Integer> diceRolls = new ArrayList<>();

    // // Roll dice for each player
    // for (Player p : players) {
    // // Only prompt if the player is human (optional check, if you want AI to skip
    // // input)
    // if (p instanceof HumanPlayer) {
    // System.out.println(p.getName() + ", press Enter to roll your dice...");
    // scanner.nextLine();
    // }
    // int roll = rollDice();
    // System.out.println(p.getName() + " rolled: " + roll);
    // diceRolls.add(roll);
    // }

    // // Find the highest roll
    // int highestRoll = Collections.max(diceRolls);

    // // Collect all players who matched the highest roll
    // List<Integer> tiedIndices = new ArrayList<>();
    // for (int i = 0; i < diceRolls.size(); i++) {
    // if (diceRolls.get(i) == highestRoll) {
    // tiedIndices.add(i);
    // }
    // }

    // // If there's only one highest roller, return that index
    // if (tiedIndices.size() == 1) {
    // int startingIndex = tiedIndices.get(0);
    // System.out.println("The highest roll was " + highestRoll + ". "
    // + players.get(startingIndex).getName() + " will start first.");
    // return startingIndex;
    // } else {
    // // Tie among multiple players — re-roll among tied players
    // System.out.println("There's a tie between the following players:");
    // List<Player> tiedPlayers = new ArrayList<>();
    // for (int idx : tiedIndices) {
    // tiedPlayers.add(players.get(idx));
    // System.out.println("- " + players.get(idx).getName());
    // }
    // System.out.println("Re-rolling among tied players...");

    // // Determine the single winner among the tied players
    // Player winner = tieBreaker(tiedPlayers, scanner);

    // int finalIndex = players.indexOf(winner);
    // System.out.println(winner.getName() + " wins the tie and will start first.");
    // return finalIndex;
    // }
    // }

    /**
     * Re-roll logic for tie-breakers among a subset of players.
     */
    // private static Player tieBreaker(List<Player> candidates, Scanner scanner) {
    // int highestRoll = -1;
    // List<Player> winners = new ArrayList<>();

    // for (Player p : candidates) {
    // if (p instanceof HumanPlayer) {
    // System.out.println(p.getName() + ", press Enter to roll your dice for the
    // tie-breaker...");
    // scanner.nextLine();
    // }
    // int roll = rollDice();
    // System.out.println(p.getName() + " rolled: " + roll);

    // if (roll > highestRoll) {
    // highestRoll = roll;
    // winners.clear();
    // winners.add(p);
    // } else if (roll == highestRoll) {
    // winners.add(p);
    // }
    // }

    // if (winners.size() == 1) {
    // return winners.get(0);
    // } else {
    // // Still a tie
    // System.out.println("There's still a tie. Re-rolling among tied players...");
    // return tieBreaker(winners, scanner);
    // }
    // }

    /**
     * Simulates rolling a six-sided die.
     */
    // private static int rollDice() {
    // return (int) (Math.random() * 6) + 1;
    // }

    // // ------------------------------------------
    // // HELPER METHODS
    // // ------------------------------------------

    // /**
    // * Checks if all players are human.
    // */
    // private boolean isAllHumanPlayers(List<Player> players) {
    // for (Player p : players) {
    // if (!(p instanceof HumanPlayer)) {
    // return false;
    // }
    // }
    // return true;
    // }

    // /**
    // * Finds the first human player in the list and returns their index.
    // * If no human player is found, returns 0 (or throw an exception, as needed).
    // */
    // private int getFirstHumanPlayerIndex(List<Player> players) {
    // for (int i = 0; i < players.size(); i++) {
    // if (players.get(i) instanceof HumanPlayer) {
    // return i;
    // }
    // }
    // // If there's no human at all, handle accordingly (could throw, or just
    // return
    // // 0)
    // return 0;
    // }

}
