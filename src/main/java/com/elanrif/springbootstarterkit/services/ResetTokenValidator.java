package com.elanrif.springbootstarterkit.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Slf4j
@Component
public class ResetTokenValidator {

    @Value("${nodemailer.reset-token.hmac}")
    private String hmacAlgorithm;

    @Value("${nodemailer.reset-token.secret}")
    private String secretKey;

    public boolean isValidToken(String code, String token) {
        log.debug("Validating reset token");
        try {
            Mac hmac = Mac.getInstance(hmacAlgorithm);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), hmacAlgorithm);
            hmac.init(secretKeySpec);

            byte[] hash = hmac.doFinal(code.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            boolean isValid = hexString.toString().equals(token);
            if (isValid) {
                log.debug("Reset token validated successfully");
            } else {
                log.warn("Reset token validation failed - token mismatch");
            }
            return isValid;
        } catch (Exception e) {
            log.error("Error validating reset token: {}", e.getMessage(), e);
            return false;
        }
    }
}
