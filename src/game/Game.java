package game;

import cards.*;
import players.*;
import account.Account;
import players.human.HumanPlayer;
import ui.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Collections;
import java.util.Iterator;
import java.util.HashSet;
import java.util.TreeMap;

import java.util.HashMap;
// import java.util.Map;
// import exceptions.DeckEmptyException;
// import exceptions.DuplicateNameException;
// import exceptions.InvalidCardSelectionException;
// import exceptions.InvalidHumanPlayerCountException;
// import exceptions.InvalidPlayerCountException;
// import exceptions.NoAvailableNPCNamesException;

public class Game {
    //ASSUMPTION: GAME is only started by the host, except in singleplayer instances.

    private Deck deck;
    private boolean gameIsOver = false;
    private ArrayList<Card> parade;
    private List<Player> combinedPlayers;
    private final int INITIAL_PARADE_LENGTH = 6;
    private Scanner scanner;

    // Add new timer-related fields
    private boolean timedMode = false;
    private long gameStartTime;
    private long timeLimit; // in milliseconds
    private HashMap<Player, Integer> timeBonus = new HashMap<>();
    

    public Game(ArrayList<Player> players, Scanner scanner) {
        this.deck = new Deck();
        this.combinedPlayers = players;
        this.scanner = scanner;
    }

    public TreeMap<Integer, ArrayList<Player>> startGame() {

        // Game mode selection with validation
        boolean validGameMode = false;
        while (!validGameMode) {
            System.out.println("\nSelect Game Mode:");
            System.out.println("1. Classic Mode");
            System.out.println("2. Timed Mode");
            System.out.println("Enter your choice (1-2): ");
            String gameModeChoice = scanner.nextLine().trim();
            
            if (gameModeChoice.equals("2")) {
                validGameMode = true;
                timedMode = true;
                System.out.println("\nSelect time limit:");
                System.out.println("1. 1-minute blitz");
                System.out.println("2. 5-minute challenge");
                System.out.println("3. 10-minute game");
                
                int timeChoice = 0;
                boolean validChoice = false;
                
                while (!validChoice) {
                    try {
                        System.out.print("Enter your choice (1-3): ");
                        timeChoice = Integer.parseInt(scanner.nextLine().trim());
                        if (timeChoice >= 1 && timeChoice <= 3) {
                            validChoice = true;
                        } else {
                            System.out.println("Please enter a number between 1 and 3.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid number.");
                    }
                }
                
                // Set time limit based on choice
                switch (timeChoice) {
                    case 1:
                        timeLimit = 60 * 1000; // 1 minute in milliseconds
                        System.out.print("\n1-minute blitz selected!\n");
                        break;
                    case 2: 
                        timeLimit = 5 * 60 * 1000; // 5 minutes in milliseconds
                        System.out.print("\n5-minute challenge selected!\n");
                        break;
                    case 3:
                        timeLimit = 10 * 60 * 1000; // 10 minutes in milliseconds
                        System.out.print("\n10-minute game selected!\n");
                        break;
                }
                
                // Don't initialize with placeholders, wait for actual players
                timeBonus = new HashMap<>();
            } else if (gameModeChoice.equals("1")) {
                validGameMode = true;
                System.out.print("\nClassic Mode selected!\n");
                timedMode = false;
            } else {
                System.out.println("\nInvalid choice. Please select 1 for Classic Mode or 2 for Timed Mode.");
            }
        }

        // Initialise parade
        parade = new ArrayList<Card>();

        for (int i = 0; i < INITIAL_PARADE_LENGTH; i++) {
            parade.add(deck.drawCard());
        }

        int currentPlayerIndex = 0;

        // Initialize time bonus map with actual players
        if (timedMode) {
            timeBonus.clear();
            for (Player player : combinedPlayers) {
                timeBonus.put(player, 0);
            }
            
            // Start the timer
            gameStartTime = System.currentTimeMillis();
            
            // Display time limit
            System.out.print("\n--- TIMED MODE ACTIVE ---");
            System.out.print("Time limit: " + (timeLimit / 60000) + " minutes");
            System.out.print("Bonus points will be awarded for quick moves!");
            System.out.print("---------------------------\n");
        }

        ArrayList<Integer> scores = new ArrayList<Integer>();
        for (int i = 0; i < combinedPlayers.size(); i++) {
            scores.add(0);
        }

        // Turn function
        while (!gameIsOver) {
            for (int i = 0; i < combinedPlayers.size(); i++) {
                System.out.print(combinedPlayers.get(i).getName());
            }
            Player currentPlayer = combinedPlayers.get(currentPlayerIndex);
            
            // Check if time has run out in timed mode
            if (timedMode && System.currentTimeMillis() - gameStartTime >= timeLimit) {
                System.out.print("\n--- TIME'S UP! ---");
                gameIsOver = true;
                break;
            }
            
            // Track time for this player's move
            long turnStartTime = System.currentTimeMillis();
            
            gameIsOver = turn(currentPlayer, parade, deck);
            
            // Calculate time bonus if in timed mode
            if (timedMode) {
                long turnDuration = System.currentTimeMillis() - turnStartTime;
                int bonus = calculateTimeBonus(turnDuration);
                int currentBonus = timeBonus.get(currentPlayer);
                timeBonus.put(currentPlayer, currentBonus + bonus);
                
                if (bonus > 0) {
                    System.out.print("\n" + currentPlayer.getName() + " gets " + bonus + " time bonus points for a quick move!");
                }
                
                // Show progress bar with remaining time
                long elapsedTime = System.currentTimeMillis() - gameStartTime;
                displayTimeProgressBar(elapsedTime);
                System.out.print("\n"); // Add extra spacing
            }
            
            // Shift to next players' turn.
            currentPlayerIndex = (currentPlayerIndex + 1) % combinedPlayers.size();
        }

        // The only time we break out of the loop is when the game is over. now, we play
        // one final round, without drawing
        // to stop the drawing mechanic, we set the deck to be empty.

        deck.clearDeck();

        // Bring out the FINAL TURN print, then call a separate function afterwards for per round
        for (int i = 0; i < combinedPlayers.size() - 1; i++) {
            System.out.print("-------------------------------------------------------------------------------------------------------------------------");
            System.out.print("--------------------------------------------FINAL TURN: NO ONE CAN DRAW CARDS--------------------------------------------");
            System.out.print("-------------------------------------------------------------------------------------------------------------------------");

            // everyone EXCEPT the current player index at the last turn will move. no
            // drawing will be done here.
            Player p = combinedPlayers.get((i + currentPlayerIndex) % combinedPlayers.size());
            turn(p, parade, deck);
        }

        // here, the game is over. as per the rules, each player will discard 2 cards
        // from their hand
        System.out.print(
                "-----------------------------------------------------------------------------------------------------------------------------------------");
        System.out.print(
                "---------Choose cards from your hand to discard! The remaining cards in your hand will be added to your river, so choose wisely!---------");
        System.out.print(
                "-----------------------------------------------------------------------------------------------------------------------------------------");
        System.out.print("\n");  // Add extra spacing before starting

        for (Player p : combinedPlayers) {
                p.chooseCardToDiscard();
                System.out.print("\n");  // Add spacing between first and second discard
                p.chooseCardToDiscard();

            // we add the rest of their hand into their river.
            ArrayList<Card> currentRiver = p.getRiver();
            ArrayList<Card> hand = p.getHand();
            for (Card c : hand) {
                currentRiver.add(c);
            }
            // print the river for each player, and their name
            Collections.sort(currentRiver, new CardComparator());
            System.out.print("\n");  // Add spacing before showing river
            System.out.print(String.format("River for %s: " + currentRiver.toString(), p.getName()));
            System.out.print("\n");
            
            // // Add separation line between players (except for the last player)
            // if (p != combinedPlayers.get(combinedPlayers.size() - 1)) {
            //     "----------------------------------------");
            //     System.out.println();
            // }
        }

        // we calculate the score for each player
        ScoreCalculator scorer = new ScoreCalculator(combinedPlayers);
        TreeMap<Integer, ArrayList<Player>> scoreMap = scorer.getScoreMap();
        
        // Add GAME OVER banner
        System.out.print("\n");
        System.out.print(" _____                        _____                 ");
        System.out.print("|  __ \\                      |  _  |                ");
        System.out.print("| |  \\/ __ _ _ __ ___   ___  | | | |_   _____ _ __  ");
        System.out.print("| | __ / _` | '_ ` _ \\ / _ \\ | | | \\ \\ / / _ \\ '__| ");
        System.out.print("| |_\\ \\ (_| | | | | | |  __/ \\ \\_/ /\\ V /  __/ |    ");
        System.out.print(" \\____/\\__,_|_| |_| |_|\\___|  \\___/  \\_/ \\___|_|    ");
        System.out.print("                                                     ");
        System.out.print("\n");
        
        // Apply time bonuses if in timed mode
        if (timedMode) {
            System.out.print("\n--- TIME BONUS POINTS ---");
            
            // Apply log2 bonus for all players
            for (Player player : combinedPlayers) {
                int bonus = timeBonus.get(player);
                if (bonus > 0) {
                    // Calculate log2 deduction (rounded down to nearest integer)
                    int logDeduction = (int) (Math.log(bonus) / Math.log(2));  // log2(x) = ln(x)/ln(2)
                    System.out.print(player.getName() + " earned " + bonus + " bonus points, resulting in a " + 
                                     logDeduction + " point deduction!");
                    
                    // Find the player in the scoreMap and adjust their score
                    for (Integer score : scoreMap.keySet()) {
                        ArrayList<Player> playersWithScore = scoreMap.get(score);
                        if (playersWithScore.contains(player)) {
                            playersWithScore.remove(player);
                            if (playersWithScore.isEmpty()) {
                                scoreMap.remove(score);
                            }
                            
                            // Apply log2 bonus (negative score is better in Parade)
                            int newScore = score - logDeduction;
                            
                            // Add player to the new score
                            if (!scoreMap.containsKey(newScore)) {
                                scoreMap.put(newScore, new ArrayList<>());
                            }
                            scoreMap.get(newScore).add(player);
                            break;
                        }
                    }
                }
            }
            
            // Add a line break after all bonuses are displayed
            System.out.print("\n");
        }
        
        // Return the scoreMap so RunGame.java can use it
        return scoreMap;
    }

    public boolean turn(Player currentPlayer, ArrayList<Card> parade, Deck deck) {
        boolean gameIsOver = false;
        Card choice = null;

        //print the following only for human players. bots -> dont care
        if (currentPlayer instanceof HumanPlayer) {
            HumanPlayer hp = (HumanPlayer)currentPlayer;
            s = hp.getSession();
            //Send the player object across to sync the client instance with the server instance
            // gse.sendToCurrentPlayer(currentPlayer,s);

            ui.displayMessage("─────────────────────────────────────", s);
            ui.displayMessage("THE PARADE: " + parade, s);
            ui.displayMessage("─────────────────────────────────────", s);

            ui.displayMessage(hp.displayHand(), s);

            ui.displayMessage("-----Your turn-----", s);

            try {
                String playerInput = InputManager.waitForInput();
                int i = Integer.parseInt(playerInput);
                choice = currentPlayer.playCard(i);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                //Default: choice = 0;
                choice = currentPlayer.playCard(0);
            }
        } else {
            choice = currentPlayer.chooseCardToPlay();
        }
        
        
        System.out.print(currentPlayer.getName() + " played: " + choice.toString());

        int choiceValue = choice.getValue();
        Color choiceColor = choice.getColor();
        
        // Add the current card to the parade.
        parade.add(0, choice);

        // Process the parade for cards to be removed and added to the current player's
        // river.
        ArrayList<Card> currRiver = currentPlayer.getRiver();
        Iterator<Card> iterator = parade.iterator();
        List<Card> takenCards = new ArrayList<Card>();

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
                takenCards.add(checkCard);
                iterator.remove();
            }
        }

        // Display which cards were taken
        if (!takenCards.isEmpty()) {
            System.out.print(currentPlayer.getName() + " takes the following cards from the parade: " + takenCards);
        } else {
            System.out.print(currentPlayer.getName() + " takes no cards from the parade!");
        }
        System.out.print("\n");

        // game ends if the deck is empty OR the current river has one of each color
        if (currRiver.size() != 0) {
            Collections.sort(currRiver, new CardComparator());
        }

        // Change to one function instead
        if (currRiver.size() != 0) {
            // create a set to store the colors of the river, avoid duplicates
            HashSet<Color> checkColor = new HashSet<Color>();
            for (Card c : currRiver) {
                checkColor.add(c.getColor());
            }

            // if per river size == 6
            if (checkColor.size() == 6) {
                gameIsOver = true;
            }
        }
        
        Card toDraw = deck.drawCard();
        if (toDraw == null) {
            gameIsOver = true;
        } else {
            currentPlayer.drawCard(toDraw);
        }

        System.out.print(currentPlayer.getName() + "'s River: " + currRiver.toString());

        return gameIsOver;
    }

    // Calculate time bonus based on how quickly the player made their move
    private int calculateTimeBonus(long turnDuration) {
        // For a 1-minute game, quick moves should be rewarded more
        if (timeLimit <= 60 * 1000) { // 1-minute game
            if (turnDuration < 3000) return 3; // 3 points for moves under 3 seconds
            if (turnDuration < 6000) return 2; // 2 points for moves under 6 seconds
            if (turnDuration < 10000) return 1; // 1 point for moves under 10 seconds
        } else { // For longer games
            if (turnDuration < 5000) return 3; // 3 points for moves under 5 seconds
            if (turnDuration < 10000) return 2; // 2 points for moves under 10 seconds
            if (turnDuration < 15000) return 1; // 1 point for moves under 15 seconds
        }
        return 0;
    }
    
    // Format milliseconds to mm:ss format
    private String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // Add this new method to the Game class
    private void displayTimeProgressBar(long elapsedTime) {
        int barLength = 30; // Length of the progress bar
        double progress = Math.min(1.0, (double) elapsedTime / timeLimit); // Cap at 1.0 (100%)
        int filledBars = (int) (progress * barLength);
        
        // Create the progress bar string
        StringBuilder progressBar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            if (i < filledBars) {
                progressBar.append("█");
            }
            // } else if (i == filledBars && progress < 1.0) { // Only show > if not at 100%
            //     progressBar.append(">");
            // } 
            else {
                progressBar.append(" ");
            }
        }
        progressBar.append("]");
        
        // Calculate remaining time
        long remainingTime = Math.max(0, timeLimit - elapsedTime);
        String timeString = formatTime(remainingTime);
        
        // Calculate percentage (capped at 100)
        int percentage = Math.min(100, (int) (progress * 100));
        
        // Print the progress bar and time
        System.out.print("\nTime remaining: " + timeString);
        System.out.print(progressBar.toString() + " " + percentage + "%");
    }
}
