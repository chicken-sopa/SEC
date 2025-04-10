package com.sec.Keys;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import static com.sec.Keys.KeyLoader.loadPrivateKey;
import static com.sec.Keys.KeyLoader.loadPublicKey;

public class KeyManager {
    private static final Map<Integer, PublicKey> PUBLIC_KEYS = new HashMap<>();
    private static final Map<Integer, PublicKey> EOA_KEYS = new HashMap<>(); //<EOA_ADDRESS,KEY>


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
            EOA_KEYS.put(0,loadPublicKey("crypto/EOA_Owner_public.pem"));
            EOA_KEYS.put(1,loadPublicKey("crypto/EOA_User_public.pem"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load public keys", e);
        }
    }

    public static PublicKey getPublicKey(int processId) {
        return PUBLIC_KEYS.get(processId);
    }

    public static PublicKey getEOAPublicKey(String address) {
        if(address.startsWith("787")) {
            return EOA_KEYS.get(0);
        }else if (address.startsWith("CA")){
            return EOA_KEYS.get(1);
        }
        else{
            System.out.println("THE SOURCE ADDRESS WAS NONE OF THE KNOWN ONES, GETTING PUBLIC KEY OF REGULAR NODE");
            return PUBLIC_KEYS.get(1);
        }
    }


    // Dynamically load the private key based on the process ID
    public static PrivateKey getPrivateKey(int processId) {
        try {
            return loadPrivateKey("crypto/" + processId + "_private.pem");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load private key for process " + processId, e);
        }
    }

    public static PrivateKey getEOAPrivateKey(String address) {

        try {
            if(address.startsWith("787")) {
                return loadPrivateKey("crypto/EOA_Owner_private.pem");

            }else if (address.startsWith("CA")) {
                return loadPrivateKey("crypto/EOA_User_private.pem");
            }
            else { //for byzantine behaviour
                return loadPrivateKey("crypto/1_private.pem");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load private key for EOA with address " + address , e);
        }
    }


}
