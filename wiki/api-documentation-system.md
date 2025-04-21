# API Documentation System

## Introduction

The BankApp uses a comprehensive API documentation system based on OpenAPI (Swagger) specification. This document
explains how the documentation system works, how to maintain it, and how to extend it when adding new API endpoints.

## Technology Stack

- **OpenAPI 3.0**: Industry standard for API documentation
- **SpringDoc**: Java library that integrates Spring Boot with OpenAPI
- **Swagger UI**: Interactive API documentation interface

## System Structure

### 1. Core Configuration

The core configuration for the OpenAPI documentation is located in `OpenAPIConfig.java`. This class defines the basic
information about the API including title, version, description, and contact information, as well as security
configurations.

```java
@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "basicAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("Bank App API Documentation")
                        .version("0.4.5")
                        .description("API documentation for the Bank App project.")
                        .contact(new Contact()
                            .name("Pawel Mackiewicz")
                            .url("https://github.com/Pawel-Mackiewicz/BankApp")
                            .email("pawel@mackiewicz.info")
                        )
                )
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")));
    }
}
```

### 2. Controller Documentation

The API endpoints are documented using interfaces that define the contract for controller implementations. For example,
`UserControllerInterface.java` contains detailed annotations for all user-related endpoints.

Documentation is added using the following annotations:

- `@Operation`: Describes an API operation (endpoint)
- `@ApiResponses`: Documents possible response types
- `@ApiResponse`: Documents a specific response scenario
- `@RequestBody`: Documents request body requirements
- `@Parameter`: Documents path, query, or header parameters
- `@Schema`: Documents the structure of complex objects
- `@ExampleObject`: Provides examples for requests or responses

Example:

```java
@Operation(summary = "Get user by ID", description = "Retrieves a user by their unique ID.")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "User found", 
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = UserResponseDto.class))),
    @ApiResponse(responseCode = "404", description = "User not found",
        content = @Content(mediaType = "application/json", 
        schema = @Schema(implementation = BaseApiError.class)))
})
ResponseEntity<RestResponse<UserResponseDto>> getUserById(Integer id);
```

### 3. Model Documentation

Data models are documented using annotations such as:

- `@Schema`: Describes the model itself
- `@Schema` on properties: Describes individual fields

## Security Configuration

The OpenAPI documentation is accessible according to the security configuration in `SecurityConfig.java`. In the current
configuration, the Swagger UI and API documentation endpoints are publicly accessible:

```java
.requestMatchers("/swagger-ui.html","/swagger-ui/**","/v3/api-docs/**","/v3/api-docs.yaml")
.

permitAll()
```

## Authentication in Swagger UI

Swagger UI now requires authentication to access protected API endpoints. The authentication is managed through a Basic
Auth mechanism directly within the Swagger UI interface.

### How to Authenticate

1. Access the Swagger UI interface at `/swagger-ui.html`
2. Click on the **Authorize** button (lock icon) in the top right corner
3. Enter your credentials:
    - The credentials can be configured in two ways:
        - Using environment variables defined in the `.env` file (`SPRING_SECURITY_USER_NAME` and
          `SPRING_SECURITY_USER_PASSWORD`)
        - Creating a dedicated user in the application's database
4. Click **Authorize** to log in
5. You can now access and test protected endpoints

### User Authentication Options

#### Option 1: Environment Variables

Set the following variables in your `.env` file:

```
SPRING_SECURITY_USER_NAME=your_username
SPRING_SECURITY_USER_PASSWORD=your_secure_password
```

#### Option 2: Create a Dedicated API User

You can create a dedicated user in the database with appropriate permissions to access the API. This approach is
recommended for production environments.

### Security Best Practices

1. **Use strong passwords** for API access
2. **Create dedicated API users** with limited permissions
3. **Regularly rotate credentials** used for API access
4. **Use HTTPS** in production environments to ensure secure transmission of credentials

## Accessing Documentation

The API documentation is available at the following URLs when the application is running:

- **Swagger UI**: `/swagger-ui.html`
- **OpenAPI Specification**: `/v3/api-docs`
- **YAML format**: `/v3/api-docs.yaml`

## Application Properties Configuration

OpenAPI documentation behavior can be customized via the `application.properties` file:

```properties
# Swagger / OpenAPI configuration
springdoc.swagger-ui.docExpansion=list
springdoc.default-produces-media-type=application/json
springdoc.show-actuator=false
springdoc.model-and-view-allowed=true
```

## Best Practices

### Endpoint Documentation

1. **Use interfaces for controller contracts**:
    - Define all API endpoints in interfaces with complete documentation
    - Implement these interfaces in controller classes

2. **Document all possible responses**:
    - Include success responses (200-level)
    - Include client error responses (400-level)
    - Include server error responses (500-level)

3. **Always provide examples**:
    - For request bodies
    - For response bodies

### Documentation Maintenance

1. **Keep documentation in sync with implementation**:
    - Update documentation whenever API contracts change
    - Include documentation updates in code reviews

2. **Version your API**:
    - Update the version in `OpenAPIConfig.java` when making breaking changes

## Extension Guide

### Adding a New Endpoint

When adding a new API endpoint, follow these steps to ensure proper documentation:

1. Add the endpoint to the appropriate controller interface with complete OpenAPI annotations
2. Include `@Operation` with a clear summary and description
3. Document all parameters using `@Parameter`
4. Document request body using `@RequestBody` with examples
5. Document all possible responses using `@ApiResponses`
6. Implement the interface method in the controller class

### Adding a New Model

When adding a new data model to be used in the API:

1. Add appropriate `@Schema` annotations to the class
2. Document each field with description, example, and constraints
3. If the model includes validation constraints, document them in the field descriptions

## Troubleshooting

### Common Issues

1. **Authentication failures in Swagger UI**:
    - Verify that the correct credentials are being used
    - Check if the user exists in the database or environment variables are set correctly
    - Ensure the user has appropriate permissions

2. **Documentation not showing up**:
    - Ensure endpoints have `@Operation` annotations
    - Check that models have `@Schema` annotations
    - Verify that you are properly authenticated

3. **Authorization errors when testing endpoints**:
    - Make sure you've authorized in the Swagger UI using the lock icon
    - Check that your user has the required permissions for the specific endpoint
    - Verify that your session has not expired (re-authorize if needed)

4. **Examples not displaying correctly**:
    - Verify JSON syntax in examples is correct
    - Check that example values match the described schema

5. **Models not fully documented**:
    - Ensure all DTOs and model classes have proper annotations
    - Check that referenced models also have documentation

## Further Resources

- [SpringDoc Documentation](https://springdoc.org/)
- [OpenAPI 3.0 Specification](https://swagger.io/specification/)
- [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/)
- [Spring Security Basic Authentication](https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/basic.html)