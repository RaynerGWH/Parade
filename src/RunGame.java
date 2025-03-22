import account.*;
import players.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class RunGame {
    Scanner sc = new Scanner(System.in);
    AccountFileManager acctMgr = new AccountFileManager(sc);

    ArrayList<Account> accounts = new ArrayList<Account>();

    //IMPORTANT
    //in the main game, accounts will be accepted via websocket and added to this arraylist.
    //for testing purposes, my "account" will be hardcoded in.

    PlayerManager playerMgr = new PlayerManager(accounts);

    //TODO: Somehow pass accounts into here.
    public static void main(String[] args) {

        RunGame rg = new RunGame();

        //DELETE ME
        rg.accounts.add(rg.acctMgr.initialize());


        System.out.println(" ____   _    ____      _    ____  _____ \r\n" + //
                        "|  _ \\ / \\  |  _ \\    / \\  |  _ \\| ____|\r\n" + //
                        "| |_) / _ \\ | |_) |  / _ \\ | | | |  _|  \r\n" + //
                        "|  __/ ___ \\|  _ <  / ___ \\| |_| | |___ \r\n" + //
                        "|_| /_/   \\_\\_| \\_\\/_/   \\_\\____/|_____|");
        System.out.println("Welcome to the Parade Card Game!");
        // single player or multiplayer (fancy console art)

        System.out.println("Would you like to play Single Player or Multi Player");
        //TODO: add single/multiplayer functionality

        System.out.print("Enter 'R' to refer to the rulebook, or 'S' to start the game: ");
        String command = rg.sc.nextLine().trim().toUpperCase();
        if (command.equals("R")) {
            scrollRulebook("rulebook/rulebook.txt");

        } else if (command.equals("S")) {
            int numPlayers = rg.accounts.size();
            while (true) {
                try {
                    int numBots = 0;

                    if (numPlayers < 8) {
                        System.out.print("Enter number of Bots: ");
                        numBots = Integer.parseInt(rg.sc.nextLine());
                        if (numPlayers + numBots > 8) {
                            throw new NumberFormatException();
                        }
                    }
                    
                    rg.playerMgr.initializeComputerPlayers(numBots);
                    rg.playerMgr.initializeHumanPlayers();
                    rg.playerMgr.setTurnOrder(numBots);

                    Game g = new Game(rg.playerMgr.getPlayers());

                    TreeMap<Integer, ArrayList<Player>> scores = g.startGame();

                    printRankings(scores);

                    //TODO: handle reward logic
                    return;

                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number!");
                } finally {
                    rg.sc.close();
                }
            }

        } else {
            System.out.println("Command not recognized.");
        }
        rg.sc.close();
    }

    public static void printRankings(TreeMap<Integer, ArrayList<Player>> scores) {
        System.out.println("\n=== FINAL RANKINGS ===");
        int rank = 1;
        for (Map.Entry<Integer, ArrayList<Player>> entry : scores.entrySet()) {
            ArrayList<Player> players = entry.getValue();
            for (Player player : players) {
                System.out.println(getOrdinal(rank) + ": " + player.getName() + " | Score: " + entry.getKey());
            }
            rank += players.size(); // Increase rank appropriately
        }
    }

    private static String getOrdinal(int rank) {
        if (rank % 100 >= 11 && rank % 100 <= 13) {
            return rank + "th";
        }
        return switch (rank % 10) {
            case 1 -> rank + "ST";
            case 2 -> rank + "ND";
            case 3 -> rank + "RD";
            default -> rank + "TH";
        };
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
