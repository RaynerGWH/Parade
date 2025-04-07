package game;

import players.*;
import java.util.*;

public interface GameMode {
    void initialize(Scanner scanner);

    boolean isTimeUp(); // Returns false for classic mode

    boolean applyModeSpecificRules(GameState state);

    void updateAfterTurn(Player player, long turnDuration);
}
