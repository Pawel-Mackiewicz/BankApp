# Exception Handling System

BankApp implements a comprehensive exception handling system that ensures consistent error handling, logging, and
transformation into standardized API responses, maintaining clarity and informativeness of messages for end users.

## Core Components

### 1. Base Exception Class: BankAppBaseException

`BankAppBaseException` serves as the foundation of our exception hierarchy:

- Extends `RuntimeException`
- Contains `ErrorCode` for error handling standardization
- Enables exception creation with message and error code
- Serves as the base for all application-specific exceptions
- Provides consistent mapping to HTTP codes and user-friendly messages

### 2. System Architecture

#### 2.1. Exception Handling Layer (`ApiExceptionHandler`)

- Global exception catching mechanism at API level
- Management of specific exception types and their transformation
- Integration with other system components
- Ensuring consistent error response format across the application

#### 2.2. Logging Layer (`ApiErrorLogger`)

- Standard error logging with appropriate severity levels
- Error message formatting
- Differentiation between serious internal errors and validation errors
- Configurable stack trace handling for development environments

#### 2.3. Validation Layer (`ValidationErrorProcessor`)

- Processing only two types of validation errors:
    - `MethodArgumentNotValidException`: exceptions from DTO validation (e.g., with @Valid annotations)
    - `ConstraintViolationException`: exceptions from controller method parameter validation (e.g., @PathVariable,
      @RequestParam)
- Conversion of validation errors to standardized format
- Extraction of validation details (field name, error message, rejected value)

#### 2.4. Error Representation Layer

Components: `BaseApiError`, `ValidationApiError`, `ValidationError`

- Data structures representing errors in API responses
- Class hierarchy enabling handling of different error detail levels
- Immutable objects ensuring thread safety

## Error Classification

### Error Code System

The system uses enums for error categorization:

#### Base Error Codes

- `INTERNAL_ERROR`: Serious internal system errors
- `VALIDATION_ERROR`: Input data validation errors
- `NOT_FOUND`: Resource not found errors
- `FORBIDDEN`: Resource access errors

#### Error Code Properties

- HTTP status associated with error type
- Standard message describing the error
- Logical categorization enabling consistent processing

## API Response Formats

### Basic Error Response

Basic error response structure (`BaseApiError`):

- HTTP status code
- Error title/category
- Detailed error message
- Request path where error occurred
- Error timestamp

Example:

```json
{
  "status": "NOT_FOUND",
  "title": "RESOURCE_NOT_FOUND",
  "message": "The requested resource could not be found",
  "path": "/api/accounts/123456789",
  "timestamp": "21-03-2025 17:23:45"
}
```

### Validation Error Response

Extended structure for validation errors (`ValidationApiError`):

- All fields from `BaseApiError`
- List of detailed validation errors for individual fields

Example:

```json
{
  "status": "BAD_REQUEST",
  "title": "VALIDATION_ERROR",
  "message": "Validation failed for the request",
  "path": "/api/users/register",
  "timestamp": "21-03-2025 17:23:45",
  "errors": [
    {
      "field": "email",
      "message": "Must be a valid email address",
      "rejectedValue": "invalid@email"
    },
    {
      "field": "password",
      "message": "Password must be at least 8 characters long",
      "rejectedValue": "pass"
    }
  ]
}
```

## Implementation Details

### Exception Handling Process

#### Global Exception Handling

1. Catching all unhandled exceptions
2. Mapping exception type to appropriate error code
3. Logging error with appropriate severity level
4. Creating standardized error response

#### Validation Exception Handling

1. Processing of validation errors
2. Extraction of error details for individual fields
3. Generation of extended response with validation details

### Logging System

#### Logging Levels

- `ERROR`: For serious internal errors (INTERNAL_ERROR)
- `WARN`: For validation errors and other irregularities

#### Log Format

- Error code and name
- Request path
- Error message
- Stack trace (optional, configurable per environment)

Example log:

```log
ERROR: Error occurred: INTERNAL_ERROR, Path: /api/transactions/execute, Message: Database connection failed
StackTrace: java.sql.SQLException: Connection refused...
```

## Code Examples

### Global Exception Handler

```java

@ExceptionHandler(Exception.class)
public ResponseEntity<BaseApiError> handleException(Exception ex, WebRequest request) {
    String path = uriHandler.getRequestURI(request);
    ErrorCode errorCode = exceptionMapper.map(ex);
    BaseApiError error = new BaseApiError(errorCode, path);
    logger.logError(errorCode, ex, path);
    return new ResponseEntity<>(error, error.getStatus());
}
```

### DTO Validation Handler

```java

@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ValidationApiError> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException ex, WebRequest request) {
    List<ValidationError> errors = validationErrorProcessor.extractValidationErrors(ex);
    return createValidationErrorResponse(errors, ex, request);
}
```

### Parameter Validation Handler

```java

@ExceptionHandler(ConstraintViolationException.class)
public ResponseEntity<ValidationApiError> handleConstraintViolationException(
        ConstraintViolationException ex, WebRequest request) {
    List<ValidationError> errors = validationErrorProcessor.extractValidationErrors(ex);
    return createValidationErrorResponse(errors, ex, request);
}
```

### ValidationErrorProcessor Implementation

#### Validation Error Processing

```java
// Converting field error from MethodArgumentNotValidException
private ValidationError convert(FieldError fieldError) {
    return new ValidationError(
            fieldError.getField(),
            fieldError.getDefaultMessage(),
            fieldError.getRejectedValue() != null ? fieldError.getRejectedValue().toString() : "");
}

// Converting constraint violation from ConstraintViolationException
private ValidationError convert(ConstraintViolation<?> violation) {
    return new ValidationError(
            violation.getPropertyPath().toString(),
            violation.getMessage(),
            violation.getInvalidValue() != null ? violation.getInvalidValue().toString() : "");
}
```

## Integration Examples

### Controller Integration

Example of controller with validation:

```java

@RestController
@RequestMapping("/api/password")
@Validated
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    @PostMapping("/reset-request")
    public ResponseEntity<Void> requestReset(
            @Valid @RequestBody PasswordResetRequestDTO request
    ) {
        passwordResetService.requestReset(request.getEmail());
        return ResponseEntity.ok().build();
    }
}
```

DTO with validation annotations:

```java
public class PasswordResetRequestDTO {
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    // Getters and setters or immutable constructor
}
```

## Future Development

### Planned Improvements

1. **Error Code Enhancement**
    - Contextual error codes for different modules
    - Error message internationalization

2. **Exception Handling Refinement**
    - Better mapping of external library exceptions
    - Enhanced error context capture

3. **Monitoring Improvements**
    - Advanced error frequency analysis
    - Critical error alerting system
    - Better monitoring system integration

### Future Extensions

1. **Enhanced Error System**
    - Multilingual error messages support
    - Error correlation system
    - Tracking IDs for error chains

2. **Advanced Monitoring**
    - Real-time error alerting
    - Pattern recognition in errors
    - Automatic error categorization

3. **Extended Error Context**
    - Rich error metadata
    - Error correlation IDs
    - Debug context enhancement

## Summary

The BankApp exception handling system provides a robust foundation for:

- Consistent error management
- Comprehensive logging
- Clear error presentation to end users
- Minimized code duplication
- Uniform error message structure

This system can be easily extended while maintaining its core principles of consistency and clarity.