import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public class Account {
    private UUID id;
    private String username;
    private int wins;
    private int losses;
    private double balance;
    private List<String> unlockedFlairs;

    public Account(UUID id, String username, int wins, int losses, double balance, List<String> unlockedFlairs) {
        this.id = id;
        this.username = username;
        this.wins = wins;
        this.losses = losses;
        this.balance = balance;
        this.unlockedFlairs = unlockedFlairs;
    }

    //overloaded constructor(for new account)
    public Account(String username) {
        this(UUID.randomUUID(), username, 0, 0, 0, new ArrayList<String>());
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public double getBalance() {
        return balance;
    }

    public List<String> getUnlockedFlairs() {
        return unlockedFlairs;
    }

    public void addBalance(double amt) {
        balance += amt;
    }

    public void deductBalance(double amt) {
        balance -= amt;
        if (balance < 0) {
            balance = 0;
        }
    }

    public String toString() {
        return String.format("%s/%s/%d/%d/%.0f/%s", id, username, wins, losses, balance, unlockedFlairs);
    }
}