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
        int[] destinationPortList = new int[4];
        for (int i = 0; i <= 3; i++) {
            destinationPortList[i] = 4550 + i;
        }

        Client client = new Client(myPort, clientId, destinationPortList, "0x78731D3Ca6b7E34aC0F824c42a7cC18A495cabaB");
        client.SendRequestToConsensusThread();
        client.startReceiveMessageThread();
    }
}