package com.misaka.util;

import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AESUtil {

    private static final String AES_ALGORITHM = "AES/ECB/PKCS5Padding";

    public static void main(String[] args) throws Exception {
        String key = System.getProperty("key");
        String data = System.getProperty("data");
        if (!StringUtils.hasText(key)) {
            System.out.println("Key cannot be empty");
            return;
        }
        if (!StringUtils.hasText(data)) {
            System.out.println("Data cannot be empty");
            return;
        }
        int length = key.length();
        if (length != 16) {
            System.out.println("The length of the key must be 16");
            return;
        }
        System.out.printf("Encrypt result: %s\n", encrypt(key, data));
    }

    public static String encrypt(String key, String data) throws Exception {
        Cipher cipher = cipher(key, Cipher.ENCRYPT_MODE);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    public static String decrypt(String key, String data) throws Exception {
        Cipher cipher = cipher(key, Cipher.DECRYPT_MODE);
        return new String(cipher.doFinal(Base64.getDecoder().decode(data)), StandardCharsets.UTF_8);
    }

    private static Cipher cipher(String key, int model) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(model, secretKeySpec);
        return cipher;
    }
}
