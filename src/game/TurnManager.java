package game;

import java.util.*;
import java.util.concurrent.TimeUnit;

import cards.*;
import constants.UIConstants;
import constants.GameplayConstants;
import players.*;
import players.human.HumanPlayer;
import ui.*;
import jakarta.websocket.Session;

public class TurnManager {
    private UserInterface ui;
    private Scanner scanner;

    public TurnManager(UserInterface ui, Scanner scanner) {
        this.ui = ui;
        this.scanner = scanner;
    }

    /**
     * Executes a single turn for the specified player.
     * 
     * @param gameState     The current game state
     * @param currentPlayer The player whose turn it is
     * @param gameMode      The current game mode
     * @param action        The action type ("Play" or "Discards")
     * @return true if the game should end after this turn, false otherwise
     */
    public boolean executeTurn(GameState gameState, Player currentPlayer, GameMode gameMode, String action) {
        return executeTurn(gameState, currentPlayer, gameMode, action, false);
    }

    /**
     * Executes a single turn for the specified player with the option to mark it as a final turn.
     * 
     * @param gameState     The current game state
     * @param currentPlayer The player whose turn it is
     * @param gameMode      The current game mode
     * @param action        The action type ("Play" or "Discards")
     * @param isFinalTurn   Whether this is a final turn (affects display)
     * @return true if the game should end after this turn, false otherwise
     */
    public boolean executeTurn(GameState gameState, Player currentPlayer, GameMode gameMode, String action, boolean isFinalTurn) {
        // Move turn logic from Game.turn() here
        // Return whether the game is over
        Session playerSession = null;
        boolean gameIsOver = false;
        Card choice = null;

        displayGameState(gameState, currentPlayer);

        // Handle player card selection
        if (currentPlayer instanceof HumanPlayer) {
            HumanPlayer currentHumanPlayer = (HumanPlayer) currentPlayer;
            playerSession = currentHumanPlayer.getSession();
            
            ui.displayMessage("Your turn! Number of cards:" + currentHumanPlayer.getHand().size(), playerSession);

            if (ui instanceof MultiplayerUI) {
                try {
                    //implement check if session is open: if it is not, we immediately play a pre determined card(idx 0) after 2s hard coded delay.
                    //or else, we continue as per normal
                    if (!(playerSession.isOpen())) {
                        Thread.sleep(2000);
                        choice = currentPlayer.playCard(GameplayConstants.DEFAULT_CARD_CHOICE_INDEX);
                    } else {
                        int indexOfCardToPlay = 0;
                        String playerInput = InputManager.waitForInputWithTimeout(GameplayConstants.NUM_SECONDS_TILL_TIMEOUT, TimeUnit.SECONDS);
                        if (playerInput == null || playerInput.equals("") || !playerInput.matches("^[0-9]*$")) {
                            // timed out
                            indexOfCardToPlay = GameplayConstants.DEFAULT_CARD_CHOICE_INDEX;
                        } else {
                            indexOfCardToPlay = Integer.parseInt(playerInput);
                        }

                        choice = currentPlayer.playCard(indexOfCardToPlay);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    choice = currentPlayer.playCard(GameplayConstants.DEFAULT_CARD_CHOICE_INDEX);
                }
            } else {
                // use the overloaded method to handle singleplayer input
                if (action.equals(GameplayConstants.PLAY)) {
                    choice = currentPlayer.chooseCardToPlay();
                } else {
                    choice = currentPlayer.chooseCardToDiscard();
                }
            }
        } else {
            choice = currentPlayer.chooseCardToPlay();
        }

        // Display the card played or discarded
        displayCardPlayedOrDiscarded(currentPlayer, choice, action);

        // Process the play action
        if (action.equals(GameplayConstants.PLAY)) {
            int choiceValue = choice.getValue();
            CardColor choiceColor = choice.getColor();

            // Add the current card to the parade
            ArrayList<Card> parade = gameState.getParade();
            parade.add(0, choice);

            // Process the parade for cards to be removed and added to the player's river
            ArrayList<Card> currRiver = currentPlayer.getRiver();
            Iterator<Card> iterator = parade.iterator();
            List<Card> takenCards = new ArrayList<Card>();

            // Skip cards based on the played card's value
            for (int i = 0; i < choiceValue; i++) {
                if (iterator.hasNext()) {
                    iterator.next();
                }
            }

            // Check remaining cards for matches
            while (iterator.hasNext()) {
                Card checkCard = iterator.next();
                int checkValue = checkCard.getValue();
                CardColor checkColor = checkCard.getColor();

                // Take matching cards
                if (checkColor.equals(choiceColor) || checkValue <= choiceValue) {
                    currRiver.add(checkCard);
                    takenCards.add(checkCard);
                    iterator.remove();
                }
            }

            // Display which cards were taken
            if (!takenCards.isEmpty()) {
                String message = PlayerDisplayUtils.getDisplayName(currentPlayer) + " takes the following cards from the parade:";
                String cardVisual = CardPrinter.printCardRow(takenCards, true);
                
                ui.broadcastMessage(message);
                ui.broadcastMessage(cardVisual);
            } else {
                String message = PlayerDisplayUtils.getDisplayName(currentPlayer) + " takes no cards from the parade!";
                ui.broadcastMessage(message);
            }

            // Sort the river
            if (!currRiver.isEmpty()) {
                Collections.sort(currRiver, new CardComparator());
            }

            // Check for game over conditions
            // 1. River has all 6 colors
            if (!currRiver.isEmpty()) {
                HashSet<CardColor> checkColor = new HashSet<CardColor>();
                for (Card c : currRiver) {
                    checkColor.add(c.getColor());
                }

                if (checkColor.size() == GameplayConstants.NUM_DIFF_COLORS_OF_CARDS) {
                    gameIsOver = true;
                }
            }

            // Draw a card if possible. If the deck is empty, then end the game.
            Card toDraw = gameState.getDeck().drawCard();
            if (toDraw == null) {
                gameIsOver = true;
            } else {
                currentPlayer.drawCard(toDraw);
            }

            // Display updated game state
            displayGameState(gameState, currentPlayer);
        }
        InputManager.clearInput();

        // Handle turn advancement
        handleTurnAdvancement(currentPlayer, gameState.getPlayers(), isFinalTurn);
        return gameIsOver;
    }

    /**
     * Handles the advancement to the next player's turn, with appropriate UI feedback.
     * @param isFinalTurn whether this is part of the final turns phase
     */
    private void handleTurnAdvancement(Player currentPlayer, List<Player> players, boolean isFinalTurn) {

        if (currentPlayer instanceof HumanPlayer) {
            HumanPlayer humanPlayer = (HumanPlayer) currentPlayer;
            Session playerSession = humanPlayer.getSession();
            
            // Then check if we're in multiplayer AND the session exists and is open
            if (ui instanceof MultiplayerUI && playerSession != null && playerSession.isOpen()) {
                // Multiplayer human player logic
                ui.displayMessage("Hit \"ENTER\" to end turn!", playerSession);
                
                try {
                    InputManager.waitForEnterPress();
                    int currentPlayerIndex = players.indexOf(currentPlayer);
                    int nextPlayerIndex = (currentPlayerIndex + 1) % players.size();
                    Player nextPlayer = players.get(nextPlayerIndex);
                    broadcastNewTurn(nextPlayer, isFinalTurn);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else if (ui instanceof MultiplayerUI) {
                try {
                    ui.broadcastMessage("Any player can hit ENTER to continue...");
                    InputManager.waitForEnterPress();
                    int currentPlayerIndex = players.indexOf(currentPlayer);
                    int nextPlayerIndex = (currentPlayerIndex + 1) % players.size();
                    Player nextPlayer = players.get(nextPlayerIndex);
                    broadcastNewTurn(nextPlayer, isFinalTurn);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else {
                humanPlayer.waitForEnterToEndTurn();
                ConsoleUtils.clear();
                
                int currentPlayerIndex = players.indexOf(currentPlayer);
                int nextPlayerIndex = (currentPlayerIndex + 1) % players.size();
                Player nextPlayer = players.get(nextPlayerIndex);
                broadcastNewTurn(nextPlayer, isFinalTurn);
            }
        } else {
            if (ui instanceof MultiplayerUI) {
                try {
                    ui.broadcastMessage("Any player can hit ENTER to continue...");
                    InputManager.waitForEnterPress();
                    int currentPlayerIndex = players.indexOf(currentPlayer);
                    int nextPlayerIndex = (currentPlayerIndex + 1) % players.size();
                    Player nextPlayer = players.get(nextPlayerIndex);
                    broadcastNewTurn(nextPlayer, isFinalTurn);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } else if (ui instanceof SinglePlayerUI) {
                HumanPlayer.waitForAnyPlayerToAdvance(scanner);
                ConsoleUtils.clear();
                
                int currentPlayerIndex = players.indexOf(currentPlayer);
                int nextPlayerIndex = (currentPlayerIndex + 1) % players.size();
                Player nextPlayer = players.get(nextPlayerIndex);
                broadcastNewTurn(nextPlayer, isFinalTurn);
            }
        }

        InputManager.clearInput();
    }

    /**
     * Displays the current game state to all players.
     */
    public void displayGameState(GameState gameState, Player currentPlayer) {
        List<Player> players = gameState.getPlayers();
        ArrayList<Card> parade = gameState.getParade();

        // Build lines for boxed section
        List<String> boxedLines = new ArrayList<>();

        // Current Turn Line
        StringBuilder turnLine = new StringBuilder("Current Turn: ");
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            String name = PlayerDisplayUtils.getDisplayName(p);
            if (p.equals(currentPlayer)) {
                turnLine.append(UIConstants.GREEN).append(name).append(UIConstants.RESET_COLOR);
            } else {
                turnLine.append(UIConstants.GRAY).append(name).append(UIConstants.RESET_COLOR);
            }
            if (i < players.size() - 1) {
                turnLine.append(" ➤  ");
            }
        }
        boxedLines.add(turnLine.toString());

        // Each player's river (store string representations of cards, not color-coded visuals)
        for (Player p : players) {
            boxedLines.add("");
            boxedLines.add(PlayerDisplayUtils.getDisplayName(p) + "'s River:");
            ArrayList<Card> river = p.getRiver();
            if (river == null || river.isEmpty()) {
                boxedLines.add("  (Empty)");
            } else {
                ArrayList<Card> sortedRiver = new ArrayList<>(river);
                Collections.sort(sortedRiver, new CardComparator());
                String[] riverLines = CardPrinter.printCardRow(sortedRiver, true).split("\n");
                boxedLines.addAll(Arrays.asList(riverLines));
            }
        }

        // Add Parade inside the box
        boxedLines.add("");
        boxedLines.add("The Parade:");
        String[] paradeLines = CardPrinter.printCardRow(parade, true).split("\n");
        boxedLines.addAll(Arrays.asList(paradeLines));

        // Encapsulate inside a box
        int maxLength = boxedLines.stream().mapToInt(line -> line.replaceAll("\\e\\[[;\\d]*m", "").length()).max().orElse(0);
        String top = "╭" + "─".repeat(maxLength + 2) + "╮";
        String bottom = "╰" + "─".repeat(maxLength + 2) + "╯";

        ui.broadcastMessage(top);
        for (String line : boxedLines) {
            ui.broadcastMessage("│ " + padRight(line, maxLength) + " │");
        }
        ui.broadcastMessage(bottom);

        // Show hand for current human player only
        if (currentPlayer instanceof HumanPlayer) {
            HumanPlayer hp = (HumanPlayer) currentPlayer;
            Session s = hp.getSession();
            ui.displayMessage("Your Hand:", s);
            ui.displayMessage(CardPrinter.printCardRow(hp.getHand(), false), s);
        }
    }

    private String padRight(String text, int width) {
        String plainText = text.replaceAll("\\e\\[[;\\d]*m", "");
        return text + " ".repeat(Math.max(0, width - plainText.length()));
    }

    /**
     * Displays information about a card being played or discarded.
    */
    public void displayCardPlayedOrDiscarded(Player currentPlayer, Card choice, String action) {
        // Move displayCardPlayedOrDiscarded logic here
        if (action.equals(GameplayConstants.PLAY)) {
            ui.broadcastMessage(PlayerDisplayUtils.getDisplayName(currentPlayer) + " played:");
            ui.broadcastMessage(CardPrinter.printCardRow(Collections.singletonList(choice), false));
        } else {
            ui.broadcastMessage(PlayerDisplayUtils.getDisplayName(currentPlayer) + " discarded:");
            ui.broadcastMessage(CardPrinter.printCardRow(Collections.singletonList(choice), false));
        }
    }


    /**
     * Broadcasts a message indicating the next player's turn.
     * @param nextPlayer the player whose turn is starting
     * @param isFinalTurn whether this is part of the final turns phase
     */
    private void broadcastNewTurn(Player nextPlayer, boolean isFinalTurn) {
        String playerName = PlayerDisplayUtils.getDisplayName(nextPlayer);
        
        // If it's a final turn, display the final turn banner first
        if (isFinalTurn) {
            ui.broadcastMessage(UIConstants.FINAL_TURN_BANNER);
    
            // Small delay to ensure banner is seen
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}