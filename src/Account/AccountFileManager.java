package account;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import exceptions.CorruptFileException;

import java.nio.file.Path;
import java.nio.file.Paths;

public class AccountFileManager {
    private static final String FILE_PATH = "Save.PG1";
    private static final String HEADER = "ID/NAME/WIN/LOSS/BALANCE-[FLAIR]\n";
    private static final String USERNAME_REGEX = "^[A-Za-z0-9]+$";

    private Scanner sc;

    public AccountFileManager(Scanner sc) {
        this.sc = sc;
    }

    public Account initialize() {
        try {
            File pg1File = findPg1File();
            Account a;
            if (pg1File == null) {
                a = createNewAccount();

            } else {
                a = processExistingFile(pg1File);
            }

            save(a);
            return a;

        } catch (IOException e) {
            System.out.println("An error has occured");
            e.printStackTrace();
            return null;
        } catch (CorruptFileException e) {
            System.out.println("An error has occured");
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.out.println("An error has occured");
            e.printStackTrace();
            return null;
        }
    }

    private File findPg1File() {
        File dir = new File(".");
        File[] files = dir.listFiles((d, name) -> name.equals(FILE_PATH));
        return (files != null && files.length > 0) ? files[0] : null;
    }

    private Account processExistingFile(File file) throws IOException, FileNotFoundException, CorruptFileException {
        byte[] encryptedData = Files.readAllBytes(file.toPath());
        byte[] decryptedData = CryptoUtils.xorCipher(encryptedData);
        String content = new String(decryptedData);

System.out.println(content);

        validateHeader(content);

        Account a = parseContent(content);
        return a;
    }

    public Account parseContent(String content) throws CorruptFileException {
        //throw the first half of the content
        String[] data = content.split("\n");
        String info = data[1];

        String[] details = info.split("/");

        //check the length of content: if it is 6, valid, else, throw new exception
        if (details.length != 6) {
            throw new CorruptFileException();
        }

        try {
            String flairArr = details[details.length - 1];
            flairArr = flairArr.replaceAll("[\\[,\\]]", "");
            ArrayList<String> unlockedFlairs = new ArrayList<String>(Arrays.asList(flairArr.split(",")));

            UUID uuid = UUID.fromString(details[0]);
            String name = details[1];
            int wins = Integer.parseInt(details[2]);
            int losses = Integer.parseInt(details[3]);
            double balance = Double.parseDouble(details[4]);

            Account a = new Account(uuid, name, wins, losses, balance, unlockedFlairs);
            return a;
            
        } catch (NumberFormatException e) {
            throw new CorruptFileException();

        } catch (IllegalArgumentException e) {
            throw new CorruptFileException();

        }
    }

    private void validateHeader(String content) throws CorruptFileException{
        if (!content.startsWith(HEADER)) {
            throw new CorruptFileException();
        }
    }

    private Account createNewAccount() throws IOException {
        System.out.print("No existing account found. Would you like to create a new account?(Y/N) > ");
        String response = sc.nextLine().trim().toUpperCase();

        while (!response.equals("Y") && !response.equals("N")) {
            System.out.println("Invalid input. Would you like to create a new account?(Y/N)");
            response = sc.nextLine();
        }

        if (response.equals("Y")) {
            while (true) {
                System.out.print("Enter username(alphanumeric only, i.e. A-Z, a-z, 0-9) >");
                String username = sc.nextLine();

                //validate username using regex
                if (validateUsername(username)) {
                    return new Account(username);
                }
                System.out.println("Invalid username (alphanumeric only).");
            }

        } else {
            throw new IOException("An account is required to play the game. Please create an account.");
        }
    }

    private boolean validateUsername(String username){
        if (username.matches(USERNAME_REGEX)) {
            return true;
        }
        return false;
    }

    public void save(Account account) throws IOException {
        try {
            Path path = Paths.get(FILE_PATH);
            String toWrite = HEADER + account.toString();
            byte[] encryptedInfo = CryptoUtils.xorCipher(toWrite.getBytes());

            Files.write(path, encryptedInfo);

        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
