module Server {
    requires Common;
    requires org.hyperledger.besu.evm;
    requires transitive org.hyperledger.besu.datatypes;
    exports Server;
}
