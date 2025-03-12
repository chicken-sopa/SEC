package Lib;

import Lib.Links.AuthenticatedPerfectLink;
import Lib.Links.Security.DigitalSignatureAuth;
import Lib.Messages.BaseMessage;

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
    public void AppendMessage(String messageToAppend) {

    }
}
