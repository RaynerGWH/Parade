package account;

import exceptions.CorruptFileException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

/**
 * Manages account file reading and writing (encryption/decryption, parsing, etc.).
 * 
 * Responsibilities:
 * 
 *   1. Locating and reading the account data file
 *   2. Decrypting and parsing account data
 *   3. Saving accounts to file
 */
public class AccountFileManager {

    // The name of the file to store account data
    private static final String FILE_PATH = "Save.PG1";

    //A form of error handling: prevents users from entering funny stuff in their name
    private static final String NAME_REGEX = "^[A-Za-z0-9]+$";

    // The expected header of the stored file
    private static final String HEADER = "ID/NAME/WIN/LOSS/BALANCE-[FLAIR]\n";

    // A list of accounts in memory (optional usage)
    private final List<Account> accounts;
    
    // Scanner for interactive account initialization
    private Scanner sc;

    /**
     * Constructs the AccountFileManager. No direct console I/O is performed here.
     * This class only handles file operations.
     */
    public AccountFileManager() {
        this.accounts = new ArrayList<>();
    }

    /**
     * Constructs the AccountFileManager with a Scanner for input.
     *
     * @param sc the Scanner used for reading user input.
     */
    public AccountFileManager(Scanner sc) {
        this();
        this.sc = sc;
    }

    /**
     * Attempts to load an account from the file.
     *
     * @return an {@link Optional} containing the loaded {@link Account}, or empty if not found
     * @throws IOException          if an I/O error occurs when reading
     * @throws CorruptFileException if the file format or header is invalid
     */
    public Optional<Account> loadAccount() throws IOException, CorruptFileException {
        File pg1File = findPg1File();
        if (pg1File == null) {
            return Optional.empty();
        }

        // Decrypt and parse
        Account account = processExistingFile(pg1File);
        return Optional.of(account);
    }

    /**
     * Locates the file containing account data, if it exists, in the current directory.
     *
     * @return the {@link File} representing the account data, or {@code null} if not found
     */
    private File findPg1File() {
        File dir = new File(".");
        File[] files = dir.listFiles((d, name) -> name.equals(FILE_PATH));
        return (files != null && files.length > 0) ? files[0] : null;
    }

    /**
     * Reads and decrypts the content from the specified file, then parses it into an {@link Account}.
     *
     * @param file the {@link File} containing the encrypted account data
     * @return the {@link Account} parsed from the file
     * @throws IOException          if an I/O error occurs
     * @throws CorruptFileException if the file format or header is invalid
     */
    private Account processExistingFile(File file) throws IOException, CorruptFileException {
        byte[] encryptedData = Files.readAllBytes(file.toPath());
        byte[] decryptedData = CryptoUtils.xorCipher(encryptedData);
        String content = new String(decryptedData);

        validateHeader(content);
        return parseContent(content);
    }

    /**
     * Validates the header of the decrypted file content.
     *
     * @param content the decrypted file content
     * @throws CorruptFileException if the content does not start with the required header
     */
    private void validateHeader(String content) throws CorruptFileException {
        if (!content.startsWith(HEADER)) {
            throw new CorruptFileException("Header is missing or incorrect.");
        }
    }

    /**
     * Parses the content of the account data into an {@link Account} object.
     *
     * @param content the decrypted file content
     * @return an {@link Account} created from the parsed data
     * @throws CorruptFileException if the content cannot be parsed correctly
     */
    private Account parseContent(String content) throws CorruptFileException {
        String[] data = content.split("\n");
        if (data.length < 2) {
            throw new CorruptFileException("Content does not contain enough lines.");
        }
    
        String infoLine = data[1];
        String[] details = infoLine.split("/");
    
        if (details.length != 6) {
            throw new CorruptFileException("Content format is invalid.");
        }
    
        try {
            UUID uuid = UUID.fromString(details[0]);
            String name = details[1];
            int wins = Integer.parseInt(details[2]);
            int losses = Integer.parseInt(details[3]);
            double balance = Double.parseDouble(details[4]);
    
            // The line in your file that has the account data
            String flairData = details[5];

            // Remove only brackets and quotes, but NOT spaces
            flairData = flairData.replaceAll("[\\[\\]\"]", "");

            // Prepare a list for unlocked flairs
            List<String> unlockedFlairs = new ArrayList<>();

            // If there's anything left, split on commas
            if (!flairData.trim().isEmpty()) {
                // Split on commas
                String[] tokens = flairData.split(",");
                for (String token : tokens) {
                    // Trim leading/trailing spaces
                    String flairName = token.trim();
                    if (!flairName.isEmpty()) {
                        unlockedFlairs.add(flairName);
                    }
                }
            }

            return new Account(uuid, name, wins, losses, balance, unlockedFlairs);
        } catch (IllegalArgumentException e) {
            throw new CorruptFileException("Failed to parse account data.");
        }
    }
    
    /**
     * Saves the given account to the file, encrypting the content before writing.
     *
     * @param account the {@link Account} to save
     * @throws IOException if writing to the file fails
     */
    public void save(Account account) throws IOException {
        Path path = Paths.get(FILE_PATH);
        String content = HEADER + account.toString();
        byte[] encryptedInfo = CryptoUtils.xorCipher(content.getBytes());

        Files.write(path, encryptedInfo);
        // Optionally, maintain a reference in memory
        if (!accounts.contains(account)) {
            accounts.add(account);
        }
    }

    /**
     * Returns an immutable snapshot of the accounts managed by this file manager.
     *
     * @return a copy of the list of accounts
     */
    public List<Account> getAccounts() {
        // Return a copy to preserve encapsulation
        return new ArrayList<>(accounts);
    }

    /**
     * Adds an account to the internal list if not already present.
     *
     * @param account the account to add
     */
    public void addAccount(Account account) {
        if (!accounts.contains(account)) {
            accounts.add(account);
        }
    }

    /**
     * Initializes the account.
     * <p>
     * If a saved account file exists and can be loaded successfully,
     * the method loads and returns that account. Otherwise, it prompts
     * the user to create a new account.
     *
     * @return the loaded or newly created {@link Account}
     */
    public Account initialize() {
        while (true) {
            try {
                Optional<Account> accountOpt = loadAccount();
                if (accountOpt.isPresent()) {
                    System.out.println("Account loaded successfully: " + accountOpt.get().getUsername());
                    return accountOpt.get();
                } else {
                    System.out.print("No saved account found. Enter account name to create a new account: ");
                    String name = sc.nextLine();
                    //Name verification
                    if (name.matches(NAME_REGEX)) {
                        Account newAccount = new Account(UUID.randomUUID(), name, 0, 0, 0.0, new ArrayList<>());
                        save(newAccount);
                        addAccount(newAccount);
                        return newAccount;
                    } else {
                        throw new IOException();
                    }
                }
            } catch (CorruptFileException e) {
                // System.out.println("Error loading account (" + ex.getMessage() + "). Creating a new account.");
                // System.out.print("Enter account name: ");
                // String name = sc.nextLine();
                // Account newAccount = new Account(UUID.randomUUID(), name, 0, 0, 0.0, new ArrayList<>());
                // addAccount(newAccount);
                // return newAccount;
                System.out.println("Error loading account: File may be corrupted. Please delete Save.PG1 file and try again.");
            } catch (IOException e) {
                System.out.println("Invalid input. Account can only ");
            }
        }
    }
}