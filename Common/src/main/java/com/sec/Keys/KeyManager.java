package com.sec.Keys;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import static com.sec.Keys.KeyLoader.loadPrivateKey;
import static com.sec.Keys.KeyLoader.loadPublicKey;

public class KeyManager {
    private static final Map<Integer, PublicKey> NODES_PUBLIC_KEYS = new HashMap<>();
    private static final Map<Integer, PublicKey> CLIENT_PUBLIC_KEYS = new HashMap<>();

    static {
        try {
            // Preload public keys (assumed to be shared across processes)
            NODES_PUBLIC_KEYS.put(0, loadPublicKey("crypto/node0_public.pem"));
            NODES_PUBLIC_KEYS.put(1, loadPublicKey("crypto/node1_public.pem"));
            NODES_PUBLIC_KEYS.put(2, loadPublicKey("crypto/node2_public.pem"));
            CLIENT_PUBLIC_KEYS.put(0, loadPublicKey("crypto/client0_public.pem"));
            CLIENT_PUBLIC_KEYS.put(1, loadPublicKey("crypto/client1_public.pem"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load public keys", e);
        }
    }

    public static PublicKey getNodePublicKey(int processId) {
        return NODES_PUBLIC_KEYS.get(processId);
    }

    public static PublicKey getClientPublicKey(int clientId) {
        return CLIENT_PUBLIC_KEYS.get(clientId);
    }

    // Dynamically load the private key based on the process ID
    public static PrivateKey getPrivateKey(int processId) {
        try {
            return loadPrivateKey("crypto/node" + processId + "_private.pem");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load private key for process " + processId, e);
        }
    }
}
