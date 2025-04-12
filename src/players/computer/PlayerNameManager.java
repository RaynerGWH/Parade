package players.computer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// This class's function is to ensure that no two players obtain the same name.
// It also ensures that there is a maximum of 8 players.

public class PlayerNameManager {
    private List<PlayerName> availableNames;

    public PlayerNameManager() {
        availableNames = new ArrayList<>();
        // Add all enumerator values (PlayerNames from the PlayerName enum) to the pool of available names.
        for (PlayerName name : PlayerName.values()) {
            availableNames.add(name);
        }

        // Shuffle the collection of names to ensure randomness 
        // e.g. so player 1 is not always going to be Alice.
        Collections.shuffle(availableNames);
    }

    public PlayerName assignName() {
        // Remove and return the first available name.
        return availableNames.remove(0);
    }
}
