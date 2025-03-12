package Keys;

import Configuration.ClientConfig;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import static Keys.KeyLoader.loadPrivateKey;
import static Keys.KeyLoader.loadPublicKey;

public class KeyManager {
    private static final Map<Integer, PublicKey> NODES_PUBLIC_KEYS = new HashMap<>();
    private static final Map<Integer, PublicKey> CLIENT_PUBLIC_KEYS = new HashMap<>();
    private static PrivateKey privateKey;

    static {
        try {
            // Preload public keys (in reality, read from a config file or constants)
            NODES_PUBLIC_KEYS.put(0, loadPublicKey("crypto/node0_public.pem"));
            NODES_PUBLIC_KEYS.put(1, loadPublicKey("crypto/node1_public.pem"));
            NODES_PUBLIC_KEYS.put(2, loadPublicKey("crypto/node2_public.pem"));
            CLIENT_PUBLIC_KEYS.put(0, loadPublicKey("crypto/client0_public.pem"));
            CLIENT_PUBLIC_KEYS.put(1, loadPublicKey("crypto/client1_public.pem"));
            // Load private key for this node
            privateKey = loadPrivateKey("crypto/node" + ClientConfig.getProcessId() + "_private.pem");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load keys", e);
        }
    }

    public static PublicKey getNodePublicKey(int processId) {
        return NODES_PUBLIC_KEYS.get(processId);
    }

    public static PublicKey getClientPublicKey(int clientId) {
        return CLIENT_PUBLIC_KEYS.get(clientId);
    }
    public static PrivateKey getPrivateKey() {
        return privateKey;
    }
}
