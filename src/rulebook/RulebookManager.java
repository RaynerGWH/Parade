package rulebook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import ui.ConsoleUtils;
/**
 * Manages rulebook functionality including displaying and navigating the rulebook.
 * This class encapsulates all rulebook-related operations to promote modularity.
 */
public class RulebookManager {
    
    private final String rulebookPath;
    private final int linesPerPage;
    private List<String> rulebookLines;
    private int totalPages;
    private final Scanner scanner;
    
    /**
     * Creates a RulebookManager with the specified rulebook path and scanner.
     * 
     * @param rulebookPath Path to the rulebook file
     * @param scanner Scanner for user input
     */
    public RulebookManager(String rulebookPath, Scanner scanner) {
        this(rulebookPath, scanner, 15); // 15 Lines per page.
    }
    
    /**
     * Creates a RulebookManager with the specified parameters.
     * 
     * @param rulebookPath Path to the rulebook file
     * @param scanner Scanner for user input
     * @param linesPerPage Number of lines to display per page
     */
    public RulebookManager(String rulebookPath, Scanner scanner, int linesPerPage) {
        this.rulebookPath = rulebookPath;
        this.scanner = scanner;
        this.linesPerPage = linesPerPage;
    }
    
    /**
     * Loads the rulebook content from the file.
     * 
     * @return true if the rulebook was successfully loaded, false otherwise
     */
    private boolean loadRulebook() {
        try {
            rulebookLines = Files.readAllLines(Paths.get(rulebookPath));
            totalPages = (int) Math.ceil((double) rulebookLines.size() / linesPerPage);
            return true;
        } catch (IOException e) {
            System.out.println("Error reading rulebook file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Displays and navigates through the rulebook content.
     */
    public void displayRulebook() {
        if (rulebookLines == null && !loadRulebook()) {
            return;
        }
        
        int currentPage = 0;
        
        while (true) {
            displayPage(currentPage);
            
            int updatedPage = updateRulebookPage(currentPage);
            if (updatedPage == -1) {
                ConsoleUtils.clear();
                System.out.println("Exited rulebook.\n");
                break;
            }
            currentPage = updatedPage;
        }
    }
    
    /**
     * Displays a single page of the rulebook.
     * 
     * @param pageNumber The page number to display
     */
    private void displayPage(int pageNumber) {
        ConsoleUtils.clear();
        
        int start = pageNumber * linesPerPage;
        int end = Math.min(start + linesPerPage, rulebookLines.size());
        
        System.out.println("Page " + (pageNumber + 1) + " of " + totalPages + ":");
        for (int i = start; i < end; i++) {
            System.out.println(rulebookLines.get(i));
        }
    }
    
    /**
     * Helper method for rulebook navigation.
     * Prompts the user based on the current page and returns the updated page number,
     * or -1 if the user wishes to quit.
     *
     * @param currentPage the current page (0-indexed).
     * @return the updated page number, or -1 to signal quitting.
     */
    private int updateRulebookPage(int currentPage) {
        String prompt;
        if (currentPage == 0) {
            prompt = "\nEnter (N)ext or (Q)uit:\n> ";
        } else if (currentPage == totalPages - 1) {
            prompt = "\nEnter (P)revious, (F)irst, or (Q)uit:\n> ";
        } else {
            prompt = "\nEnter (N)ext, (P)revious, (F)irst, or (Q)uit:\n> ";
        }

        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            System.out.println("No more input available.");
            return -1;
        }
        String input = scanner.nextLine().trim().toUpperCase();

        switch (input) {
            case "N":
                if (currentPage < totalPages - 1) {
                    return currentPage + 1;
                } else {
                    System.out.println("This is the last page.");
                    return currentPage;
                }
            case "P":
                if (currentPage > 0) {
                    return currentPage - 1;
                } else {
                    System.out.println("This is the first page.");
                    return currentPage;
                }
            case "F":
                return 0;
            case "Q":
                return -1;
            default:
                System.out.println("Invalid input. Please try again.");
                return currentPage;
        }
    }
}