package Server;

import Configuration.ProcessConfig;


public class Main {
    public static void main(String[] args) throws Exception {


        boolean isLeader = false;
        // Prompt user for a process ID between 0 and 5
        int processId = Integer.parseInt(args[0]);
        if(args.length > 1 && args[1].equals("yes")){
            isLeader = true;
        }
        ProcessConfig.setProcessId(processId);
        // Assign a unique port using 455X format
        Integer portToSend = 4550 + processId;
        System.out.println("Process " + processId + " will use port: " + portToSend);

        Server server = new Server(portToSend, processId, isLeader);

        server.init();
    }
}