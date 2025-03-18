import java.io.*;

public class testmain {

    public static AccountFileManager afm = new AccountFileManager();
    public static void main(String[] args) {
        try {
            Account a = afm.initialize();
            System.out.println(a);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CorruptFileException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
