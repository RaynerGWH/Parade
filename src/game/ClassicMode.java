package game;

import java.util.*;
import players.*;

public class ClassicMode implements GameMode {
    @Override
    public void initialize(Scanner scanner) {
        // No special initialization needed
    }
    
    @Override
    public boolean isTimeUp() {
        return false; // Never times out
    }
    
    @Override
    public boolean applyModeSpecificRules(GameState state) {
        return false; // No special rules to end game
    }
    
    @Override
    public void updateAfterTurn(Player player, long turnDuration) {
        // No action needed, only for timed mode
    }
}