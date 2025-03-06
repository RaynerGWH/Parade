import java.util.*;
import java.nio.file.*;
import java.io.*;

public class RunGame {
    public static void main(String[] args) {
        System.out.println("Welcome to the Parade Card Game!");
        Scanner scanner = new Scanner(System.in);
        // single plyaer or multiplayer (fancy console art)

        System.out.println("Would you like to play Single Player or Multi Player");
        //
        System.out.print("Enter 'R' to refer to the rulebook: ");
        System.out.println("Enter 'S' to start the game!");
        String command = scanner.nextLine().trim().toUpperCase();
        if (command.equals("R")) {
            scrollRulebook("rulebook.txt");

        } else if (command.equals("S")) {
            Game g = new Game(2, 2);

        } else {
            System.out.println("Command not recognized.");
        }
    }

    public static void scrollRulebook(String filePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            Scanner scanner = new Scanner(System.in);
            int linesPerPage = 15;
            int totalPages = (int) Math.ceil((double) lines.size() / linesPerPage);
            int currentPage = 0;
            String input;

            while (true) {
                // Display the current page
                int start = currentPage * linesPerPage;
                int end = Math.min(start + linesPerPage, lines.size());
                System.out.println("\nPage " + (currentPage + 1) + " of " + totalPages + ":");
                for (int i = start; i < end; i++) {
                    System.out.println(lines.get(i));
                }

                // Prompt user for input
                System.out.print("\nEnter (N)ext, (P)revious, or (Q)uit: ");
                input = scanner.nextLine().trim().toUpperCase();

                switch (input) {
                    case "N":
                        if (currentPage < totalPages - 1) {
                            currentPage++;
                        } else {
                            System.out.println("This is the last page.");
                        }
                        break;
                    case "P":
                        if (currentPage > 0) {
                            currentPage--;
                        } else {
                            System.out.println("This is the first page.");
                        }
                        break;
                    case "Q":
                        System.out.println("Exiting rulebook.");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid input. Please try again.");
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading rulebook file: " + e.getMessage());
        }
    }
}
