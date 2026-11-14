package com.fsb.auth;

import com.fsb.exception.AuthException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AiContextSignatureTest {
    private static final String SECRET = "0123456789abcdef0123456789abcdef";

    @Test
    void verifiesSignedIdentityAndConfirmationContexts() {
        AiContextSignature signature = new AiContextSignature(SECRET);
        String timestamp = String.valueOf(java.time.Instant.now().getEpochSecond());
        String nonce = "nonce-identity-1";
        String identitySignature = signature.signIdentity("telegram", "user-123", timestamp, nonce);
        String confirmationSignature = signature.signConfirmation(
                "telegram", "user-123", "one-time-code", "request-1", true, timestamp, "nonce-confirm-1");

        assertDoesNotThrow(() -> signature.verifyIdentity(
                "telegram", "user-123", timestamp, nonce, identitySignature));
        String groupSignature = signature.signIdentity("telegram", "user-123", false, timestamp, "nonce-group-1");
        assertDoesNotThrow(() -> signature.verifyIdentity(
                "telegram", "user-123", false, timestamp, "nonce-group-1", groupSignature));
        assertThrows(AuthException.class, () -> signature.verifyIdentity(
                "telegram", "user-123", true, timestamp, "nonce-group-1", groupSignature));
        assertDoesNotThrow(() -> signature.verifyConfirmation(
                "telegram", "user-123", "one-time-code", "request-1", timestamp,
                "nonce-confirm-1", confirmationSignature));
        assertThrows(AuthException.class, () -> signature.verifyConfirmation(
                "telegram", "user-123", "one-time-code", "request-1", false, timestamp,
                "nonce-confirm-1", confirmationSignature));
    }

    @Test
    void rejectsTamperedAndExpiredContexts() {
        AiContextSignature signature = new AiContextSignature(SECRET);
        String timestamp = String.valueOf(java.time.Instant.now().getEpochSecond());
        String nonce = "nonce-2";
        String validSignature = signature.signIdentity("telegram", "user-123", timestamp, nonce);

        assertThrows(AuthException.class, () -> signature.verifyIdentity(
                "telegram", "attacker", timestamp, nonce, validSignature));
        assertThrows(AuthException.class, () -> signature.verifyIdentity(
                "telegram", "user-123", "1", nonce, validSignature));
    }

    @Test
    void refusesWeakSigningSecrets() {
        AiContextSignature signature = new AiContextSignature("short");
        assertThrows(IllegalStateException.class, () -> signature.signWebUser("42", "1", "nonce"));
    }

    @Test
    void auditIdentityDigestIsKeyedAndDomainSeparated() {
        AiContextSignature first = new AiContextSignature(SECRET);
        AiContextSignature second = new AiContextSignature("abcdef0123456789abcdef0123456789");

        assertEquals(first.digestAuditIdentity("12345"), first.digestAuditIdentity("12345"));
        assertNotEquals(first.digestAuditIdentity("12345"), second.digestAuditIdentity("12345"));
        assertNotEquals(first.digestAuditIdentity("12345"), first.digest("12345"));
    }

    @Test
    void platformIdentityDigestIsKeyedStableAndPlatformScoped() {
        AiContextSignature first = new AiContextSignature(SECRET);
        AiContextSignature second = new AiContextSignature("abcdef0123456789abcdef0123456789");

        assertEquals(first.digestPlatformIdentity("telegram", "12345"),
                first.digestPlatformIdentity("telegram", "12345"));
        assertNotEquals(first.digestPlatformIdentity("telegram", "12345"),
                first.digestPlatformIdentity("qq", "12345"));
        assertNotEquals(first.digestPlatformIdentity("telegram", "12345"),
                second.digestPlatformIdentity("telegram", "12345"));
        assertNotEquals(first.digestPlatformIdentity("telegram", "12345"),
                java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(
                        sha256("telegram\u000012345")));
    }

    @Test
    void platformIdentityReferenceIsAuthenticatedAndDoesNotContainExternalId() {
        AiContextSignature signature = new AiContextSignature(SECRET);
        String encrypted = signature.encryptPlatformExternalId("telegram", "sensitive-user-123");

        assertEquals("sensitive-user-123", signature.decryptPlatformExternalId("telegram", encrypted));
        assertNotEquals(encrypted, signature.encryptPlatformExternalId("qq", "sensitive-user-123"));
        org.junit.jupiter.api.Assertions.assertNotEquals(encrypted,
                signature.encryptPlatformExternalId("telegram", "sensitive-user-123"));
        org.junit.jupiter.api.Assertions.assertFalse(encrypted.contains("sensitive-user-123"));
        assertThrows(IllegalStateException.class,
                () -> signature.decryptPlatformExternalId("qq", encrypted));
    }

    private byte[] sha256(String value) {
        try {
            return java.security.MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (java.security.NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
