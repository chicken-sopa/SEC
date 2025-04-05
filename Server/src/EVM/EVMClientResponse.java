package EVM;

import com.sec.Links.AuthenticatedPerfectLink;
import com.sec.Messages.BaseMessage;
import com.sec.Messages.EvmResultMessage;

public class EVMClientResponse implements IEVMClientResponse {

    final int SENDER_ID;
    final AuthenticatedPerfectLink<BaseMessage> link;

    public EVMClientResponse(int senderId, AuthenticatedPerfectLink<BaseMessage> link) {
        SENDER_ID = senderId;
        this.link = link;
    }


    @Override
    public void sendEVMAnswerToClient(String answerFromEVM) {
        try {
            EvmResultMessage message = new EvmResultMessage(this.SENDER_ID, answerFromEVM);
            link.sendMessage(message, 5554);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
