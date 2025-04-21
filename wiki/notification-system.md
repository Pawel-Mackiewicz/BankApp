# Email Notification System in BankApp

BankApp implements a flexible and reliable email notification system that handles various types of email communications
with users, ensuring proper templating, delivery, and error handling.

## System Architecture

The email notification system follows a multi-layered architecture:

1. **Service Layer** (`EmailService`)
    - High-level interface for sending various types of emails
    - Handles email content generation through templates
    - Manages error handling and logging
    - Used by business services like `PasswordResetService` and `UserRegistrationService`

2. **Template Layer** (`EmailTemplateProvider`, `EmailTemplate`)
    - Manages email templates and their content
    - Provides consistent styling and formatting
    - Implements template method pattern for content generation
    - Supports various email types through specific template classes

3. **Sending Layer** (`EmailSender`, `ResendEmailSender`)
    - Handles actual email delivery
    - Abstracts email sending mechanism
    - Currently implements Resend API integration
    - Validates email parameters before sending

## Email Template System

### Base Template Structure (`EmailTemplate`)

The system uses a base HTML template that ensures consistent styling across all emails:

- Responsive design (works on mobile devices)
- Consistent branding elements
- Standard CSS styling for buttons, info boxes, and typography
- Support for warning messages and call-to-action buttons

### Template Types

The system supports the following email templates:

1. **Welcome Email** (`WelcomeEmailTemplate`)
    - Sent to new users after registration
    - Personalizes content with user's name and username
    - Provides initial guidance for using the application

2. **Password Reset Request** (`PasswordResetEmailTemplate`)
    - Contains secure password reset link
    - Includes security information and validity period
    - Clear call-to-action button for resetting password

3. **Password Reset Confirmation** (`PasswordResetConfirmationTemplate`)
    - Confirms successful password change
    - Includes security tips
    - Provides direct login link
    - Warning message for unauthorized changes

## Template Variables

Templates use a flexible variable system (`TemplateVariables`) that supports:

- User name personalization
- Dynamic links (reset, login)
- Username insertion
- Other contextual information

## Integration Points

### User Registration Flow

The `UserRegistrationService` uses the email system to:

- Send welcome emails to newly registered users
- Include personalized information:
    - User's full name
    - Username
    - Initial account information
- Process is triggered automatically after successful account creation
- Includes information about the welcome bonus transaction

Example integration:

```java
emailService.sendWelcomeEmail(
        user.getEmail().

toString(), 
    user.

getFullName(), 
    user.

getUsername()
);
```

### Password Reset Flow

The `PasswordResetService` uses the email system to:

- Send initial reset link email
- Send confirmation email after successful reset

### Error Handling

The system implements comprehensive error handling:

1. **Custom Exceptions**
    - `EmailSendingException`: For delivery failures
    - Proper exception wrapping and context preservation

2. **Logging**
    - Detailed error logging with appropriate levels
    - Sensitive information handling in logs
    - Transaction correlation through log context

## Implementation Details

### Email Sender Implementation

1. **Resend Integration** (`ResendEmailSender`)
    - Uses Resend API for reliable delivery
    - Configurable sender address
    - Email validation before sending
    - Proper error handling and retries

### Template Provider Implementation

1. **Default Provider** (`DefaultEmailTemplateProvider`)
    - Manages template instances
    - Handles template variable assembly
    - Creates final email content

### Code Examples

Current implementations show the following patterns:

1. **Service Layer Usage**:

```java

@Service
public class PasswordResetService {
    private final EmailService emailService;

    public void requestReset(String email) {
        // ...existing code...
        emailService.sendPasswordResetEmail(email, token, user.getFullName());
    }
}
```

2. **Error Handling Pattern**:

```java
try{
        emailService.sendPasswordResetEmail(email, token, user.getFullName());
        }catch(
EmailSendingException e){
        throw e;
}catch(
Exception e){
        throw new

EmailSendingException("Failed to send password reset email",e);
}
```

### Configuration

The system is configurable through properties:

- `app.resend.api-key`: Resend API authentication
- `app.email.from-address`: Default sender address
- `app.base-url`: Application URL for email links

## Current Limitations and Areas for Improvement

1. **Template Management**:
    - Current templates are hardcoded
    - Limited customization options
    - No versioning system

2. **Queue Management**:
    - No retry mechanism for failed emails
    - No delayed sending capability
    - Limited bulk email support

3. **Monitoring**:
    - Basic logging only
    - No delivery status tracking
    - Limited analytics capabilities

These limitations are documented for future improvement phases.

## Future Enhancements

1. **Template System**
    - Support for more localization options
    - Dynamic template loading
    - A/B testing support

2. **Delivery Features**
    - Alternative email provider support
    - Bounce handling
    - Delivery status tracking

3. **Integration**
    - Event-based email triggering
    - More business process integration
    - Enhanced template customization

4. **Email Content Security**
    - HTML content sanitization
    - Secure link generation
    - No sensitive data in email body

5. **Delivery Security**
    - Secure API communication
    - Email address validation
    - Rate limiting support

The email notification system provides a robust foundation for all email communications in BankApp, ensuring reliable
delivery and consistent user experience across all types of notifications.