package Lib;

import Configuration.ClientConfig;
import com.sec.Links.AuthenticatedPerfectLink;
import com.sec.Links.Security.DigitalSignatureAuth;
import com.sec.Messages.AppendMessage;
import com.sec.Messages.BaseMessage;
import com.sec.Messages.ConsensusFinishedMessage;
import com.sec.Messages.MessageType;

import java.net.SocketException;
import java.security.NoSuchAlgorithmException;

import static Configuration.ClientConfig.getProcessId;

public class Lib implements ILib {

    AuthenticatedPerfectLink<BaseMessage> authenticatedPerfectLink;
    DigitalSignatureAuth<BaseMessage>  digitalSignatureAuth;

    public Lib(int myPort) throws NoSuchAlgorithmException, SocketException {
        digitalSignatureAuth = new DigitalSignatureAuth<>();
        authenticatedPerfectLink = new AuthenticatedPerfectLink<>(myPort, digitalSignatureAuth, getProcessId());
        startReceiveMessageThread();
    }

    @Override
    public void SendAppendMessage(String messageToAppend, int destinationPort) throws Exception {
        AppendMessage message = new AppendMessage(MessageType.APPEND, getProcessId(), messageToAppend,getProcessId());
        System.out.println("CHECK VALUE IN APPENDED MSG = " + message.getMessage());
        authenticatedPerfectLink.sendMessage(message, destinationPort);
    }

    private void startReceiveMessageThread() {
        new Thread(() -> {
            while (true) {
                try {
                    BaseMessage msg = authenticatedPerfectLink.receiveMessage();
                    if (msg != null && msg.getMessageType() == MessageType.FINISHED){
                        ConsensusFinishedMessage finishedMessage = (ConsensusFinishedMessage)msg;
                        System.out.println("Message has been appended. Value appended was: " +  finishedMessage.getVal());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

}
