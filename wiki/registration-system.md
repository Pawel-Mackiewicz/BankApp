# Registration System in BankApp

BankApp implements a comprehensive user registration system that ensures secure, validated, and user-friendly account
creation process with automatic bank account setup.

## System Architecture

The registration system follows a multi-layered architecture:

1. **Controller Layer** (`RegistrationController`)
    - Handles HTTP requests related to user registration
    - Implements web form endpoints for registration process
    - Validates input data and manages form binding
    - Handles registration errors and success redirects

2. **Service Layer** (`UserRegistrationService`)
    - Orchestrates the entire registration process
    - Manages user creation, account setup, and welcome bonus
    - Delegates tasks to specialized services
    - Ensures transaction integrity during registration

3. **Validation Layer** (`UserValidationService`)
    - Implements comprehensive validation logic
    - Ensures data integrity and business rules
    - Validates uniqueness constraints
    - Enforces security policies

4. **Mapping Layer** (`UserMapper`)
    - Handles DTO to entity conversions
    - Ensures proper data transformation
    - Manages field formatting and normalization

## Registration Process Lifecycle

### Form Presentation

1. User requests registration form via `GET /register`
2. `RegistrationController` prepares empty `UserRegistrationDto`
3. Thymeleaf template renders the registration form

### Data Submission and Validation

1. User submits form data via `POST /register`
2. Spring's validation framework validates `@Valid` annotations
3. Custom validators check business rules:
    - Age verification (minimum 18 years)
    - Password complexity
    - Field formats (PESEL, phone number)
4. `UserValidationService` verifies uniqueness:
    - Email address
    - PESEL number
    - Phone number
    - Username

### Account Creation Process

1. `UserRegistrationService` orchestrates the process:
    - Maps DTO to User entity
    - Creates user account
    - Sets up banking account
    - Processes welcome bonus
    - Sends welcome email
2. Transaction integrity is maintained throughout

## Validation Rules

### Personal Information

- **First Name & Last Name**:
    - Required fields
    - Only Polish letters allowed
    - Proper capitalization enforced

- **PESEL**:
    - Exactly 11 digits
    - Must be unique in system
    - Format validation

- **Date of Birth**:
    - Must be 18+ years old
    - Cannot be in future
    - Maximum age limit: 120 years

### Contact Information

- **Email**:
    - Valid email format
    - Must be unique
    - Used for welcome email

- **Phone Number**:
    - Formats: +48XXXXXXXXX, 0XXXXXXXXX, or XXXXXXXXX
    - Must be unique
    - Polish number format validation

### Security Information

- **Password Requirements**:
    - Minimum 8 characters
    - At least one uppercase letter
    - At least one lowercase letter
    - At least one digit
    - At least one special character (@$!%*?&)
    - Password confirmation must match

## Account Setup

### User Account Creation

1. Basic user entity created
2. Security details configured:
    - Password encryption
    - Account enabled status
    - Credentials validity

### Bank Account Setup

1. New bank account created
2. Account linked to user
3. Welcome bonus processed:
    - 1000 currency units transferred
    - Internal transaction recorded
    - Transfer from bank system account

## Error Handling

- **Validation Errors**:
    - Form-level validation feedback
    - Field-specific error messages
    - Client-side validation for immediate feedback
    - Server-side validation for security

- **Business Rule Violations**:
    - Duplicate entry handling
    - Age restriction enforcement
    - Format violation handling

- **System Errors**:
    - Transaction rollback on failure
    - Proper error logging
    - User-friendly error messages

## Security Mechanisms

- **Data Protection**:
    - Password encryption
    - Sensitive data validation
    - Input sanitization

- **Process Security**:
    - CSRF protection
    - Session management
    - Secure redirects

## Implementation Details

- **Design Patterns**:
    - MVC (Controller-Service-Repository)
    - Builder (User creation)
    - DTO (Data transfer)
    - Validator (Custom validation)

- **Frameworks & Libraries**:
    - Spring MVC
    - Thymeleaf
    - Spring Validation
    - Lombok
    - SLF4J for logging

- **Frontend Validation**:
    - JavaScript immediate feedback
    - Bootstrap styling
    - Form helpers and tooltips

## Future Enhancements

- **Security Improvements**:
    - Two-factor authentication
    - Identity verification service integration

- **User Experience**:
    - Progressive form completion
    - Real-time validation feedback
    - Mobile number verification
    - Email verification process
    - Google auth integration

The registration system provides a robust and secure foundation for user onboarding in BankApp, ensuring data integrity
while maintaining a user-friendly experience.