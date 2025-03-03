import java.util.ArrayList;
import java.util.Scanner;

public class HumanPlayer extends AbstractPlayer {
    private Scanner scanner;
    private String name;

    public HumanPlayer(ArrayList<Card> hand, String name, Scanner scanner) {
        super(hand);
        this.name = name;
        // Use the passed-in scanner rather than creating a new one
        this.scanner = scanner;
    }

    @Override
    public Card chooseCardToPlay() {
        if (hand.isEmpty()) {
            System.out.println("You have no cards to play.");
            return null;
        }
        
        System.out.println("Your hand:");
        for (int i = 0; i < hand.size(); i++) {
            System.out.println(i + ": " + hand.get(i));
        }
        
        int index = -1;
        while (true) {
            System.out.print(name + ", enter the index of the card you want to play: ");
            String input = scanner.nextLine();
            try {
                index = Integer.parseInt(input);
                if (index >= 0 && index < hand.size()) {
                    break;
                } else {
                    System.out.println("Invalid index. Please enter a number between 0 and " + (hand.size() - 1) + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        
        Card card = playCard(index);
        System.out.println("You played: " + card);
        return card;
    }
    
    public String getName() {
        return name;
    }
}
