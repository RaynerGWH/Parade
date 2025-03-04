
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

        for (int i = 0; i < 6; i++) {
            parade.add(deck.drawCard());
        }

        System.out.println(parade.toString());

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

        while (!gameIsOver) {

            Player currentPlayer = combinedPlayers.get(currentPlayerIndex);

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
            parade.add(choice);
            
            // Process the parade for cards to be removed and added to the current player's
            // river.
            ArrayList<Card> currRiver = currentPlayer.getRiver();
            for (int i = choiceValue; i < parade.size(); i++) {
                Card checkCard = parade.get(i);
                int checkValue = checkCard.getValue();
                Color checkColor = checkCard.getColor();
                if (checkColor.equals(choiceColor) || checkValue <= choiceValue) {
                    // TODO: remove card from parade (or create a copy) and add to currentPlayer's river
                }
            }

            // old code (because now we are gna iterate through the list of players and go to next turn after every while loop iteration)
            // ============================
            // we iterate until we flag the game is over
            // for (Player p : combinedPlayers) {
            // // player p makes a move
            // Card choice = null;
            // if (p instanceof HumanPlayer) {
            // HumanPlayer hp = (HumanPlayer) p;
            // choice = hp.chooseCardToPlay();
            // } else {
            // BeginnerComputerPlayer bcp = (BeginnerComputerPlayer) p;
            // choice = bcp.chooseCardToPlay();
            // }

            // int choiceValue = choice.getValue();
            // Color choiceColor = choice.getColor();

            // we now check the parade, highlighting cards that should be removed and added
            // to your river
            // ArrayList<Card> currRiver = p.getRiver();

            // for removal of the cards, we count up (VALUE) cards (excluding the card we
            // played)
            // from there onwards, the cards that have
            // a) a lower value than our card
            // b) same color as our card
            // will be added to the player's river

            // ============================

            // TODO: implement checks for game end logic.

            // Shift to next players' turn.
            currentPlayerIndex = (currentPlayerIndex + 1) % combinedPlayers.size();
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
    // STATIC DICE ROLLING METHODS (helper functions)
    // ------------------------------------------
    public static int determineStartingPlayerIndex(List<Player> players, Scanner scanner) {
        System.out.println("Rolling dice to determine who starts first...");
        List<Integer> diceRolls = new ArrayList<>();

        // Roll dice for each player.
        for (Player p : players) {
            if (p instanceof HumanPlayer) {
                System.out.println(p.getName() + ", press Enter to roll your dice...");
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
                System.out.println(p.getName() + ", press Enter to roll your dice for the tie-breaker...");
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
        return (int) (Math.random() * 6) + 1;
    }
}