package com.sec.Keys;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import static com.sec.Keys.KeyLoader.loadPrivateKey;
import static com.sec.Keys.KeyLoader.loadPublicKey;

public class KeyManager {
    private static final Map<Integer, PublicKey> PUBLIC_KEYS = new HashMap<>();


    static {
        try {
            // Preload public keys (assumed to be shared across processes)
            PUBLIC_KEYS.put(0, loadPublicKey("crypto/0_public.pem"));
            PUBLIC_KEYS.put(1, loadPublicKey("crypto/1_public.pem"));
            PUBLIC_KEYS.put(2, loadPublicKey("crypto/2_public.pem"));
            PUBLIC_KEYS.put(3, loadPublicKey("crypto/3_public.pem"));
            PUBLIC_KEYS.put(4, loadPublicKey("crypto/4_public.pem"));
            PUBLIC_KEYS.put(5, loadPublicKey("crypto/5_public.pem"));
            PUBLIC_KEYS.put(6, loadPublicKey("crypto/6_public.pem"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load public keys", e);
        }
    }

    public static PublicKey getPublicKey(int processId) {
        return PUBLIC_KEYS.get(processId);
    }


    // Dynamically load the private key based on the process ID
    public static PrivateKey getPrivateKey(int processId) {
        try {
            return loadPrivateKey("crypto/" + processId + "_private.pem");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load private key for process " + processId, e);
        }
    }


}
