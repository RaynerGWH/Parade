package game;

import java.util.*;

import cards.*;
import constants.Constants;
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
        Session s = null;
        boolean gameIsOver = false;
        Card choice = null;

        displayGameState(gameState, currentPlayer);

        // Handle player card selection
        if (currentPlayer instanceof HumanPlayer) {
            HumanPlayer hp = (HumanPlayer) currentPlayer;
            s = hp.getSession();
            
            ui.displayMessage("Your turn! Number of cards:" + hp.getHand().size(), s);

            if (ui instanceof MultiplayerUI) {
                try {
                    //implement check if session is open: if it is not, we immediately play a pre determined card(idx 0) after 2s hard coded delay.
                    //or else, we continue as per normal
                    if (!(s.isOpen())) {
                        Thread.sleep(2000);
                        choice = currentPlayer.playCard(0);
                    } else {
                        int i = 0;
                        String playerInput = InputManager.waitForInput();
                        if (playerInput == null) {
                            // timed out
                            i = 0;
                        } else {
                            i = Integer.parseInt(playerInput);
                        }

                        choice = currentPlayer.playCard(i);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    // Default: choice = 0;
                    choice = currentPlayer.playCard(0);
                }
            } else {
                // use the overloaded method to handle singleplayer input
                choice = currentPlayer.chooseCardToPlay();
            }
        } else {
            choice = currentPlayer.chooseCardToPlay();
        }

        // Display the card played or discarded
        displayCardPlayedOrDiscarded(currentPlayer, choice, action);

        // Process the play action
        if (action.equals(Constants.PLAY)) {
            int choiceValue = choice.getValue();
            Color choiceColor = choice.getColor();

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
                Color checkColor = checkCard.getColor();

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
                HashSet<Color> checkColor = new HashSet<Color>();
                for (Card c : currRiver) {
                    checkColor.add(c.getColor());
                }
                if (checkColor.size() == 6) {
                    gameIsOver = true;
                }
            }

            // Draw a card if possible
            Card toDraw = gameState.getDeck().drawCard();
            if (toDraw == null) {
                gameIsOver = true;
            } else {
                currentPlayer.drawCard(toDraw);
            }

            // Display updated game state
            displayGameState(gameState, currentPlayer);
        }

        // Handle turn advancement
        handleTurnAdvancement(currentPlayer, gameState.getPlayers(), isFinalTurn);

        return gameIsOver;
    }

    /**
     * Handles the advancement to the next player's turn, with appropriate UI feedback.
     */
    private void handleTurnAdvancement(Player currentPlayer, List<Player> players) {
        handleTurnAdvancement(currentPlayer, players, false);
    }

    /**
     * Handles the advancement to the next player's turn, with appropriate UI feedback.
     * @param isFinalTurn whether this is part of the final turns phase
     */
    private void handleTurnAdvancement(Player currentPlayer, List<Player> players, boolean isFinalTurn) {
        if (currentPlayer instanceof HumanPlayer) {
            HumanPlayer humanPlayer = (HumanPlayer) currentPlayer;
            Session playerSession = humanPlayer.getSession();
            
            if (ui instanceof MultiplayerUI) {
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
    }

    /**
     * Displays the current game state to all players.
     */
    public void displayGameState(GameState gameState, Player currentPlayer) {
        // Move displayGameState logic here
        List<Player> players = gameState.getPlayers();
        ArrayList<Card> parade = gameState.getParade();
        
        // Build the "Current Turn" header
        StringBuilder header = new StringBuilder("Current Turn: ");
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            String displayName = PlayerDisplayUtils.getDisplayName(p);
            if (p.equals(currentPlayer)) {
                // ANSI escape code for green text
                header.append("\u001B[32m").append(displayName).append("\u001B[0m");
            } else {
                header.append(displayName);
            }
            if (i < players.size() - 1) {
                header.append(" ▶ ");
            }
        }
        ui.broadcastMessage(header.toString());
        ui.broadcastMessage("------------------------------------------------------------");

        // Display each player's river
        for (Player p : players) {
            String riverHeader = PlayerDisplayUtils.getDisplayName(p) + "'s River: ";
            ui.broadcastMessage(riverHeader);
            ArrayList<Card> river = p.getRiver();
            if (river == null || river.isEmpty()) {
                ui.broadcastMessage("   (Empty)");
            } else {
                ArrayList<Card> sortedRiver = new ArrayList<>(river);
                Collections.sort(sortedRiver, new CardComparator());
                ui.broadcastMessage(CardPrinter.printCardRow(sortedRiver, false));
            }
        }
        ui.broadcastMessage("------------------------------------------------------------");

        // Display the parade
        ui.broadcastMessage("The Parade:");
        ui.broadcastMessage(CardPrinter.printCardRow(parade, true));

        // If the current player is human, show their hand privately
        if (currentPlayer instanceof HumanPlayer) {
            HumanPlayer hp = (HumanPlayer) currentPlayer;
            Session s = hp.getSession();
            ui.displayMessage("Your Hand:", s);
            ui.displayMessage(CardPrinter.printCardRow(hp.getHand(), false), s);
        }
    }

    /**
     * Displays information about a card being played or discarded.
    */
    public void displayCardPlayedOrDiscarded(Player currentPlayer, Card choice, String action) {
        // Move displayCardPlayedOrDiscarded logic here
        if (action.equals(Constants.PLAY)) {
            ui.broadcastMessage(PlayerDisplayUtils.getDisplayName(currentPlayer) + " played:");
            ui.broadcastMessage(CardPrinter.printCardRow(Collections.singletonList(choice), false));
        } else {
            ui.broadcastMessage(PlayerDisplayUtils.getDisplayName(currentPlayer) + " discarded:");
            ui.broadcastMessage(CardPrinter.printCardRow(Collections.singletonList(choice), false));
        }
    }

    /**
     * Broadcasts a message indicating the next player's turn.
     */
    private void broadcastNewTurn(Player nextPlayer) {
        broadcastNewTurn(nextPlayer, false);
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
            ui.broadcastMessage("═════════════════════════════════════════════════════════════");
            ui.broadcastMessage("                         FINAL TURN                         ");
            ui.broadcastMessage("                   NO CARDS WILL BE DRAWN                   ");
            ui.broadcastMessage("═════════════════════════════════════════════════════════════");
            
            // Small delay to ensure banner is seen
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        ui.broadcastMessage("===============================================================");
        ui.broadcastMessage("                      " + playerName + "'s TURN                ");
        ui.broadcastMessage("===============================================================");
    }
}