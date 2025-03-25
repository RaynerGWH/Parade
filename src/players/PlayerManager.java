package players;

import players.computer.*;
import cards.*;
import players.human.HumanPlayer;
import java.util.*;
import java.util.stream.Collectors;



public class PlayerManager {
    //Managers
    private PlayerNameManager nameManager = new PlayerNameManager();
    private ArrayList<Player> players = new ArrayList<Player>();
    private Deck deck = new Deck();
    private final int HAND_COUNT = 5;
    private Scanner sc = new Scanner(System.in);
    // private Random random = new Random();

    // public void initializeHumanPlayers(Map<Session, Account> sessions) {
    //     for (Session s:sessions.keySet()) {
    //         Account a = sessions.get(s);
    //         String username = a.getUsername();
    //         ArrayList<Card> hand = initialiseHand();
    //         players.add(new HumanPlayer(hand, username, sc, s));
    //     }
    // }

    public void initializeHumanPlayers(int numHumans) {
        for (int i = 0; i < numHumans; i++) {
            System.out.print("Enter name: ");
            String name = sc.nextLine();

            ArrayList<Card> hand = initialiseHand();
            players.add(new HumanPlayer(hand, name, sc));
        }
    }

    public ArrayList<Card> initialiseHand() {
        ArrayList<Card> hand = new ArrayList<Card>();
        for (int i = 0; i < HAND_COUNT; i++) {
            hand.add(deck.drawCard());
        }
        
        return hand;
    }

    public void initializeComputerPlayers(int count) {
        for (int i = 1; i <= count; i++) {
            while (true) {
                try {
                    // if (random.nextInt(20) == 15) {
                    //     //SPAWN YL BOT
                    //     System.out.println("WARNING: YL HAS FOUND OUT A  BOUT THE PARADE GAME. HE WILL NOW BE PLAYING.");
                    //     YLComputer YL  = new YLComputer(initialiseHand());
                    //     players.add(YL);
                    //     continue;
                    // }

                    System.out.println("Enter difficulty of bot " + i + "(Level 1 OR 2)");
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
        System.out.println(players.get(0).getName() + "Will start first");
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