package com.elanrif.springbootstarterkit.services;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class ResetTokenValidator {

        private static final String SECRET_KEY = "+IXwddeo/C094APtLGjVSPNn+0HBYzfAXa3lzDRIfGo=";

        public static boolean isValidToken(String code, String token) {
            try {
                // Initiate HMAC with the secret key
                Mac hmac = Mac.getInstance("HmacSHA256");
                SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
                hmac.init(secretKeySpec);

                // Compute the HMAC hash of the random string
                byte[] hash = hmac.doFinal(code.getBytes());

                // Convert the hash to a hexadecimal string
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }

                // Compare the computed token with the provided token
                return hexString.toString().equals(token);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
}
