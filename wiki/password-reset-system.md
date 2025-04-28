# Password Reset System

The BankApp implements a comprehensive and secure password reset solution that follows security best practices while
providing a user-friendly experience.

## System Architecture

The password reset system follows a three-tier architecture:

1. **Presentation Layer** (`PasswordResetWebController`)
    - Handles web forms and user interactions
    - Manages form validation and error presentation
    - Forwards requests to the REST API layer

2. **REST API Layer** (`PasswordResetController`)
    - Exposes endpoints for password reset operations
    - Validates incoming requests
    - Delegates business logic to the service layer

3. **Service Layer** (`PasswordResetService`)
    - Implements core business logic
    - Manages token creation, validation, and consumption
    - Orchestrates the password reset workflow
    - Integrates with email notification system

## Password Reset Workflow

### Requesting a Password Reset

1. User navigates to `/password-reset` and submits their email
2. `PasswordResetWebController` forwards the request to the REST API
3. `PasswordResetService` generates a secure token via `PasswordResetTokenService`
4. A reset link containing the token is sent to the user's email
5. For security reasons, the system provides the same response regardless of whether the email exists

### Completing the Password Reset

1. User clicks the reset link in their email, accessing `/password-reset/token/{token}`
2. System validates the token (checking existence, expiration, and usage status)
3. User enters a new password in the form
4. Upon submission, `PasswordResetService`:
    - Validates the token again
    - Marks the token as used to prevent reuse
    - Updates the user's password
    - Sends a confirmation email

## Security Features

- **Token Security**:
    - One-time use tokens
    - Limited token lifetime
    - Rate limiting to prevent brute force attacks

- **Information Hiding**:
    - No disclosure of user existence during reset request
    - Catching `UserNotFoundException` without informing the user
    - Generic error messages for security-sensitive operations

- **Error Handling**:
    - Comprehensive exception hierarchy for different error cases
    - Detailed server-side logging with appropriate log levels
    - User-friendly error messages that don't leak sensitive information

- **Transactional Processing**:
    - `@Transactional` annotation ensures atomicity of the reset operation
    - All database changes are rolled back if any part of the process fails

## Implementation Details

- **Token Generation**: Secure random tokens created by `PasswordResetTokenService`
- **Email Notifications**: Sent at two key points in the workflow:
    - When a reset is requested (with reset link)
    - When a password change is completed (confirmation)
- **Validation**: Comprehensive input validation at both web and API layers
- **Logging**: Detailed logging at DEBUG/INFO/WARN levels for operational monitoring