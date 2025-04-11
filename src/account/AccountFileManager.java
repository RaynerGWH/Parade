package account;

import exceptions.CorruptFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import constants.UIConstants;

/**
 * Manages account file reading and writing (encryption/decryption, parsing, etc.).
 */
public class AccountFileManager {

    private static final String NAME_REGEX = "^[A-Za-z0-9]+$";
    private static final String HEADER = "ID/NAME/WIN/LOSS/BALANCE-[FLAIR]\n";

    private final List<Account> accounts;
    private Scanner sc;

    public AccountFileManager() {
        this.accounts = new ArrayList<>();
    }

    public AccountFileManager(Scanner sc) {
        this();
        this.sc = sc;
    }

    /**
     * Loads all accounts from the current directory.
     *
     * @return list of loaded accounts
     * @throws IOException
     * @throws CorruptFileException
     */
    public List<Account> loadAllAccounts() throws IOException, CorruptFileException {
        File dir = new File(".");
        File[] files = dir.listFiles((d, name) -> name.endsWith(".PG1"));
        List<Account> loadedAccounts = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                try {
                    Account account = processExistingFile(file);
                    loadedAccounts.add(account);
                } catch (CorruptFileException e) {
                    System.out.println("Skipping corrupted file: " + file.getName());
                }
            }
        }

        accounts.clear();
        accounts.addAll(loadedAccounts);
        return loadedAccounts;
    }

    private Account processExistingFile(File file) throws IOException, CorruptFileException {
        byte[] encryptedData = Files.readAllBytes(file.toPath());
        byte[] decryptedData = CryptoUtils.xorCipher(encryptedData);
        String content = new String(decryptedData);

        validateHeader(content);
        Account a = parseContent(content);
        if (file.getName().startsWith(a.getUsername())) {
            return a;
        }

        throw new CorruptFileException("An error has occured. Please delete the .PG1 file for this account");
    }

    private void validateHeader(String content) throws CorruptFileException {
        if (!content.startsWith(HEADER)) {
            throw new CorruptFileException("Header is missing or incorrect.");
        }
    }

    private Account parseContent(String content) throws CorruptFileException {
        String[] data = content.split("\n");
        if (data.length < 2) throw new CorruptFileException("Not enough data.");

        String[] details = data[1].split("/");
        if (details.length != 6) throw new CorruptFileException("Invalid format.");

        try {
            UUID uuid = UUID.fromString(details[0]);
            String name = details[1];
            int wins = Integer.parseInt(details[2]);
            int losses = Integer.parseInt(details[3]);
            double balance = Double.parseDouble(details[4]);

            String flairData = details[5].replaceAll("[\\[\\]\"]", "").trim();
            List<String> unlockedFlairs = new ArrayList<>();
            if (!flairData.isEmpty()) {
                for (String flair : flairData.split(",")) {
                    unlockedFlairs.add(flair.trim());
                }
            }

            return new Account(uuid, name, wins, losses, balance, unlockedFlairs);
        } catch (IllegalArgumentException e) {
            throw new CorruptFileException("Failed to parse account data.");
        }
    }

    public void save(Account account) throws IOException{
        String filename = account.getUsername() + ".PG1";
        Path path = Paths.get(filename);
        String content = HEADER + account.toString();
        byte[] encryptedInfo = CryptoUtils.xorCipher(content.getBytes());

        Files.write(path, encryptedInfo);

        if (!accounts.contains(account)) {
            accounts.add(account);
        }
    }

    public List<Account> getAccounts() {
        return new ArrayList<>(accounts);
    }

    public void addAccount(Account account) {
        if (!accounts.contains(account)) {
            accounts.add(account);
        }
    }

    public Account initialize() {
        try {
            loadAllAccounts();
        } catch (IOException | CorruptFileException e) {
            System.out.println("Error loading accounts: " + e.getMessage());
        }

        if (!accounts.isEmpty()) {
            System.out.println("📜 Available accounts:\nChoose your champion to rejoin the Parade!");

            for (int i = 0; i < accounts.size(); i++) {
                System.out.println("[" + (i + 1) + "] " + accounts.get(i).getUsername());
            }
            System.out.print("🎯 Select your hero by number, or type [0] to summon a new one:\n" + UIConstants.LIGHT_PURPLE + "The stage is set — who shall step forward? 🎴" + UIConstants.RESET_COLOR);
            int choice = Integer.parseInt(sc.nextLine());

            if (choice > 0 && choice <= accounts.size()) {
                return accounts.get(choice - 1);
            }
        }

        while (true) {
            System.out.print("Enter a new account name: ");
            String name = sc.nextLine();

            if (name.matches(NAME_REGEX)) {
                Account newAccount = new Account(UUID.randomUUID(), name, 0, 0, 0.0, new ArrayList<>());
                try {
                    save(newAccount);
                } catch (IOException e) {
                    System.out.println("Error saving new account.");
                }
                return newAccount;
            } else {
                System.out.println("Invalid name. Alphanumeric characters only.");
            }
        }
    }
}
