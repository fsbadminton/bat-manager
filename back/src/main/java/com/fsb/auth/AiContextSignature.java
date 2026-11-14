package com.fsb.auth;

import com.fsb.exception.AuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

@Component
public class AiContextSignature {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final String secret;

    public AiContextSignature(@Value("${ai.context-secret:}") String secret) {
        this.secret = secret;
    }

    public void verifyIdentity(String platform, String externalUserId, String timestamp, String nonce, String signature) {
        verifyIdentity(platform, externalUserId, true, timestamp, nonce, signature);
    }

    public void verifyIdentity(String platform, String externalUserId, boolean isDm,
                               String timestamp, String nonce, String signature) {
        verify("identity\n" + platform + "\n" + externalUserId + "\n" + isDm + "\n" + timestamp + "\n" + nonce,
                timestamp, nonce, signature);
    }

    public void verifyConfirmation(String platform, String externalUserId, String token,
                                   String idempotencyKey, String timestamp, String nonce, String signature) {
        verifyConfirmation(platform, externalUserId, token, idempotencyKey, true,
                timestamp, nonce, signature);
    }

    public void verifyConfirmation(String platform, String externalUserId, String token,
                                   String idempotencyKey, boolean isDm,
                                   String timestamp, String nonce, String signature) {
        verify("confirm\n" + platform + "\n" + externalUserId + "\n" + token + "\n"
                + idempotencyKey + "\n" + isDm + "\n" + timestamp + "\n" + nonce,
                timestamp, nonce, signature);
    }

    public void verifyWebUser(String userId, String timestamp, String nonce, String signature) {
        verify("web\n" + userId + "\n" + timestamp + "\n" + nonce, timestamp, nonce, signature);
    }

    public String digest(String value) {
        return hmac("binding-token\n" + value);
    }

    public String digestAuditIdentity(String externalUserId) {
        return hmac("audit-external-id\n" + externalUserId);
    }

    public String digestPlatformIdentity(String platform, String externalUserId) {
        return hmac("platform-identity\n" + platform + "\n" + externalUserId);
    }

    public String encryptPlatformExternalId(String platform, String externalUserId) {
        byte[] iv = new byte[12];
        SECURE_RANDOM.nextBytes(iv);
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptionKey(), "AES"),
                    new GCMParameterSpec(128, iv));
            cipher.updateAAD(platform.getBytes(StandardCharsets.UTF_8));
            byte[] encrypted = cipher.doFinal(externalUserId.getBytes(StandardCharsets.UTF_8));
            byte[] payload = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, payload, 0, iv.length);
            System.arraycopy(encrypted, 0, payload, iv.length, encrypted.length);
            return "v2:" + Base64.getUrlEncoder().withoutPadding().encodeToString(payload);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to encrypt platform identity reference", ex);
        }
    }

    public String decryptPlatformExternalId(String platform, String reference) {
        if (reference == null || !reference.startsWith("v2:")) {
            throw new IllegalArgumentException("Unsupported platform identity reference");
        }
        try {
            byte[] payload = Base64.getUrlDecoder().decode(reference.substring(3));
            if (payload.length < 29) throw new IllegalArgumentException("Invalid encrypted identity reference");
            byte[] iv = java.util.Arrays.copyOfRange(payload, 0, 12);
            byte[] encrypted = java.util.Arrays.copyOfRange(payload, 12, payload.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(encryptionKey(), "AES"),
                    new GCMParameterSpec(128, iv));
            cipher.updateAAD(platform.getBytes(StandardCharsets.UTF_8));
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to decrypt platform identity reference", ex);
        }
    }

    private byte[] encryptionKey() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest((secret + "\nplatform-identity-encryption").getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to derive platform identity encryption key", ex);
        }
    }

    public String signIdentity(String platform, String externalUserId, String timestamp, String nonce) {
        return signIdentity(platform, externalUserId, true, timestamp, nonce);
    }

    public String signIdentity(String platform, String externalUserId, boolean isDm,
                               String timestamp, String nonce) {
        return hmac("identity\n" + platform + "\n" + externalUserId + "\n" + isDm + "\n" + timestamp + "\n" + nonce);
    }

    public String signConfirmation(String platform, String externalUserId, String token,
                                   String idempotencyKey, String timestamp, String nonce) {
        return signConfirmation(platform, externalUserId, token, idempotencyKey, true,
                timestamp, nonce);
    }

    public String signConfirmation(String platform, String externalUserId, String token,
                                   String idempotencyKey, boolean isDm, String timestamp, String nonce) {
        return hmac("confirm\n" + platform + "\n" + externalUserId + "\n" + token + "\n"
                + idempotencyKey + "\n" + isDm + "\n" + timestamp + "\n" + nonce);
    }

    public String signWebUser(String userId, String timestamp, String nonce) {
        return hmac("web\n" + userId + "\n" + timestamp + "\n" + nonce);
    }

    private void verify(String value, String timestamp, String nonce, String signature) {
        if (secret == null || secret.length() < 32 || timestamp == null || nonce == null
                || nonce.isBlank() || nonce.length() > 128 || signature == null) {
            throw new AuthException(401, "INVALID_CONTEXT: missing signed AI context");
        }
        long issuedAt;
        try {
            issuedAt = Long.parseLong(timestamp);
        } catch (NumberFormatException ex) {
            throw new AuthException(401, "INVALID_CONTEXT: invalid AI context timestamp");
        }
        if (Math.abs(Instant.now().getEpochSecond() - issuedAt) > 60) {
            throw new AuthException(401, "INVALID_CONTEXT: expired AI context");
        }
        byte[] expected = hmac(value).getBytes(StandardCharsets.UTF_8);
        byte[] supplied = signature.getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(expected, supplied)) {
            throw new AuthException(401, "INVALID_CONTEXT: invalid AI context signature");
        }
    }

    private String hmac(String value) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("AI_CONTEXT_SECRET must contain at least 32 characters");
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to sign AI context", ex);
        }
    }
}
