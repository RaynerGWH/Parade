package account;

public class CryptoUtils {
    private static final String SEED = "RAYNER";
    private static final byte[] SEED_BYTES = SEED.getBytes();

    public static byte[] xorCipher(byte[] input) {
        byte[] output = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = (byte) (input[i] ^ SEED_BYTES[i % SEED_BYTES.length]);
        }
        return output;
    }
}
