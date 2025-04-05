package game;

import cards.*;
import players.*;
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

import jakarta.websocket.Session;

// Import the Account and AccountFileManager classes so we can access flair info and load accounts
import account.Account;
import account.AccountFileManager;

public class Game {
    // ASSUMPTION: GAME is only started by the host, except in singleplayer instances.

    private Deck deck;
    private boolean gameIsOver = false;
    private ArrayList<Card> parade;
    private List<Player> combinedPlayers;
    private final int INITIAL_PARADE_LENGTH = 6;
    private UserInterface ui;
    private GameServerEndpoint gse;
    private Scanner scanner;

    // Timer-related fields for timed mode
    private boolean timedMode = false;
    private long gameStartTime;
    private long timeLimit; // in milliseconds
    private HashMap<Player, Integer> timeBonus = new HashMap<>();

    public Game(ArrayList<Player> players, UserInterface ui, GameServerEndpoint gse, Scanner scanner) {
        this.deck = new Deck();
        this.combinedPlayers = players;
        this.ui = ui;
        this.gse = gse;
        this.scanner = scanner;
    }

    /**
     * Returns a player's display name with flair appended (if available).
     */
    private String getDisplayName(Player player) {
        if (player instanceof HumanPlayer) {
            HumanPlayer hp = (HumanPlayer) player;
            Account account = hp.getAccount();
            if (account != null) {
                List<String> flairs = account.getUnlockedFlairs();
                if (flairs != null && !flairs.isEmpty()) {
                    return account.getUsername() + " [" + flairs.get(0) + "]";
                } else {
                    return account.getUsername();
                }
            }
        }
        return player.getName();
    }
    
    public TreeMap<Integer, ArrayList<Player>> startGame() {
        // Assign accounts to HumanPlayers if not already set
        for (Player p : combinedPlayers) {
            if (p instanceof HumanPlayer) {
                HumanPlayer hp = (HumanPlayer) p;
                if (hp.getAccount() == null) {
                    AccountFileManager accountFileManager = new AccountFileManager(scanner);
                    Account loadedAccount = accountFileManager.initialize();
                    hp.setAccount(loadedAccount);
                }
            }
        }
        
        // Game mode selection with validation
        boolean validGameMode = false;
        while (!validGameMode) {
            System.out.println("Game Modes available:");
            System.out.println("    1. Classic");
            System.out.println("    2. Timed");
            System.out.print("Enter '1' or '2'\n> ");
            String gameModeChoice = scanner.nextLine().trim();

            if (gameModeChoice.equals("2")) {
                validGameMode = true;
                timedMode = true;
                System.out.print("\n████████╗██╗███╗   ███╗███████╗██████╗    ███╗   ███╗ █████╗ ██████╗ ███████╗\r\n" + 
                        "╚══██╔══╝██║████╗ ████║██╔════╝██╔══██╗   ████╗ ████║██╔══██╗██╔══██╗██╔════╝\r\n" + 
                        "   ██║   ██║██╔████╔██║█████╗  ██║  ██║   ██╔████╔██║██║  ██║██║  ██║█████╗  \r\n" + 
                        "   ██║   ██║██║╚██╔╝██║██╔══╝  ██║  ██║   ██║╚██╔╝██║██║  ██║██║  ██║██╔══╝  \r\n" + 
                        "   ██║   ██║██║ ╚═╝ ██║███████╗██████╔╝   ██║ ╚═╝ ██║╚█████╔╝██████╔╝███████╗\r\n" + 
                        "   ╚═╝   ╚═╝╚═╝     ╚═╝╚══════╝╚═════╝    ╚═╝     ╚═╝ ╚════╝ ╚═════╝ ╚══════╝\n");
                System.out.print("════════════════════════════════════════════════════════════════════════════");
                System.out.println("\nCategories:");
                System.out.println("    1. 1-minute blitz");
                System.out.println("    2. 5-minute challenge");
                System.out.println("    3. 10-minute game");

                int timeChoice = 0;
                boolean validChoice = false;

                while (!validChoice) {
                    try {
                        System.out.print("Enter category (1-3)\n> ");
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

                timeBonus = new HashMap<>();
            } else if (gameModeChoice.equals("1")) {
                validGameMode = true;
                System.out.print(
                        "\n █████╗ ██╗      █████╗  ██████╗ ██████╗██╗ █████╗     ███╗   ███╗ █████╗ ██████╗ ███████╗\r\n"
                                + "██╔══██╗██║     ██╔══██╗██╔════╝██╔════╝██║██╔══██╗    ████╗ ████║██╔══██╗██╔══██╗██╔════╝\r\n"
                                + "██║  ╚═╝██║     ███████║╚█████╗ ╚█████╗ ██║██║  ╚═╝    ██╔████╔██║██║  ██║██║  ██║█████╗  \r\n"
                                + "██║  ██╗██║     ██╔══██║ ╚═══██╗ ╚═══██╗██║██║  ██╗    ██║╚██╔╝██║██║  ██║██║  ██║██╔══╝  \r\n"
                                + "╚█████╔╝███████╗██║  ██║██████╔╝██████╔╝██║╚█████╔╝    ██║ ╚═╝ ██║╚█████╔╝██████╔╝███████╗\r\n"
                                + " ╚════╝ ╚══════╝╚═╝  ╚═╝╚═════╝ ╚═════╝ ╚═╝ ╚════╝     ╚═╝     ╚═╝ ╚════╝ ╚═════╝ ╚══════╝\n");
                timedMode = false;
            } else {
                System.out.println("\nInvalid choice. Please select 1 for Classic Mode or 2 for Timed Mode.");
            }
        }

        // Initialize parade with initial cards
        parade = new ArrayList<Card>();
        for (int i = 0; i < INITIAL_PARADE_LENGTH; i++) {
            parade.add(deck.drawCard());
        }

        int currentPlayerIndex = 0;
        if (timedMode) {
            timeBonus.clear();
            for (Player player : combinedPlayers) {
                timeBonus.put(player, 0);
            }
            gameStartTime = System.currentTimeMillis();
            ui.broadcastMessage("\n═══ TIMED MODE ACTIVE ═══\n");
            ui.broadcastMessage("Time limit: " + (timeLimit / 60000) + " minutes\n");
            ui.broadcastMessage("Bonus points will be awarded for quick moves!\n");
            ui.broadcastMessage("═══════════════════════════\n");
        }

        ArrayList<Integer> scores = new ArrayList<Integer>();
        for (int i = 0; i < combinedPlayers.size(); i++) {
            scores.add(0);
        }

        // Main turn loop
        while (!gameIsOver) {
            Player currentPlayer = combinedPlayers.get(currentPlayerIndex);

            // Timed mode: check time limit
            if (timedMode && System.currentTimeMillis() - gameStartTime >= timeLimit) {
                ui.broadcastMessage("\n═══ TIME'S UP! ═══\n");
                gameIsOver = true;
                break;
            }

            // Track time for this player's move
            long turnStartTime = System.currentTimeMillis();

            // Process the current turn (the console is cleared and the full game state is displayed)
            gameIsOver = turn(currentPlayer, parade, deck, "Plays");

            // Calculate time bonus if in timed mode
            if (timedMode) {
                long turnDuration = System.currentTimeMillis() - turnStartTime;
                int bonus = calculateTimeBonus(turnDuration);
                int currentBonus = timeBonus.get(currentPlayer);
                timeBonus.put(currentPlayer, currentBonus + bonus);

                if (bonus > 0) {
                    ui.broadcastMessage("\n" + getDisplayName(currentPlayer) + " gets " + bonus + " time bonus points for a quick move!");
                }

                long elapsedTime = System.currentTimeMillis() - gameStartTime;
                ui.broadcastMessage(displayTimeProgressBar(elapsedTime));
                ui.broadcastMessage("\n");
            }

            // Next player's turn
            currentPlayerIndex = (currentPlayerIndex + 1) % combinedPlayers.size();
        }

        // Final round and scoring code remains as before...
        deck.clearDeck();
        for (int i = 0; i < combinedPlayers.size() - 1; i++) {
            ui.broadcastMessage("══════════════════════════════════════════════════════════════");
            ui.broadcastMessage("FINAL TURN: NO ONE CAN DRAW CARDS");
            ui.broadcastMessage("══════════════════════════════════════════════════════════════");

            Player p = combinedPlayers.get((i + currentPlayerIndex) % combinedPlayers.size());
            turn(p, parade, deck, "Play");
        }

        ui.broadcastMessage("══════════════════════════════════════════════════════════════\n");
        ui.broadcastMessage("Choose cards from your hand to discard! The remaining cards in your hand will be added to your river, so choose wisely!\n");
        ui.broadcastMessage("══════════════════════════════════════════════════════════════\n");
        for (Player currentPlayer : combinedPlayers) {
            Card firstDiscardedCard = null;
            Card secondDiscardedCard = null;

            if (currentPlayer instanceof HumanPlayer) {
                HumanPlayer currentHumanPlayer = (HumanPlayer) currentPlayer;
                // Using the updated state display instead of separate hand display
                turn(currentHumanPlayer, parade, deck, "Discards");
                turn(currentHumanPlayer, parade, deck, "Discards");
            } else {
                firstDiscardedCard = currentPlayer.chooseCardToDiscard();
                displayCardPlayedOrDiscarded(currentPlayer, firstDiscardedCard, "Discard");
                secondDiscardedCard = currentPlayer.chooseCardToDiscard();
                displayCardPlayedOrDiscarded(currentPlayer, secondDiscardedCard, "Discard");
            }
                
            ArrayList<Card> currentPlayerRiver = currentPlayer.getRiver();
            ArrayList<Card> currentPlayerHand = currentPlayer.getHand();
            for (Card c : currentPlayerHand) {
                currentPlayerRiver.add(c);
            }

            Collections.sort(currentPlayerRiver, new CardComparator());
            ui.broadcastMessage("\n");
            ui.broadcastMessage(getDisplayName(currentPlayer) + "'s River: ");
            ui.broadcastMessage(CardPrinter.printCardRow(currentPlayerRiver, false));
            ui.broadcastMessage("\n");
        }

        ScoreCalculator scorer = new ScoreCalculator(combinedPlayers);
        TreeMap<Integer, ArrayList<Player>> scoreMap = scorer.getScoreMap();

        ui.broadcastMessage("\n");
        ui.broadcastMessage(" _____                        _____                 ");
        ui.broadcastMessage("|  __ \\                      |  _  |                ");
        ui.broadcastMessage("| |  \\/ __ _ _ __ ___   ___  | | | |_   _____ _ __  ");
        ui.broadcastMessage("| | __ / _ | '_  _ \\ / _ \\ | | | \\ \\ / / _ \\ '__| ");
        ui.broadcastMessage("| |_\\ \\ (_| | | | | | |  __/ \\ \\_/ /\\ V /  __/ |    ");
        ui.broadcastMessage(" \\____/\\__,_|_| |_| |_|\\___|  \\___/  \\_/ \\___|_|    ");
        ui.broadcastMessage("                                                     ");
        ui.broadcastMessage("\n");

        if (timedMode) {
            ui.broadcastMessage("\n--- TIME BONUS POINTS ---");
            for (Player player : combinedPlayers) {
                int bonus = timeBonus.get(player);
                if (bonus > 0) {
                    int logDeduction = (int) (Math.log(bonus) / Math.log(2));
                    ui.broadcastMessage(getDisplayName(player) + " earned " + bonus + " bonus points, resulting in a " +
                            logDeduction + " point deduction!");

                    for (Integer score : scoreMap.keySet()) {
                        ArrayList<Player> playersWithScore = scoreMap.get(score);
                        if (playersWithScore.contains(player)) {
                            playersWithScore.remove(player);
                            if (playersWithScore.isEmpty()) {
                                scoreMap.remove(score);
                            }
                            int newScore = score - logDeduction;
                            if (!scoreMap.containsKey(newScore)) {
                                scoreMap.put(newScore, new ArrayList<>());
                            }
                            scoreMap.get(newScore).add(player);
                            break;
                        }
                    }
                }
            }
            ui.broadcastMessage("\n");
        }

        return scoreMap;
    }

    /**
     * New method to clear the console and display the current game state.
     * Shows:
     *   - A header with "Current Turn" where the active player's name is highlighted in green.
     *   - Each player's river.
     *   - (For human players) Their hand is shown so they can select a card.
     *   - The parade is displayed at the bottom.
     */
    private void displayGameState(Player currentPlayer) {
        // Clear the console
        clearConsole();

        // Build and display the "Current Turn" header
        StringBuilder header = new StringBuilder("Current Turn: ");
        for (int i = 0; i < combinedPlayers.size(); i++) {
            Player p = combinedPlayers.get(i);
            String displayName = getDisplayName(p);
            if (p.equals(currentPlayer)) {
                // ANSI escape code for green text
                header.append("\u001B[32m").append(displayName).append("\u001B[0m");
            } else {
                header.append(displayName);
            }
            if (i < combinedPlayers.size() - 1) {
                header.append(" ▶ ");
            }
        }
        ui.broadcastMessage(header.toString());
        ui.broadcastMessage("------------------------------------------------------------");

        // Display each player's river
        for (Player p : combinedPlayers) {
            String riverHeader = getDisplayName(p) + "'s River: ";
            ui.broadcastMessage(riverHeader);
            ArrayList<Card> river = p.getRiver();
            if (river == null || river.isEmpty()) {
                ui.broadcastMessage("   (Empty)");
            } else {
                ArrayList<Card> sortedRiver = new ArrayList<>(river);
                Collections.sort(sortedRiver, new CardComparator());
                ui.broadcastMessage(CardPrinter.printCardRow(sortedRiver, true));
            }
        }
        ui.broadcastMessage("------------------------------------------------------------");

        // Display the parade
        ui.broadcastMessage("The Parade:");
        ui.broadcastMessage(CardPrinter.printCardRow(parade, true));
        
        // If the current player is human, show their hand privately.
        if (currentPlayer instanceof HumanPlayer) {
            HumanPlayer hp = (HumanPlayer) currentPlayer;
            Session s = hp.getSession();
            ui.displayMessage("Your Hand:", s);
            ui.displayMessage(CardPrinter.printCardRow(hp.getHand(), false), s);
        }

    }

    /**
     * Modified turn method.
     * At the beginning and after processing the move, the full game state is shown.
     */
    public boolean turn(Player currentPlayer, ArrayList<Card> parade, Deck deck, String action) {
        Session s = null;
        boolean gameIsOver = false;
        Card choice = null;

        // Show updated state at the start of the turn.
        displayGameState(currentPlayer);

        if (currentPlayer instanceof HumanPlayer) {
            HumanPlayer hp = (HumanPlayer) currentPlayer;
            s = hp.getSession();
            
            ui.displayMessage("Your turn! Number of cards: " + hp.getHand().size(), s);

            if (ui instanceof MultiplayerUI) {
                try {
                    int i = 0;
                    String playerInput = InputManager.waitForInput();
                    if (playerInput == null) {
                        i = 0;
                    } else {
                        i = Integer.parseInt(playerInput);
                    }
                    choice = currentPlayer.playCard(i);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    choice = currentPlayer.playCard(0);
                }
            } else {
                choice = currentPlayer.chooseCardToPlay();
            }
        } else {
            choice = currentPlayer.chooseCardToPlay();
        }

        displayCardPlayedOrDiscarded(currentPlayer, choice, action);

        if (action.equals("Plays")) {
            int choiceValue = choice.getValue();
            Color choiceColor = choice.getColor();

            // Add the played card to the parade.
            parade.add(0, choice);

            // Process the parade to move matching cards into the current player's river.
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

            if (!takenCards.isEmpty()) {
                ui.broadcastMessage(getDisplayName(currentPlayer) + " takes the following cards from the parade:");
                ui.broadcastMessage(CardPrinter.printCardRow(takenCards, true));    
            } else {
                ui.broadcastMessage(getDisplayName(currentPlayer) + " takes no cards from the parade!");
            }

            if (currRiver.size() != 0) {
                Collections.sort(currRiver, new CardComparator());
            }

            // Check win condition: if the river has all 6 colors.
            if (currRiver.size() != 0) {
                HashSet<Color> checkColor = new HashSet<Color>();
                for (Card c : currRiver) {
                    checkColor.add(c.getColor());
                }
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
        } else {
            return true;
        }

        // After the move, update the display to reflect the new game state.
        displayGameState(currentPlayer);
        return gameIsOver;
    }

    // Calculate time bonus based on turn duration
    private int calculateTimeBonus(long turnDuration) {
        if (timeLimit <= 60 * 1000) {
            if (turnDuration < 3000)
                return 3;
            if (turnDuration < 6000)
                return 2;
            if (turnDuration < 10000)
                return 1;
        } else {
            if (turnDuration < 5000)
                return 3;
            if (turnDuration < 10000)
                return 2;
            if (turnDuration < 15000)
                return 1;
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

    // Display time progress bar
    private String displayTimeProgressBar(long elapsedTime) {
        int barLength = 30;
        double progress = Math.min(1.0, (double) elapsedTime / timeLimit);
        int filledBars = (int) (progress * barLength);

        StringBuilder progressBar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            progressBar.append(i < filledBars ? "█" : " ");
        }
        progressBar.append("]");

        long remainingTime = Math.max(0, timeLimit - elapsedTime);
        String timeString = formatTime(remainingTime);
        int percentage = Math.min(100, (int) (progress * 100));

        return String.format("\nTime remaining: %s%n%s%d%%", timeString, progressBar.toString(), percentage);
    }

    private void displayCardPlayedOrDiscarded(Player currentPlayer, Card choice, String playOrDiscard) {
        if (playOrDiscard.equals("Play")) {
            ui.broadcastMessage(getDisplayName(currentPlayer) + " played:");
            ui.broadcastMessage(CardPrinter.printCardRow(Collections.singletonList(choice), false));
        } else {
            ui.broadcastMessage(getDisplayName(currentPlayer) + " discarded:");
            ui.broadcastMessage(CardPrinter.printCardRow(Collections.singletonList(choice), false));
        }
    }

    public static void clearConsole() {
        // Clear the screen using the proper platform logic
        ClearConsole.clear();
    
        // Force a wide, consistent console width for Visual Studio Code or IDEs
        int consoleWidth = getConsoleWidth();
    
        String title = " NEW TURN ";
        int sideWidth = (consoleWidth - title.length() - 2);
        int paddingLeft = sideWidth / 2;
        int paddingRight = sideWidth - paddingLeft;
    
        // Print the full-width rounded box
        System.out.println("╭" + "─".repeat(consoleWidth - 2) + "╮");
        System.out.println("│" + " ".repeat(paddingLeft) + title + " ".repeat(paddingRight) + "│");
        System.out.println("╰" + "─".repeat(consoleWidth - 2) + "╯");
    }
    
    private static int getConsoleWidth() {
        // Try environment variables commonly set in Unix-based terminals
        try {
            return Integer.parseInt(System.getenv("COLUMNS"));
        } catch (Exception ignored) {}
    
        // If COLUMNS isn't available, try fallback environment
        try {
            return Integer.parseInt(System.getenv("CONSOLE_WIDTH"));
        } catch (Exception ignored) {}
    
        return 80; // Final fallback width
    }
}