package Lib;

import Configuration.ClientConfig;
import Lib.Links.AuthenticatedPerfectLink;
import Lib.Links.Security.DigitalSignatureAuth;
import Lib.Messages.AppendMessage;
import Lib.Messages.BaseMessage;
import Lib.Messages.MessageType;

import java.net.SocketException;
import java.security.NoSuchAlgorithmException;

public class Lib implements ILib {

    AuthenticatedPerfectLink<BaseMessage> authenticatedPerfectLink;
    DigitalSignatureAuth<BaseMessage>  digitalSignatureAuth;

    public Lib(int myPort) throws NoSuchAlgorithmException, SocketException {
        digitalSignatureAuth = new DigitalSignatureAuth<>();
        authenticatedPerfectLink = new AuthenticatedPerfectLink<>(myPort, digitalSignatureAuth);
    }


    @Override
    public void SendAppendMessage(String messageToAppend, int destinationPort) throws Exception {
        AppendMessage message = new AppendMessage(MessageType.APPEND, ClientConfig.getProcessId(), messageToAppend);
        authenticatedPerfectLink.sendMessage(message, destinationPort);
    }
}
