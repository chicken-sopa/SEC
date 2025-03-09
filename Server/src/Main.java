import Configuration.ProcessConfig;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        Scanner myObj = new Scanner(System.in);


        // Prompt user for a process ID between 0 and 5
        int processId;
        while (true) {
            System.out.println("Enter a process ID (0-2): ");
            try {
                processId = Integer.parseInt(myObj.nextLine());
                if (processId >= 0 && processId <= 2) break;
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid input. Please enter a number between 0 and 5.");
        }
        ProcessConfig.setProcessId(processId);
        // Assign a unique port using 455X format
        Integer portToSend = 4550 + processId;
        System.out.println("Process " + processId + " will use port: " + portToSend);

        Server server = new Server(portToSend, processId);

        server.init();
    }
}