package game;

import account.Account;
import cards.*;

public interface Listener {
    void onCardPlayed(Account player, Card card);
}
