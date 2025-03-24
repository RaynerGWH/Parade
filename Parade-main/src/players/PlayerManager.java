package players;

import players.computer.*;
import cards.*;
import exceptions.DeckEmptyException;
import exceptions.DuplicateNameException;
import exceptions.NoAvailableNPCNamesException;
import players.human.HumanPlayer;
import Account.*;
import java.util.*;
import java.util.stream.Collectors;

import Account.Account;


public class PlayerManager {
    //Managers
    private PlayerNameManager nameManager = new PlayerNameManager();
    private ArrayList<Player> players = new ArrayList<Player>();
    private Deck deck = new Deck();
    private final int STARTING_HAND_SIZE = 5;
    private Scanner sc = new Scanner(System.in);
    // private Random random = new Random();
    
    private ArrayList<Account> accounts;
    
    public PlayerManager(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    public void initializeHumanPlayers() throws DuplicateNameException{

        // HashSet to store all initalised player names.
        Set<String> currentNames = new HashSet<>();

        for (int i = 0; i < accounts.size(); i++) {
            String username = accounts.get(i).getUsername();
            if (username.isEmpty()) {
                username = "Player " + (i + 1);
            }

            // If returned false when adding username to the HashSet of currentNames, we would know that the set already contains it.
            if (!currentNames.add(username)) {
                throw new DuplicateNameException("Duplicate username found: " + username);
            }

            ArrayList<Card> hand = initialiseHand();

            players.add(new HumanPlayer(hand, username, sc));
        }
    }

    public ArrayList<Card> initialiseHand() throws DeckEmptyException{
        ArrayList<Card> hand = new ArrayList<Card>();
        for (int i = 0; i < STARTING_HAND_SIZE; i++) {
            Card cardToAddToHand = deck.drawCard(); // Throws DeckEmptyException if deck is empty.
            hand.add(cardToAddToHand);
        }
        
        return hand;
    }

    public void initializeComputerPlayers(int count) throws NoAvailableNPCNamesException, DeckEmptyException{
        for (int i = 1; i <= count; i++) {
            while (true) {
                try {
                    // if (random.nextInt(20) == 15) {
                    //     //SPAWN YL BOT
                    //     System.out.println("WARNING: YL HAS FOUND OUT ABOUT THE PARADE GAME. HE WILL NOW BE PLAYING.");
                    //     YLComputer YL  = new YLComputer(initialiseHand());
                    //     players.add(YL);
                    //     continue;
                    // }

                    System.out.print("Enter difficulty of bot " + i + " (Level 1 or 2): ");
                    String difficulty = sc.nextLine();
                    int diffLvl = Integer.parseInt(difficulty);
                    Player bot = checkDiff(diffLvl);
                    players.add(bot);
                    break;

                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number!");
                }
            }
        }
    }

    public Player checkDiff(int diffLvl) throws NumberFormatException{
        switch (diffLvl) {
            case 1:
                return new BeginnerComputerPlayer(initialiseHand(), nameManager.assignName().getDisplayName());
            case 2:
                return new IntermediateComputerPlayer(initialiseHand(), nameManager.assignName().getDisplayName());
            default:
                throw new NumberFormatException();
        }
    }

    public void setTurnOrder(int count) {
        if (count == 0) {
            reorderPlayersByStartingPlayer(players, sc);
        } else {
            Collections.shuffle(players);
        }
        System.out.println(players.get(0).getName() + " will start first");
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public static void reorderPlayersByStartingPlayer(List<Player> players, Scanner scanner) {
    System.out.println("Rolling dice to determine who starts first...");
    Map<Player, Integer> playerRolls = new HashMap<>();

    // Roll dice for each player
    for (Player p : players) {
        if (p instanceof HumanPlayer) {
            System.out.print(p.getName() + ", press Enter to roll your dice...");
            scanner.nextLine();
        }
        int roll = rollDice();
        System.out.println(p.getName() + " rolled: " + roll);
        playerRolls.put(p, roll);
    }

    // Find maximum roll value
    int maxRoll = Collections.max(playerRolls.values());
    List<Player> candidates = playerRolls.entrySet().stream()
            .filter(entry -> entry.getValue() == maxRoll)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

    Player startingPlayer;
    if (candidates.size() == 1) {
        startingPlayer = candidates.get(0);
        System.out.println("The highest roll was " + maxRoll + ".");
    } else {
        System.out.println("Tie between: " + candidates.stream()
                .map(Player::getName)
                .collect(Collectors.joining(", ")));
        startingPlayer = resolveTie(candidates, scanner);
    }

    // Reorder the list to put starting player first
    if (players.get(0) != startingPlayer) {
        players.remove(startingPlayer);
        players.add(0, startingPlayer);
    }
}

private static Player resolveTie(List<Player> candidates, Scanner scanner) {
    Map<Player, Integer> tieBreakerRolls = new HashMap<>();
    System.out.println("Re-rolling tie-breaker...");

    for (Player p : candidates) {
        if (p instanceof HumanPlayer) {
            System.out.print(p.getName() + ", press Enter to roll tie-breaker...");
            scanner.nextLine();
        }
        int roll = rollDice();
        System.out.println(p.getName() + " rolled: " + roll);
        tieBreakerRolls.put(p, roll);
    }

    int maxRoll = Collections.max(tieBreakerRolls.values());
    List<Player> winners = tieBreakerRolls.entrySet().stream()
            .filter(entry -> entry.getValue() == maxRoll)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

    if (winners.size() == 1) {
        return winners.get(0);
    }
    System.out.println("Still tied: " + winners.stream()
            .map(Player::getName)
            .collect(Collectors.joining(", ")));
    return resolveTie(winners, scanner);
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