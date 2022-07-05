package com.gree.airconditioner.utils;


import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Slf4j
public class CryptoUtil {
    static String AES_General_Key = "a3K8Bx%2r8Y7#xDh";

    public static String decryptPack(final String message) {
        return decryptPack(AES_General_Key.getBytes(), message);
    }

    public static String decryptPack(final byte[] keyArray, final String message) {
        try {
            final Key key = new SecretKeySpec(keyArray, "AES");
            final byte[] imageByte = Base64.getDecoder().decode(message);
            final Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.DECRYPT_MODE, key);
            final byte[] bytePlainText = aesCipher.doFinal(imageByte);
            return new String(bytePlainText);
        } catch (Exception e) {
            log.error("Decrypt failed!", e);
            return null;
        }
    }

    public static String encryptPack(final String message) {
        return encryptPack(AES_General_Key.getBytes(), message);
    }

    public static String encryptPack(final byte[] keyArray, final String message) {
        try {
            final Key key = new SecretKeySpec(keyArray, "AES");
            final Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, key);
            final byte[] bytePlainText = aesCipher.doFinal(message.getBytes());
            return new String(Base64.getEncoder().encode(bytePlainText));
        } catch (Exception e) {
            log.error("Encrypt failed!", e);
            return null;
        }
    }
}
