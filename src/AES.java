import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.util.Base64;

// Reference : https://www.section.io/engineering-education/implementing-aes-encryption-and-decryption-in-java/

// AES class contains encrypt, decrypt methods
public class AES {
    private final static int KEY_SIZE = 128;
    private final static int T_LEN = 128;
    private static Cipher encryptionCipher;

    public static SecretKey getSecretKey() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(KEY_SIZE);
        SecretKey key = generator.generateKey();
        return key;
    }

    public static String encrypt(String message, SecretKey key) throws Exception {
        byte[] messageInBytes = message.getBytes();
        encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = encryptionCipher.doFinal(messageInBytes);
        return encode(encryptedBytes);
    }

    public static String decrypt(String encryptedMessage, SecretKey key) throws Exception {
        byte[] messageInBytes = decode(encryptedMessage);
        Cipher decryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, encryptionCipher.getIV());
        decryptionCipher.init(Cipher.DECRYPT_MODE, key, spec);

        byte[] decryptedBytes = decryptionCipher.doFinal(messageInBytes);

        return new String(decryptedBytes);
    }

    private static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private static byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    public static void main(String[] args) {
        try {
            AES aes = new AES();
            SecretKey key = aes.getSecretKey();
            String encryptedMessage = aes.encrypt("Girish", key);
            String decryptedMessage = aes.decrypt(encryptedMessage, key);
            System.err.println("Encrypted Message : " + encryptedMessage);
            System.err.println("Decrypted Message : " + decryptedMessage);

        } catch (Exception ignored) {
        }
    }
}