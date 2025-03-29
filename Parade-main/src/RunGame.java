import Account.*;
import cards.Deck;
import exceptions.DeckEmptyException;
import exceptions.DuplicateNameException;
import exceptions.NoAvailableNPCNamesException;
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
    public static void main(String[] args) 
    throws InterruptedException {

        RunGame rg = new RunGame();

        //DELETE ME
        rg.accounts.add(rg.acctMgr.initialize());


        String[] paradeLetterP = {
            "██████╗ ",
            "██╔══██╗",
            "██████╔╝",
            "██╔═══╝ ",
            "██║     ",
            "╚═╝     "};

    String[] paradeLetterA1 = {
            " █████╗ ",
            "██╔══██╗",
            "███████║",
            "██╔══██║",
            "██║  ██║",
            "╚═╝  ╚═╝"};

    String[] paradeLetterR = {
            "██████╗ ",
            "██╔══██╗",
            "██████╔╝",
            "██╔══██╗",
            "██║  ██║",
            "╚═╝  ╚═╝"};

    String[] paradeLetterA2 = paradeLetterA1; // Reusing A

    String[] paradeLetterD = {
            "██████╗ ",
            "██╔══██╗",
            "██║  ██║",
            "██║  ██║",
            "██████╔╝",
            "╚═════╝ "};

    String[] paradeLetterE = {
            "███████╗",
            "██╔════╝",
            "█████╗  ",
            "██╔══╝  ",
            "███████╗",
            "╚══════╝"};

    String[][] letters = {paradeLetterP, paradeLetterA1, paradeLetterR, paradeLetterA2, paradeLetterD, paradeLetterE};

    // We init delay
    int timer = 70;

    // Print letters row by row with animation
    for (int row = 0; row < 6; row++) {
        for (String[] letter : letters) {
        
            // We print out the letters
            System.out.print(letter[row] + "");

            // Create delayed response
            Thread.sleep(timer);
        }
        System.out.println();
        // Increase delay exponentially
        timer /= 1.2;
    }
        // single player or multiplayer (fancy console art)

        System.out.println("Would you like to play Single Player or Multi Player");
        //TODO: add single/multiplayer functionality

        while (true) {
            System.out.print("Enter 'R' to refer to the rulebook, or 'S' to start the game: ");
            String command = rg.sc.nextLine().trim().toUpperCase();
        
            if (command.equals("R")) {
                scrollRulebook("src/rulebook/rulebook.txt");
                // After returning from rulebook, this loop will continue
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
                        return; // Game ended
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid number!");
                    } catch (DeckEmptyException e) {
                        System.out.println(e.getMessage());
                    } catch (DuplicateNameException e) {
                        System.out.println(e.getMessage());
                    } catch (NoAvailableNPCNamesException e) {
                        System.out.println(e.getMessage());
                    } 
                    finally {
                        rg.sc.close();
                    }
                }
            } else {
                System.out.println("Command not recognized.");
            }
        }
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
                if (currentPage == 0) {
                    System.out.print("\nEnter (N)ext or (Q)uit: ");
                } else {
                    System.out.print("\nEnter (N)ext, (P)revious, or (Q)uit: ");
                }
                
                input = scanner.nextLine().trim().toUpperCase();

                switch (input) {
                    case "N":
                    if (currentPage < totalPages - 1) {
                        currentPage++;
                    } else {
                        System.out.println("This is the last page.");
                        while (true) {
                            System.out.print("Enter (P)revious, (F)irst, or (Q)uit: ");
                            String subInput = scanner.nextLine().trim().toUpperCase();
                            switch (subInput) {
                                case "P":
                                    if (currentPage > 0) currentPage--;
                                    // Break out of the loop to reprint the new page
                                    break;
                                case "F":
                                    currentPage = 0;
                                    break;
                                case "Q":
                                    System.out.println("Exiting rulebook.");
                                    scanner.close();
                                    return;
                                default:
                                    System.out.println("Invalid input.");
                                    continue;
                            }
                            // Only reach here if input is P or F (not invalid or Q)
                            break;
                        }
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
