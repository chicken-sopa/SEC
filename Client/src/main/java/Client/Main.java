package Client;

import Configuration.ClientConfig;

public class Main {
    public static void main(String[] args) throws Exception {

        // DESCOMENTAR PARA CORRER COM ARGS
        //int clientId = Integer.parseInt(args[0]);
        int clientId = 1;

        // Assign a unique port using 555X format
        int myPort = 5550 + clientId;
        ClientConfig.setProcessId(clientId);

        int[] destinationPortList = new int[1];
        for (int i = 0; i <= 0; i++) {
            destinationPortList[i] = 4550 + i;
        }

        Client client = new Client(myPort, clientId);
        client.sendSomething(destinationPortList);
    }
}