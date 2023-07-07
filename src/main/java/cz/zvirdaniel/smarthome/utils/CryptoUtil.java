package cz.zvirdaniel.smarthome.utils;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CryptoUtil {
    public static String AES_General_Key = "a3K8Bx%2r8Y7#xDh";

    public static String decryptContent(String aesKey, String message) {
        try {
            final Key key = new SecretKeySpec(aesKey.getBytes(), "AES");
            final byte[] imageByte = Base64.getDecoder().decode(message);
            final Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.DECRYPT_MODE, key);
            final byte[] bytePlainText = aesCipher.doFinal(imageByte);
            return new String(bytePlainText);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed!", e);
        }
    }

    public static String encryptContent(String aesKey, String message) {
        try {
            final Key key = new SecretKeySpec(aesKey.getBytes(), "AES");
            final Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, key);
            final byte[] bytePlainText = aesCipher.doFinal(message.getBytes());
            return new String(Base64.getEncoder().encode(bytePlainText));
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed!", e);
        }
    }
}
