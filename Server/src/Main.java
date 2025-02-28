import java.io.IOException;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");
        FairLossLink fll = new FairLossLink();

        fll.sendMessage(args[0]);
        fll.receiveMessage();

    }
}