import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import account.Account;
import account.AccountFileManager;
import game.GameClientEndpoint;
import game.GameManager;
import game.GameServerEndpoint;
import ui.*;

import jakarta.websocket.*;

public class RunGame {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        GameManager gameMgr = new GameManager(sc);
        UserInterface ui;
        GameServerEndpoint gse = null;
        GameClientEndpoint gce;
        Map<Session,Account> sessions = new HashMap<Session, Account>();
        CountDownLatch latch = new CountDownLatch(1);

        System.out.println(" ____   _    ____      _    ____  _____ \r\n" + //
                "|  _ \\ / \\  |  _ \\    / \\  |  _ \\| ____|\r\n" + //
                "| |_) / _ \\ | |_) |  / _ \\ | | | |  _|  \r\n" + //
                "|  __/ ___ \\|  _ <  / ___ \\| |_| | |___ \r\n" + //
                "|_| /_/   \\_\\_| \\_\\/_/   \\_\\____/|_____|");
        System.out.println("Welcome to the Parade Card Game!");

        System.out.print("Enter 'R' to refer to the rulebook, or 'S' to start the game: ");
        String command = sc.nextLine().trim().toUpperCase();
        if (command.equals("R")) {
            scrollRulebook("rulebook/rulebook.txt");

        } else if (command.equals("S")) {
            int numBots = 0;
            System.out.println("Would you like to play Single Player(S) or Multi Player(M)");
            command = sc.nextLine().trim().toUpperCase();
            AccountFileManager acctMgr = new AccountFileManager(sc);
            Account a = acctMgr.initialize();

            if (command.equals("S")) {
                //Add my own account into the game
                ui = new SinglePlayerUI();
                sessions.put(null,a);
                gameMgr.start(numBots, ui, gse);

            } else if (command.equals("M")) {
                System.out.println("Please enter \"H\" to host, or \"J\" to join");
                command = sc.nextLine();
                if (command.equals("H")) {

                    //how do i get my current session?
                    gse = new GameServerEndpoint();
                    ui = new MultiplayerUI(gse);
                    gameMgr.start(0, ui, gse);
                    
                } else if (command.equals("J")) {
                    try {
                        URI uri = new URI("ws://localhost:8080/game");
                        gce = new GameClientEndpoint(uri, sc);
                        // Create a latch that will only count down when the game session is done.
                        latch = new CountDownLatch(1);
                        gce.setLatch(latch);
                
                        // Now block the main thread until the latch is counted down.
                        latch.await();
                    } catch (URISyntaxException e) {
                        System.out.println("Invalid URI Entered.");
                    } catch (InterruptedException e) {
                        System.out.println("Connection interrupted");
                    }
                }
            }
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

