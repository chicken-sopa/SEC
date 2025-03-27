module Common {
    requires com.sun.jna;
    requires com.google.gson;
    requires web3j.utils;
    exports com.sec.Links;
    exports com.sec.Messages;
    exports com.sec.Links.Security;
    exports com.sec.Links.LinkMessages.Base.Contracts;
    exports com.sec.Keys;
    exports com.sec.Messages.Types.Writeset;
    exports com.sec.Messages.Types.ValTSPair;
    exports com.sec.Helpers;
    exports com.sec.BlockChain;
}