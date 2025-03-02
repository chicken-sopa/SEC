package Communication.Helpers;

public final class Constants {

    private static final String algorithm = "SHA256withRSA";
    private static final int KeySize = 2048;

    private Constants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String getAlgorithm() {
        return algorithm;
    }

    public static int getKeySize() {
        return KeySize;
    }
}
