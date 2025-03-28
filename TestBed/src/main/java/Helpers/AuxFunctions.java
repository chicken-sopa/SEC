package Helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class AuxFunctions {
    private AuxFunctions(){}

    public static String getFunctionSelector(String functionSignature) {
        // Step 1: Keccak-256 hash of the UTF-8 bytes of the signature
        byte[] hash = Hash.sha3(functionSignature.getBytes(StandardCharsets.UTF_8));

        // Step 2: Take the first 4 bytes (function selector)
        byte[] selector = Arrays.copyOfRange(hash, 0, 4);

        // Step 3: Return it as hex string with "0x" prefix
        return Numeric.toHexStringNoPrefix(selector);
    }

    public static String extractWordFromReturnData(ByteArrayOutputStream byteArrayOutputStream) {
        String[] lines = byteArrayOutputStream.toString().split("\\r?\\n");
        JsonObject trace = JsonParser.parseString(lines[lines.length - 1]).getAsJsonObject();

        String memory = trace.get("memory").getAsString();
        JsonArray stack = trace.get("stack").getAsJsonArray();
//        System.out.println(memory);
        int offset = Integer.decode(stack.get(stack.size() - 1).getAsString());
        int size = Integer.decode(stack.get(stack.size() - 2).getAsString());
        String word = memory.substring(2 + offset * 2, 2 + offset * 2 + size * 2);
        System.out.println("Word received from outputStream -> " + word);
        return word;
    }

    public static String extractAddressFromReturnData(ByteArrayOutputStream byteArrayOutputStream) {
        String word = extractWordFromReturnData(byteArrayOutputStream);
        return word.replaceFirst("^0+", "");
    }

    public static boolean extractBooleanFromReturnData(ByteArrayOutputStream byteArrayOutputStream) {
        String word = extractWordFromReturnData(byteArrayOutputStream);
        return word.endsWith("01");
    }

    public static BigInteger extractBigIntegerFromReturnData(ByteArrayOutputStream byteArrayOutputStream) {
        var word = extractWordFromReturnData(byteArrayOutputStream);
        return new BigInteger(word, 16);
    }

    public static String padHexStringTo256Bit(String hexString) {
        if (hexString.startsWith("0x")) {
            hexString = hexString.substring(2);
        }

        int length = hexString.length();
        int targetLength = 64;

        if (length >= targetLength) {
            return hexString.substring(0, targetLength);
        }

        return "0".repeat(targetLength - length) +
                hexString;
    }

    public static String convertIntegerToHex256Bit(int number) {
        BigInteger bigInt = BigInteger.valueOf(number);

        return String.format("%064x", bigInt);
    }
}
