package Client;

import Configuration.ClientConfig;

public class Main {
    public static void main(String[] args) throws Exception {

        // DESCOMENTAR PARA CORRER COM ARGS
        int clientId = Integer.parseInt(args[0]);

        // Assign a unique port using 555X format
        int myPort = 5550 + clientId;
        ClientConfig.setProcessId(clientId);
        System.out.println("Running client on port: " + myPort);
        int[] destinationPortList = new int[3];
        for (int i = 0; i <= 2; i++) {
            destinationPortList[i] = 4550 + i;
        }

        Client client = new Client(myPort, clientId, destinationPortList);
        client.Listen();
    }
}