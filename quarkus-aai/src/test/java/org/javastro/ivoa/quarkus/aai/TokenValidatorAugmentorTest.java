package org.javastro.ivoa.quarkus.aai;

import io.quarkus.security.ForbiddenException;
import io.quarkus.security.credential.TokenCredential;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.jboss.logmanager.Logger;
import java.util.logging.Level;

/**
 * Unit tests for the `TokenValidatorAugmentor` class.
 *
 * This test class validates the behavior of the `TokenValidatorAugmentor` under various scenarios
 * involving security identity augmentation and token validation. The tests cover cases such as:
 * - Validation of correct and incorrect client IDs.
 * - Handling of missing or malformed token data.
 * - Bypassing token validation for anonymous identities or when security is disabled.
 *
 * Each test case ensures the adherence of the augmentor to its intended functionality
 * by asserting expected behaviors such as access denial, identity augmentation, or bypassing validation.
 */
class TokenValidatorAugmentorTest {

    private TokenValidatorAugmentor validator;
    private SecurityIdentity identity;
    private AuthenticationRequestContext context;
    private TokenCredential tokenCredential;

    private String previousProfile;

    //Used to suppress outputting intentional exception throwing for testing failing use cases.
    private Logger validatorLogger;
    private Level previousLogLevel;

    @BeforeEach
    void setUp() {
        validator = new TokenValidatorAugmentor();
        validator.expectedClientId = "test-client";
        validator.securityEnabled = true;

        identity = mock(SecurityIdentity.class);
        context = mock(AuthenticationRequestContext.class);
        tokenCredential = mock(TokenCredential.class);

        when(identity.isAnonymous()).thenReturn(false);
        when(identity.getCredentials())
                .thenReturn(Set.of(tokenCredential));

        when(tokenCredential.getToken())
                .thenReturn(createToken("{\"client_id\":\"test-client\"}"));

        previousProfile = System.getProperty("quarkus.profile");

        validatorLogger = (org.jboss.logmanager.Logger)
                org.jboss.logmanager.LogManager.getLogManager()
                        .getLogger(TokenValidatorAugmentor.class.getName());

        previousLogLevel = validatorLogger.getLevel();
        validatorLogger.setLevel(Level.OFF);
    }

    @AfterEach
    void restoreProfile() {
        if (previousProfile == null) {
            System.clearProperty("quarkus.profile");
        } else {
            System.setProperty("quarkus.profile", previousProfile);
        }

        validatorLogger.setLevel(previousLogLevel);
    }

    /**
     * Validates that a valid client ID allows the use of the provided identity.
     * <p>
     * This test ensures that when a correct client ID is presented, the system
     * properly maintains the provided security identity without modification.
     * It asserts that the identity returned by the augment process is the same
     * as the identity originally provided.
     */
    @Test
    void validClientIdAllowsIdentity() {
        assertSame(identity, augment());
    }

    /**
     * Validates that the client ID comparison is case-insensitive and ignores leading
     * and trailing whitespace.
     * <p>
     * This test ensures that the system correctly handles client IDs with varying
     * capitalisation and extra whitespace. It simulates a scenario in which the expected
     * client ID contains surrounding whitespace and differs in case from the client ID
     * provided in a token. The test verifies that the system correctly normalises
     * and matches the client IDs, ensuring the provided security identity remains unchanged.
     */
    @Test
    void clientIdComparisonIsCaseInsensitiveAndTrimsWhitespace() {
        validator.expectedClientId = " Test-Client ";
        when(tokenCredential.getToken())
                .thenReturn(createToken(
                        "{\"client_id\":\" test-client \"}"));

        assertSame(identity, augment());
    }

    /**
     * Validates that an incorrect client ID in the token payload results in access denial.
     * <p>
     * This test ensures that when a client ID provided in the token does not match the
     * expected client ID, the system rejects the request by throwing a ForbiddenException.
     * It simulates a scenario where the token contains an unexpected client ID and verifies
     * that the augment process correctly enforces the check.
     */
    @Test
    void incorrectClientIdIsDenied() {
        when(tokenCredential.getToken())
                .thenReturn(createToken(
                        "{\"client_id\":\"another-client\"}"));

        assertThrows(ForbiddenException.class, this::augment);
    }

    /**
     * Validates that when the client ID is missing from the token payload, access is denied.
     * <p>
     * This test ensures that the system requires the presence of a client ID in the token's payload
     * to authorise access. When the client ID is absent, the system should reject the request
     * by throwing a ForbiddenException. This behaviour is tested by simulating a scenario where
     * the token payload does not contain a client ID and invoking the augment process to validate
     * the enforcement of this rule.
     */
    @Test
    void missingClientIdIsDenied() {
        when(tokenCredential.getToken())
                .thenReturn(createToken("{\"sub\":\"user123\"}"));

        assertThrows(ForbiddenException.class, this::augment);
    }

    /**
     * Validates that a malformed token results in access denial.
      * <p>
     * This test ensures that when an invalid JWT token is provided by the token credential,
     * the system rejects the request by throwing a ForbiddenException. It simulates a scenario
     * where the token is syntactically incorrect and verifies that the augment process
     * enforces proper validation to prevent unauthorised access.
     */
    @Test
    void malformedTokenIsDenied() {
        when(tokenCredential.getToken()).thenReturn("not-a-valid-jwt");

        assertThrows(ForbiddenException.class, this::augment);
    }

    /**
     * Validates that a malformed JSON payload in the token results in access denial.
     * <p>
     * This test ensures that when the token credential provides a payload that is not
     * well-formed JSON, the system rejects the request by throwing a ForbiddenException.
     * <p>
     * It simulates a scenario where the token contains an invalid JSON payload
     * and verifies that the augment process enforces proper validation to prevent
     * unauthorised access due to malformed data within the token.
     */
    @Test
    void malformedJsonPayloadIsDenied() {
        when(tokenCredential.getToken())
                .thenReturn(createToken("{invalid-json"));

        assertThrows(ForbiddenException.class, this::augment);
    }

    /**
     * Validates that an anonymous identity is allowed without performing token validation.
     * <p>
     * This test ensures that when a security identity is marked as anonymous and does
     * not provide any credentials (such as basic auth), the augment process allows the identity without
     * invoking token validation. It verifies the following behaviour:
     * - The identity is returned as-is by the augment process.
     * - The `getToken` method of the token credential is never called during the process.
     */
    @Test
    void anonymousIdentityIsAllowedWithoutTokenValidation() {
        when(identity.isAnonymous()).thenReturn(true);
        when(identity.getCredentials()).thenReturn(Set.of());

        assertSame(identity, augment());

        verify(tokenCredential, never()).getToken();
    }

    /**
     * Validates that an identity without token credentials is allowed without performing token validation.
     * For instances such as the security being disabled.
     * <p>
     * This test ensures that when a security identity does not provide any token credentials,
     * the augment process permits the identity without invoking the token validation logic.
     * Specifically, the following behaviour is verified:
     * - The identity returned by the augment process is the same as the initially provided identity.
     * - The `getToken` method of the token credential is never called during the process.
     */
    @Test
    void identityWithoutTokenCredentialIsAllowed() {
        when(identity.getCredentials()).thenReturn(Set.of());

        assertSame(identity, augment());

        verify(tokenCredential, never()).getToken();
    }

    @Test
    void disabledSecurityAllowsIncorrectClientId() {
        validator.securityEnabled = false;
        when(tokenCredential.getToken())
                .thenReturn(createToken(
                        "{\"client_id\":\"another-client\"}"));

        assertSame(identity, augment());
    }

    @Test
    void testProfileSkipsTokenValidation() {
        System.setProperty("quarkus.profile", "test");

        when(tokenCredential.getToken())
                .thenReturn(createToken(
                        "{\"client_id\":\"another-client\"}"));

        assertSame(identity, augment());

        verify(tokenCredential, never()).getToken();
    }

    private SecurityIdentity augment() {
        Uni<SecurityIdentity> result = validator.augment(identity, context);
        return result.await().indefinitely();
    }

    private String createToken(String payload) {
        String encodedPayload = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));

        // These tests exercise payload extraction, not JWT signature validation.
        return "test-header." + encodedPayload + ".test-signature";
    }
}