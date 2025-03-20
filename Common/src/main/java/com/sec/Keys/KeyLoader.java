package com.sec.Keys;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Utility class to load RSA public and private keys from PEM files.
 */
public class KeyLoader {

    /**
     * Loads a public key from a PEM file.
     * @param filePath Path to the public key PEM file.
     * @return PublicKey instance.
     * @throws Exception If an error occurs while reading or parsing the key.
     */
    public static PublicKey loadPublicKey(String filePath) throws Exception {

        String keyContent = readKeyFile(filePath);
        keyContent = keyContent.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", ""); // Remove new lines and spaces

        byte[] keyBytes = Base64.getDecoder().decode(keyContent);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    public static PublicKey loadPublicKeyById(int id) throws Exception {

        String filePath = "crypto/" + id + "_public.pem";
        String keyContent = readKeyFile(filePath);
        keyContent = keyContent.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", ""); // Remove new lines and spaces

        byte[] keyBytes = Base64.getDecoder().decode(keyContent);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }


    /**
     * Loads a private key from a PEM file.
     * @param filePath Path to the private key PEM file.
     * @return PrivateKey instance.
     * @throws Exception If an error occurs while reading or parsing the key.
     */
    public static PrivateKey loadPrivateKey(String filePath) throws Exception {
        String keyContent = readKeyFile(filePath);
        keyContent = keyContent.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", ""); // Remove new lines and spaces

        byte[] keyBytes = Base64.getDecoder().decode(keyContent);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    /**
     * Reads a key file as a string.
     * @param filePath Path to the key file.
     * @return Key contents as a string.
     * @throws IOException If an error occurs while reading the file.
     */
    private static String readKeyFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(new File(filePath).toPath()));
    }
}
