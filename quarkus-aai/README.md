Quarkus AAI
===========

functionality to deal with AAI beyond the built-in Quarkus abilities.


## Conditional roles
A number of roles can be used to control access to the application's resources.

1. Define the roles in the `application.properties` file:

```properties
# roles that can edit 
# The key can be any string but the value should be a comma-separated list of OIDC groups
resource.roles.edit=first_oidc_group,second_oidc_group
```

2. Use the `@ConditionalRolesAllowed` annotation to control access to the resource:
```java
// example of a method that requires the edit role, evaluates the property from application.properties 
// to see if the user has the role (via the supplied oidc token)

@Post
@ConditionalRolesAllowed("resource.roles.edit")
@Transactional
public Response getResource() {
    // Perform some action that requires the edit role
    // return Response.ok().entity(resource).build(); for example
}
```

## Token Validation

The `TokenValidatorAugmentor` is used to validate the token received from the OIDC provider. It is started automatically by the Quarkus application and can be disabled by setting the `security.roles.enabled` property to `false` in the `application.properties` file. It is intended to be an extra level of custom validation on top of the validation steps that are performed by quarkus's own ODIC extension. 

It checks that the client id contained in the token matches the one configured in the `application.properties` file (see `quarkus.oidc.client-id`) and that the token has not expired.