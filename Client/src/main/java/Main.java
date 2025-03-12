import Configuration.ClientConfig;

public class Main {
    public static void main(String[] args) throws Exception {

        // DESCOMENTAR PARA CORRER COM ARGS
        //int clientId = Integer.parseInt(args[0]);
        int clientId = 0;

        // Assign a unique port using 555X format
        int myPort = 5550 + clientId;
        ClientConfig.setProcessId(clientId);

        int[] destinationPortList = new int[4];
        for (int i = 0; i < 4; i++) {
            destinationPortList[i] = Integer.parseInt("455"+ i);
        }

        Client client = new Client(myPort, clientId);
        client.sendSomething(destinationPortList);
    }
}