package com.utils;


import liquibase.repackaged.org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;


public class Cypher {
    private static final String ALGORITHM = "AES";

    public static String encrypt(String value, String key) throws Exception {
        Key keyK = new SecretKeySpec(key.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keyK);

        byte[] encValue = cipher.doFinal(value.getBytes());
        byte[] res = new Base64().encode(encValue);
        return new String(res);
    }

    public static String decrypt(String value, String key) throws Exception {
        Key keyK = new SecretKeySpec(key.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keyK);

        byte[] decodedBytes = new Base64().decode(value);
        byte[] res = cipher.doFinal(decodedBytes);

        return new String(res);
    }

    public static String generateRandomSecret() {
        int length = 10;
        boolean useLetters = true;
        boolean useNumbers = false;
        return RandomStringUtils.random(length, useLetters, useNumbers);
    }
}

