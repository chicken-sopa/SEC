package Client;

import Configuration.ClientConfig;

public class Main {
    public static void main(String[] args) throws Exception {

        // DESCOMENTAR PARA CORRER COM ARGS
        int clientId = Integer.parseInt(args[0]);
        String myAddress;
        // Assign a unique port using 555X format
        int myPort = 5550 + clientId;
        ClientConfig.setProcessId(clientId);
        System.out.println("Running client on port: " + myPort);
        int[] destinationPortList = new int[4];
        for (int i = 0; i <= 3; i++) {
            destinationPortList[i] = 4550 + i;
        }

        if(clientId == 4){
            myAddress = "0x78731D3Ca6b7E34aC0F824c42a7cC18A495cabaB";
        }else {
            myAddress = "0xCA35b7d915458EF540aDe6068dFe2F44E8fa733c";
        }

        Client client = new Client(myPort, clientId, destinationPortList, myAddress);
        client.SendRequestToConsensusThread();
        client.startReceiveMessageThread();
    }
}