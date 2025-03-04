
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
    // private final List<BeginnerComputerPlayer> computer;

    private Deck deck = new Deck();
    Scanner scanner = new Scanner(System.in);
    private boolean gameIsOver = false;
    private ArrayList<Card> parade;

    public Game(int numberOfPlayers) {
        if (numberOfPlayers < 2 || numberOfPlayers > 8) {
            throw new IllegalArgumentException("At least 2 players are required to play the game.");
        }
        players = new ArrayList<>();

        Deck deck = new Deck();

        // Initialize players and other game components here...

        //Initialising players
        for (int i = 0; i < numberOfPlayers; i++) {
            //initialise hand for every player
            ArrayList<Card> hand = new ArrayList<Card>();
            for (int j = 0; j < 5; j++) {
                hand.add(deck.drawCard());
            }
            
            //initialise name(name is picked based on the Enum, corresponding to the idx of the player)
            PlayerName name = PlayerName.values()[i];
            HumanPlayer p = new HumanPlayer(hand, name, scanner);
            players.add(p);
        }

        //Initialise parade
        parade = new ArrayList<Card>();
        for (int i = 0; i < 6; i++) {
            parade.add(deck.drawCard());
        }

        System.out.println(parade.toString());
        
        //TODO: IMPLEMENT DECIDE PLAYER LOGIC

        Random random = new Random();
        Collections.shuffle(players,random);

        while (!gameIsOver) {
            
            //we iterate until we flag the game is over
            for (Player p:players) {
                //player p makes a move
                Card choice = null;
                if (p instanceof HumanPlayer) {
                    HumanPlayer hp = (HumanPlayer)p;
                    choice = hp.chooseCardToPlay();
                } else {
                    BeginnerComputerPlayer bcp = (BeginnerComputerPlayer)p;
                    choice = bcp.chooseCardToPlay();
                }

                int choiceValue = choice.getValue();
                Color choiceColor = choice.getColor();

                //we now check the parade, highlighting cards that should be removed and added to your river
                ArrayList<Card> currRiver = p.getRiver();

                //for removal of the cards, we count up (VALUE) cards (excluding the card we played)
                //from there onwards, the cards that have
                //a) a lower value than our card
                //b) same color as our card
                //will be added to the player's river

                
                for (int i = choiceValue; i < parade.size(); i++) {
                    Card checkCard = parade.get(i);
                    int checkValue = checkCard.getValue();
                    Color checkColor = checkCard.getColor();

                    if (checkColor.equals(choiceColor) || checkValue <= choiceValue) {
                        //TODO: remove card from parade(most likely need to create a copy of it), add to the current player's river
                    }
                }

                //add the current card to the parade
                parade.add(choice);


                //TODO: implement checks for game end logic
            }
        }
    }   

    // public void setCard() {
    //     if (!deck.isEmpty()) {
    //         Card newCard = deck.drawCard();
    //         players.drawCard(newCard);
    //     }
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

    // private int determineStartingPlayer(Scanner scanner) {
    //     System.out.println("Rolling dice to determine who starts first...");
    //     // Roll dice for all players and store the results.
    //     List<Integer> diceRolls = new ArrayList<>();
    //     for (Player p : players) {
    //         System.out.println(p.getName() + ", press Enter to roll your dice...");
    //         scanner.nextLine();  // Wait for Enter key press.
    //         int roll = rollDice();
    //         System.out.println(p.getName() + " rolled: " + roll);
    //         diceRolls.add(roll);
    //     }
    //     // Find the highest roll using a loop.
    //     int highestRoll = -1;
    //     for (int roll : diceRolls) {
    //         if (roll > highestRoll) {
    //             highestRoll = roll;
    //         }
    //     }
    //     // Collect indices of players who tied with the highest roll.
    //     List<Integer> tiedIndices = new ArrayList<>();
    //     for (int i = 0; i < diceRolls.size(); i++) {
    //         if (diceRolls.get(i) == highestRoll) {
    //             tiedIndices.add(i);
    //         }
    //     }
    //     // If there's only one highest roller, they start.
    //     if (tiedIndices.size() == 1) {
    //         int startingIndex = tiedIndices.get(0);
    //         System.out.println("The highest roll was " + highestRoll + ". " 
    //                 + players.get(startingIndex).getName() + " will start first.");
    //         return startingIndex;
    //     } else {
    //         // If there's a tie, display the tied players.
    //         System.out.println("There's a tie between:");
    //         List<Player> tiedPlayers = new ArrayList<>();
    //         for (int idx : tiedIndices) {
    //             Player p = players.get(idx);
    //             tiedPlayers.add(p);
    //             System.out.println("- " + p.getName());
    //         }
    //         System.out.println("Re-rolling among tied players...");
    //         Player winner = determineStartingPlayer(tiedPlayers, scanner);
    //         int finalIndex = players.indexOf(winner);
    //         System.out.println(winner.getName() + " wins the tie and will start first.");
    //         return finalIndex;
    //     }
    // }
    // // Overloaded helper method to handle tie-breakers among a subset of players interactively.
    // private Player determineStartingPlayer(List<Player> candidates, Scanner scanner) {
    //     int highestRoll = -1;
    //     List<Player> winners = new ArrayList<>();
    //     // Each candidate rolls a die interactively.
    //     for (Player p : candidates) {
    //         System.out.println(p.getName() + ", press Enter to roll your dice for the tie-breaker...");
    //         scanner.nextLine();
    //         int roll = rollDice();
    //         System.out.println(p.getName() + " rolled: " + roll);
    //         if (roll > highestRoll) {
    //             highestRoll = roll;
    //             winners.clear();
    //             winners.add(p);
    //         } else if (roll == highestRoll) {
    //             winners.add(p);
    //         }
    //     }
    //     // If one candidate has the highest roll, return that candidate.
    //     if (winners.size() == 1) {
    //         return winners.get(0);
    //     } else {
    //         // If there is still a tie, re-roll among the winners.
    //         System.out.println("There is still a tie. Re-rolling among tied players...");
    //         return determineStartingPlayer(winners, scanner);
    //     }
    // }
    // // Helper method to simulate a dice roll (returns a value between 1 and 6).
    // private int rollDice() {
    //     return (int) (Math.random() * 6) + 1;
    // }
    // int currentPlayerIndex = startingIndex;  // starting index determined earlier

    // while (!gameIsOver()) {
    //     Player currentPlayer = players.get(currentPlayerIndex);
    //     // Let the current player take their turn.

    //     // Then update the current player index:
    //     currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    // }

}
