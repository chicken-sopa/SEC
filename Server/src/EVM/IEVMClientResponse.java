package EVM;

public interface IEVMClientResponse {

    public void sendEVMAnswerToClient(String answerFromEVM, int transactionOwnerId);
}
