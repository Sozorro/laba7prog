package ru.kessi.server.database;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordManager {
    public static String getHash(String pass) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            byte[] encodedhash = digest.digest(pass.getBytes(StandardCharsets.UTF_8));
            
            // Переводим байты в читаемую шестнадцатеричную строку
            return bytesToHex(encodedhash);
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Алгоритм SHA-256 не найден", e);
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
