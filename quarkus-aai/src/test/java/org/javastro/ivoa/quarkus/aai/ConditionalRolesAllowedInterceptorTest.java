
package org.javastro.ivoa.quarkus.aai;

import io.quarkus.security.ForbiddenException;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.inject.Instance;
import jakarta.interceptor.InvocationContext;
import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConditionalRolesAllowedInterceptorTest {

    private ConditionalRolesAllowedInterceptor interceptor;
    private Config config;
    private Instance<SecurityIdentity> identityInstance;
    private SecurityIdentity identity;
    private InvocationContext context;

    @BeforeEach
    void setUp() {
        interceptor = new ConditionalRolesAllowedInterceptor();
        interceptor.rolesEnabled = true;

        config = mock(Config.class);
        identityInstance = mockIdentityInstance();
        identity = mock(SecurityIdentity.class);
        context = mock(InvocationContext.class);

        interceptor.config = config;
        interceptor.identityInstance = identityInstance;

        when(identityInstance.get()).thenReturn(identity);
    }

    /**
     * Tests the behaviour of the {@code enforceRoles} method when the {@code rolesEnabled} property
     * in the {@link ConditionalRolesAllowedInterceptor} is set to {@code false}.
     * <p>
     * This test validates that when roles enforcement is disabled:
     * <p>
     * - The {@code context.proceed()} method is invoked without additional role checks.
     * - No interaction occurs with the {@code identityInstance}.
     * - The method invocation completes successfully without throwing an exception.
     *
     * @throws Exception if an error occurs during method invocation
     */
    @Test
    void rolesDisabledAllowsInvocationWithoutCheckingIdentity() throws Exception {
        interceptor.rolesEnabled = false;

        when(context.proceed()).thenReturn("success");

        assertEquals("success", interceptor.enforceRoles(context));

        verifyNoInteractions(identityInstance);
        verify(context).proceed();
    }

    /**
     * Verifies that an anonymous identity is denied access by the {@code enforceRoles} method of the
     * {@link ConditionalRolesAllowedInterceptor}.
     * <p>
     * This test ensures the following behaviour:
     * - When the {@code SecurityIdentity.isAnonymous()} method returns {@code true}, indicating that
     *   the caller's identity is anonymous, the {@code enforceRoles} method throws a
     *   {@link ForbiddenException}.
     * - Ensures that the {@code context.proceed()} method is never invoked for anonymous users,
     *   indicating that access to the resource is blocked.
     *
     * @throws Exception if an error occurs during method invocation
     */
    @Test
    void anonymousIdentityIsDenied() throws Exception {
        when(identity.isAnonymous()).thenReturn(true);

        assertThrows(
                ForbiddenException.class,
                () -> interceptor.enforceRoles(context));

        verify(context, never()).proceed();
    }

    /**
     * Verifies the behaviour of the {@code enforceRoles} method when the caller possesses a matching
     * role required for the method invocation.
     * <p>
     * This test ensures the following behaviour:
     * - The method's associated role is retrieved from the configuration using the {@code configureMethod} setup.
     * - The caller's role is validated successfully against the required role.
     * - The {@code context.proceed()} method is invoked, allowing the method's execution to proceed.
     * - The method invocation completes successfully, returning the expected result without
     *   triggering access denial or exceptions.
     *
     * @throws Exception if an error occurs during method invocation or role enforcement
     */
    @Test
    void matchingRoleAllowsInvocation() throws Exception {
        configureMethod("viewer");
        when(identity.hasRole("archive-viewer")).thenReturn(true);
        when(context.proceed()).thenReturn("success");

        assertEquals("success", interceptor.enforceRoles(context));
    }

    /**
     * Tests that the {@code enforceRoles} method of the {@link ConditionalRolesAllowedInterceptor}
     * correctly denies access when the caller does not possess a matching role required for the method invocation.
     * <p>
     * This test ensures the following behaviour:
     * - The method's required role is retrieved from the configuration using the {@code configureMethod} setup.
     * - The caller's roles are validated, and it is determined that no matching role exists.
     * - A {@link ForbiddenException} is thrown to indicate that access to the resource is denied.
     * - The {@code context.proceed()} method is never invoked, thus preventing further execution of the target method.
     *
     * @throws Exception if an error occurs during role enforcement or method invocation
     */
    @Test
    void missingMatchingRoleIsDenied() throws Exception {
        configureMethod("viewer");
        when(identity.hasRole("archive-viewer")).thenReturn(false);

        assertThrows(
                ForbiddenException.class,
                () -> interceptor.enforceRoles(context));

        verify(context, never()).proceed();
    }

    /**
     * Validates that the {@code enforceRoles} method allows invocation if the caller possesses
     * at least one of multiple roles specified as required.
     * <p>
     * This test ensures the following behaviours:
     * - Multiple roles required for access are retrieved from the configuration, separated by commas.
     * - Each role is validated against the caller's roles.
     * - If the caller has at least one of the required roles, the {@code context.proceed()} method is invoked,
     *   allowing the method invocation to proceed.
     * - If none of the required roles are present, access is denied by throwing a {@link ForbiddenException}.
     * - The method invocation completes successfully if a matching role is present, returning the expected result.
     *
     * @throws Exception if an error occurs during role enforcement or method invocation
     */
    @Test
    void anyOneOfMultipleRolesIsSufficient() throws Exception {
        configureMethod("viewer");
        when(config.getOptionalValue("viewer", String.class))
                .thenReturn(Optional.of("archive-viewer, archive-admin"));

        when(identity.hasRole("archive-viewer")).thenReturn(false);
        when(identity.hasRole("archive-admin")).thenReturn(true);
        when(context.proceed()).thenReturn("success");

        assertEquals("success", interceptor.enforceRoles(context));
    }

    /**
     * Verifies that the {@code enforceRoles} method of the {@link ConditionalRolesAllowedInterceptor}
     * denies access when a required configuration property is missing.
     * <p>
     * This test validates the following behaviour:
     * - The required configuration property for roles is not provided in the setup.
     * - The {@code enforceRoles} method retrieves an empty {@code Optional} when attempting
     *   to fetch the missing property from the configuration.
     * - A {@link ForbiddenException} is thrown, indicating that access is denied due to
     *   the absence of the required property.
     * - The {@code context.proceed()} method is never invoked, ensuring that access
     *   to the resource is prohibited in such scenarios.
     *
     * @throws Exception if an error occurs during the test execution or role enforcement
     */
    @Test
    void missingConfigurationPropertyIsDenied() throws Exception {
        configureMethod("viewer");
        when(config.getOptionalValue("viewer", String.class))
                .thenReturn(Optional.empty());

        assertThrows(
                ForbiddenException.class,
                () -> interceptor.enforceRoles(context));
    }

    /**
     * Tests the behaviour of the {@code enforceRoles} method in the {@link ConditionalRolesAllowedInterceptor}
     * when a method is annotated with {@link ConditionalRolesAllowed} but the value of the annotation
     * is an empty string.
     * <p>
     * This test ensures the following behaviour:
     * - The method-level annotation {@code @ConditionalRolesAllowed("")} is retrieved during processing.
     * - The empty annotation value is evaluated and does not enforce roles, allowing unrestricted access.
     * - The {@code context.proceed()} method is invoked, enabling the execution of the method.
     * - No interactions occur with the {@code config}, as there are no roles to fetch or validate.
     * - The method invocation completes successfully, returning the expected result.
     *
     * @throws Exception if an error occurs during method invocation or role enforcement
     */
    @Test
    void emptyAnnotationValueAllowsInvocation() throws Exception {
        when(context.getMethod()).thenReturn(
                EmptyAnnotationResource.class.getDeclaredMethod("endpoint"));
        when(context.proceed()).thenReturn("success");

        assertEquals("success", interceptor.enforceRoles(context));

        verifyNoInteractions(config);
    }

    /**
     * Validates the behaviour of the {@code enforceRoles} method in the
     * {@link ConditionalRolesAllowedInterceptor} when a method does not have
     * a {@link ConditionalRolesAllowed} annotation, but its containing class
     * does possess such an annotation.
     * <p>
     * This test ensures the following behaviour:
     * - The method's roles are inherited from the class-level {@code ConditionalRolesAllowed} annotation.
     * - The required role is retrieved from the class-level annotation.
     * - The caller's identity is checked for the required role.
     * - If the caller possesses the role, the {@code context.proceed()} method is invoked,
     *   allowing the execution of the target method.
     * - If the caller lacks the role, access is denied.
     *
     * @throws Exception if an error occurs during role enforcement or method invocation
     */
    @Test
    void classLevelAnnotationIsUsedWhenMethodHasNone()
            throws Exception {
        when(context.getMethod()).thenReturn(
                ClassAnnotationResource.class.getDeclaredMethod("endpoint"));
        when(config.getOptionalValue("viewer", String.class))
                .thenReturn(Optional.of("archive-viewer"));
        when(identity.hasRole("archive-viewer")).thenReturn(true);
        when(context.proceed()).thenReturn("success");

        assertEquals("success", interceptor.enforceRoles(context));
    }

    /**
     * Configures the test environment by mocking and setting up method behaviour for a specified
     * configuration property.
     *
     * @param property the name of the configuration property used to retrieve the test value
     * @throws Exception if an error occurs during reflection or when interacting with mocked objects
     */
    @SuppressWarnings("SameParameterValue")
    private void configureMethod(String property) throws Exception {
        when(context.getMethod()).thenReturn(
                MethodAnnotationResource.class.getDeclaredMethod("endpoint"));
        when(config.getOptionalValue(property, String.class))
                .thenReturn(Optional.of("archive-viewer"));
    }

    /**
     * Represents a resource class that holds methods annotated to enforce role-based access control.
     * The role-based access is determined by the {@code ConditionalRolesAllowed} annotation applied to methods.
     */
    static class MethodAnnotationResource {
        @ConditionalRolesAllowed("viewer")
        public void endpoint() {
        }
    }

    /**
     * Represents a resource class secured with a {@link ConditionalRolesAllowed} annotation.
     * This class allows access to its methods only to users possessing the specified roles.
     * <p>
     * Class-level security enforces that only users with the "viewer" role can access resources
     * defined within this class. Roles are typically evaluated using a security identity during
     * runtime.
     * <p>
     * This class is used as a test case to verify the behavior of the {@code ConditionalRolesAllowedInterceptor}
     * when applying security constraints at the class level.
     * <p>
     * An endpoint method is defined and is protected by the "viewer" role, enabling thorough testing
     * of class-level role enforcement scenarios.
     */
    @ConditionalRolesAllowed("viewer")
    static class ClassAnnotationResource {
        public void endpoint() {
        }
    }

    /**
     * Represents a resource with an endpoint annotated using {@link ConditionalRolesAllowed}.
     * This class is used in scenarios where the roles annotation is present but
     * provides an empty value. The purpose of this class is to validate the behavior
     * of the {@link ConditionalRolesAllowedInterceptor} when the annotation is used
     * with an empty value.
     */
    static class EmptyAnnotationResource {
        @ConditionalRolesAllowed("")
        public void endpoint() {
        }
    }

    /**
     * Creates and returns a mock instance of {@code Instance<SecurityIdentity>}.
     * This is used for testing purposes to simulate interactions with a security identity instance.
     *
     * @return a mocked {@code Instance<SecurityIdentity>} object.
     */
    @SuppressWarnings("unchecked")
    private Instance<SecurityIdentity> mockIdentityInstance() {
        return mock(Instance.class);
    }
}
