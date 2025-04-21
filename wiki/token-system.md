# Token System in BankApp

BankApp implements a secure and robust token system primarily used
for [password reset functionality](../../wiki/Password-Reset-System), ensuring secure handling of sensitive operations
through time-limited, single-use tokens.

## System Architecture

The token system follows a multi-layered architecture with clear separation of concerns:

1. **Service Layer**
    - `TokenService`: Handles token generation and secure hashing
    - `PasswordResetTokenService`: Manages token lifecycle and validation

2. **Repository Layer** (`PasswordResetTokenRepository`)
    - Manages token persistence
    - Handles token queries and cleanup operations
    - Implements JPA repository pattern

3. **Model Layer** (`PasswordResetToken`)
    - Represents token entity
    - Implements token validation logic
    - Manages token state and expiration

## Token Security Features

### Token Generation and Storage

- Uses SecureRandom for token generation
- Implements SHA-256 hashing for token storage
- **Only stores hashed tokens in database**
- Uses Base64URL encoding for token transmission

### Security Measures

- Time-limited tokens (1 hour validity)
- Single-use tokens (consumed after use)
- Rate limiting (max 2 active tokens per user)
- **Protection against timing attacks**
- Automatic cleanup of expired tokens

## Implementation Details

### Token Generation Process

1. **Token Creation**:

```java
String plainToken = tokenService.generateToken();
String tokenHash = tokenService.hashToken(plainToken);
```

2. **Token Storage**:

- Hash is stored in database
- Original token is sent to user
- Token includes:
    - User email
    - Full name
    - Expiration time
    - Usage status

### Validation Flow

1. **Token Receipt**
    - Token is received from user
    - Token is hashed for comparison
    - Database lookup by hash

2. **Validation Checks**
    - Token existence
    - Expiration status
    - Usage status

3. **Token Consumption**
    - Marks token as used
    - Records usage timestamp
    - Prevents reuse

## Error Handling

The system implements comprehensive error handling:

1. **Custom Exceptions**
    - `TokenNotFoundException`: Token not found in database
    - `ExpiredTokenException`: Token has expired
    - `UsedTokenException`: Token already used
    - `TooManyPasswordResetAttemptsException`: Rate limit exceeded
    - `TokenException`: Base exception for token-related errors

2. **Security Considerations**
    - Information hiding in error messages
    - Consistent error handling patterns
    - Proper exception wrapping

## Database Management

### Token Cleanup

- Automated cleanup every 30 days
- Removes expired and used tokens
- Maintains database efficiency

### Queries

- Find by token hash
- Find valid tokens by user
- Count active tokens per user
- Find expired tokens

## Integration Points

### Password Reset Flow

```
1. User requests password reset
2. Token is generated and emailed
3. User submits token with new password
4. Token is validated and consumed
5. Password is updated
6. Confirmation email sent
```

### Rate Limiting Integration

- Maximum 2 active tokens per user
- Automatic token expiration
- Prevents token harvesting attempts

## Current Limitations and Future Improvements

1. **Token Management**
    - No token revocation mechanism
    - Limited token metadata
    - Basic rate limiting

2. **Monitoring**
    - Basic logging only
    - Limited token usage analytics
    - No suspicious activity detection

## Best Practices Implemented

1. **Security**
    - Secure random token generation
    - Hash-based token storage
    - Protection against timing attacks
    - Rate limiting

2. **Performance**
    - Efficient database queries
    - Automated cleanup
    - Indexed token lookups

3. **Maintainability**
    - Clear separation of concerns
    - Comprehensive logging
    - Well-documented code
    - Extensive test coverage

## Configuration

Configurable parameters include:

- `MAX_ACTIVE_TOKENS_PER_USER`: Maximum active tokens per user (default: 2)
- `DEFAULT_CLEANUP_DAYS`: Days before token cleanup (default: 30)
- `EXPIRATION_TIME`: Token validity period (default: 60 minutes from creation)

## Monitoring and Maintenance

1. **Logging**
    - Token creation events
    - Validation attempts
    - Cleanup operations
    - Error conditions

2. **Database Maintenance**
    - Scheduled token cleanup
    - Index maintenance
    - Performance monitoring

The token system provides a secure foundation for password reset functionality in BankApp, ensuring reliable and secure
token-based operations while maintaining good security practices and user experience.