package Configuration;

public class ProcessConfig {
    private static int processId;

    public static void setProcessId(int id) {
        processId = id;
    }

    public static int getProcessId() {
        return processId;
    }
}
