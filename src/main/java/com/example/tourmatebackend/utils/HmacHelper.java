package com.example.tourmatebackend.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class HmacHelper {

    public static String hmacSha256(String secret, String data) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secretKey);

            byte[] hashBytes = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hashBytes); // Base64 encoded HMAC
        } catch (Exception e) {
            throw new RuntimeException("Error while generating HMAC SHA256", e);
        }
    }
}
