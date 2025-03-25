import java.util.Scanner;
import game.*;

public class UserInputHandler implements Runnable {
    private GameClientEndpoint clientEndpoint;

    public UserInputHandler(GameClientEndpoint clientEndpoint) {
        this.clientEndpoint = clientEndpoint;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String userInput = scanner.nextLine();
            if ("exit".equalsIgnoreCase(userInput)) {
                break;
            } else {
                clientEndpoint.handleIdx(userInput);
            }
        }
        scanner.close();
    }
}


