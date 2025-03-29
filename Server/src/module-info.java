module Server {
    requires Common;
    requires org.hyperledger.besu.evm;
    requires transitive org.hyperledger.besu.datatypes;
    requires tuweni.bytes;
    requires com.google.gson;
    exports Server;
}
