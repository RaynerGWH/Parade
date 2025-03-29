package account;

/**
 * Utility class for encrypting and decrypting byte arrays using XOR with a simple key.
 */
public class CryptoUtils {

    // The seed used for XOR operations.
    private static final String SEED = "RAYNER";

    // The seed in byte format.
    private static final byte[] SEED_BYTES = SEED.getBytes();

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private CryptoUtils() {
        // Utility class
    }

    /**
     * Performs XOR-based encryption/decryption on the provided input using the {@link #SEED_BYTES}.
     *
     * @param input the byte array to encrypt or decrypt
     * @return the resulting transformed byte array
     */
    public static byte[] xorCipher(byte[] input) {
        byte[] output = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = (byte) (input[i] ^ SEED_BYTES[i % SEED_BYTES.length]);
        }
        return output;
    }
}
