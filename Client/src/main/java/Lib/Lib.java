package Lib;

import com.sec.Links.AuthenticatedPerfectLink;
import com.sec.Links.Security.DigitalSignatureAuth;
import com.sec.Messages.AppendMessage;
import com.sec.Messages.BaseMessage;
import com.sec.Messages.ConsensusFinishedMessage;
//import com.sec.Messages.AbortMessage;
import com.sec.Messages.MessageType;

import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import static Configuration.ClientConfig.getProcessId;

public class Lib implements ILib {
    private final AuthenticatedPerfectLink<BaseMessage> authenticatedPerfectLink;
    private final DigitalSignatureAuth<BaseMessage> digitalSignatureAuth;
    private final CopyOnWriteArrayList<Consumer<BaseMessage>> listeners = new CopyOnWriteArrayList<>();

    public Lib(int myPort) throws NoSuchAlgorithmException, SocketException {
        digitalSignatureAuth = new DigitalSignatureAuth<>();
        authenticatedPerfectLink = new AuthenticatedPerfectLink<>(myPort, digitalSignatureAuth, getProcessId());
        startReceiveMessageThread();
    }

    @Override
    public void SendAppendMessage(String messageToAppend, int destinationPort) throws Exception {
        AppendMessage message = new AppendMessage(MessageType.APPEND, getProcessId(), messageToAppend, getProcessId());
        authenticatedPerfectLink.sendMessage(message, destinationPort);
    }

    private void startReceiveMessageThread() {
        new Thread(() -> {
            while (true) {
                try {
                    BaseMessage msg = authenticatedPerfectLink.receiveMessage();
                    if (msg != null && msg instanceof ConsensusFinishedMessage) {
                        System.out.println("-------------------- VIM DAR NOTIFY AO SLIETERNS ---------------------");
                        System.out.println("MESSAGE RECEBIDA Da lib e" + msg.getMessageType());
                        notifyListeners(msg);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void addMessageListener(Consumer<BaseMessage> listener) {
        listeners.add(listener);
    }

    private void notifyListeners(BaseMessage message) {
        for (Consumer<BaseMessage> listener : listeners) {
            listener.accept(message);
        }
    }
}
