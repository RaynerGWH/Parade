package game;

import cards.*;
import players.*;

import java.util.*;

public class GameState {
    private Deck deck;
    private boolean gameIsOver;
    private ArrayList<Card> parade;
    private List<Player> players;

    public GameState(List<Player> players) {
        this.deck = new Deck();
        this.gameIsOver = false;
        this.parade = new ArrayList<>();
        this.players = new ArrayList<>(players);
    }

    public Deck getDeck() {
        return deck;
    }

    public boolean isGameOver() {
        return gameIsOver;
    }

    public void setGameOver(boolean gameIsOver) {
        this.gameIsOver = gameIsOver;
    }

    public ArrayList<Card> getParade() {
        return parade;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void initializeParade(int initialLength) {
        for (int i = 0; i < initialLength; i++) {
            parade.add(deck.drawCard());
        }
    }
}