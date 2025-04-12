package players;

import players.computer.*;
import cards.*;
import players.human.HumanPlayer;
import account.Account;
import cards.Deck;
import players.Player;
import players.computer.BeginnerComputerPlayer;
import players.computer.IntermediateComputerPlayer;
import players.computer.PlayerNameManager;
import account.*;
import java.util.*;
import java.util.stream.Collectors;

import constants.UIConstants;

import jakarta.websocket.*;

public class PlayerManager {
    //Managers
    private PlayerNameManager nameManager = new PlayerNameManager();
    private ArrayList<Player> players = new ArrayList<Player>();
    private Deck deck = new Deck();
    private final int HAND_COUNT = 5;
    private Scanner sc = new Scanner(System.in);
    // private Random random = new Random();

    public void initializeHumanPlayers(Map<Session, Account> sessions, boolean isMulti) {
        Map<String, Integer> usernames = new HashMap<String, Integer>();
        for (Map.Entry<Session, Account> entry : sessions.entrySet()) {
            Session s = entry.getKey();
            Account a = entry.getValue();
            String username = a.getUsername();

            usernames.put(username, 1);

            ArrayList<Card> hand = initialiseHand();
            HumanPlayer humanPlayer = new HumanPlayer(hand, username, s, sc);
            
            // Set the account on the player so it can be accessed for display purposes
            humanPlayer.setAccount(a);
            
            players.add(humanPlayer);
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
                    System.out.print(UIConstants.RESET_COLOR + "\nEnter difficulty of bot " + i + " (Level 1 or 2)" + UIConstants.ConsoleInput);
                    String difficulty = sc.nextLine();
                    int diffLvl = Integer.parseInt(difficulty);
                    Player bot = checkDiff(diffLvl);
                    players.add(bot);
                    break;

                } catch (NumberFormatException e) {
                    System.out.println(UIConstants.RESET_COLOR + "\n❌ Please enter a valid number!");
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

    public ArrayList<Player> getPlayers() {
        return players;
    }
}
