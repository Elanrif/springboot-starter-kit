package com.elanrif.springbootstarterkit.services;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Slf4j
@Component
public class ResetTokenValidator {

    private static final Logger logger = LoggerFactory.getLogger(ResetTokenValidator.class);

    @Value("${nodemailer.reset-token.hmac}")
    private String hmacAlgorithm;

    @Value("${nodemailer.reset-token.secret}")
    private String secretKey;

    public boolean isValidToken(String code, String token) {
        log.debug("Validating reset token");
        try {
            // Initiate HMAC with the secret key
            Mac hmac = Mac.getInstance(hmacAlgorithm);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), hmacAlgorithm);
            hmac.init(secretKeySpec);

            // Compute the HMAC hash of the random code string
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
            logger.error("Error validating token", e);
            return false;
        }
    }
}
