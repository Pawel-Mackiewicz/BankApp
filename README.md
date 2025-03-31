# BankApp

## About
Hi everyone!

I'm working on BankApp – a Java-based banking application for my portfolio. The project has grown significantly, so I'm looking for people who would like to join and help me complete it.

## About the Project

- **Name**: BankApp
- **Purpose**: A banking application for my portfolio
- **Tech Stack**:
  - **Backend**: Java, Spring, Hibernate, MySQL, Maven, JUnit, Mockito
  - **Frontend**: HTML, CSS, JavaScript, Thymeleaf (which I want to replace with a modern frontend)
  - **Additional Tools**: Git, Docker, AWS
- **Online**: [bankapp.mackiewicz.info](http://bankapp.mackiewicz.info)
- **Repo**: https://github.com/Pawel-Mackiewicz/BankApp.git

### Project Structure

```bash
BankApp/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── info/
│   │   │       └── mackiewicz/
│   │   │           └── bankapp/                  # Main application package
│   │   │               ├── account/              # Account management module
│   │   │               │   ├── controller/       # REST controllers for accounts
│   │   │               │   ├── exception/        # Account-related exceptions
│   │   │               │   ├── model/            # Account data models
│   │   │               │   ├── repository/       # Account repositories 
│   │   │               │   ├── service/          # Account business services
│   │   │               │   ├── util/             # Helper utilities
│   │   │               │   └── validation/       # Account data validation
│   │   │               │
│   │   │               ├── notification/         # Notification module
│   │   │               │   └── email/            # Email message handling
│   │   │               │
│   │   │               ├── presentation/         # Presentation layer
│   │   │               │   ├── auth/             # Authentication
│   │   │               │   │   ├── controller/   # Auth controllers
│   │   │               │   │   ├── dto/          # Data transfer objects
│   │   │               │   │   ├── service/      # Auth services
│   │   │               │   │   └── validation/   # Auth data validation
│   │   │               │   │
│   │   │               │   ├── dashboard/        # Dashboard interface
│   │   │               │   │   ├── controller/   # Dashboard controllers
│   │   │               │   │   ├── dto/          # Data transfer objects
│   │   │               │   │   └── service/      # Dashboard services
│   │   │               │   │
│   │   │               │   └── exception/        # Presentation exception handling
│   │   │               │
│   │   │               ├── security/             # Security module
│   │   │               │   ├── controller/       # Security controllers
│   │   │               │   ├── exception/        # Security exceptions
│   │   │               │   ├── model/            # Security models
│   │   │               │   ├── repository/       # Security repositories
│   │   │               │   └── service/          # Security services
│   │   │               │
│   │   │               ├── shared/               # Shared components
│   │   │               │   ├── config/           # Application configuration
│   │   │               │   ├── core/             # Core functionalities
│   │   │               │   ├── dto/              # Common DTOs
│   │   │               │   ├── infrastructure/   # Infrastructure components
│   │   │               │   ├── util/             # Helper utilities
│   │   │               │   ├── validation/       # General validation mechanisms
│   │   │               │   └── web/              # Web components
│   │   │               │
│   │   │               ├── transaction/          # Transaction module
│   │   │               │   ├── config/           # Transaction configuration
│   │   │               │   ├── controller/       # Transaction controllers
│   │   │               │   ├── exception/        # Transaction exceptions
│   │   │               │   ├── model/            # Transaction models
│   │   │               │   ├── repository/       # Transaction repositories
│   │   │               │   ├── service/          # Transaction services
│   │   │               │   └── validation/       # Transaction validation
│   │   │               │
│   │   │               └── user/                 # User module
│   │   │                   ├── controller/       # User controllers
│   │   │                   ├── exception/        # User exceptions
│   │   │                   ├── model/            # User models
│   │   │                   │   └── vo/           # Value Objects
│   │   │                   ├── repository/       # User repositories
│   │   │                   ├── service/          # User services
│   │   │                   └── validation/       # User validation
│   │   │
│   │   └── resources/                            # Application resources
│   │       ├── static/                           # Static resources (CSS, JS, images)
│   │       │   ├── css/                          # CSS styles
│   │       │   ├── js/                           # JavaScript scripts
│   │       │   │   └── modules/                  # JS modules
│   │       │   └── favicon.ico                   # Page icon
│   │       │
│   │       ├── templates/                        # Thymeleaf templates
│   │       │   ├── dashboard.html                # Main dashboard 
│   │       │   ├── login.html                    # Login page
│   │       │   ├── registration.html             # Registration page
│   │       │   ├── settings.html                 # User settings
│   │       │   ├── transactions-history.html     # Transaction history
│   │       │   └── password-reset.html           # Password reset
│   │       │
│   │       ├── application.properties            # Main application configuration
│   │       └── logback.xml                       # Logging configuration
│   │
│   └── test/                                     # Application tests
│       ├── java/                                 # Test code
│       │   └── info/
│       │       └── mackiewicz/
│       │           └── bankapp/                  # Unit and integration tests
│       │
│       └── resources/                            # Test resources
│           ├── application-test.properties       # Test configuration
│           └── test-plans/                       # Test plans
│
├── logs/                                         # Application logs directory
│
├── .env                                          # Environment variables
├── Dockerfile                                    # Docker configuration
├── fly.toml                                      # Fly.io deployment configuration
├── pom.xml                                       # Maven configuration
└── README.md                                     # Project documentation
```

### How to run locally

#### Requiriments
- Java 21 or newer (never tested it on older version)
- Maven
- MySQL
- Git

#### Steps

1. **Clone repo**
   ```bash
   git clone https://github.com/Pawel-Mackiewicz/BankApp.git
   cd BankApp
   ```

2. **DB Config**
   - Create MySQL db and make `bankapp` schema
   - Create a copy of the `.env.example` file and name it `.env`

   - Fill in the `.env` file with your credentials, use `.env.example`:
   ```bash
   # Application configuration
   PORT=8080   # port at which the application will run (default is 8080)
   SPRING_PROFILES_ACTIVE=dev  # active profile for the application (dev/prod)

   # Database configuration
   DB_URL=your_database_url_here
   DB_USERNAME=your_username_here
   DB_PASSWORD=your_password_here

   # Spring Security credentials
   SPRING_SECURITY_USER_NAME=your_admin_username
   SPRING_SECURITY_USER_PASSWORD=your_admin_password

   # Resend email configuration
   RESEND_API_KEY=your_resend_api_key
   APP_BASE_URL=base_url_for_your_app # http://localhost:8080 for local development
   ```

3. **Build and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
   
The application is configured to check for an admin api user on startup. If you've set the `SPRING_SECURITY_USER_NAME` and `SPRING_SECURITY_USER_PASSWORD` in your `.env` file, an admin account will be automatically created on first run.

   The application will be available at: `http://localhost:8080`

### Key Features

#### Transaction System

BankApp's Transaction System provides a robust foundation for secure financial operations between bank accounts. The system handles deposits, withdrawals, and various types of transfers with strong focus on data consistency and error handling.

- **Multi-layered Architecture**: Clean separation of controller, service, and execution layers
- **Security-First Design**: Robust account locking mechanism to prevent race conditions
- **Asynchronous Processing**: Non-blocking transaction execution for improved throughput
- **Comprehensive Error Handling**: Centralized error management with appropriate recovery strategies
- **Automatic Batch Processing**: Scheduled tasks for processing new transactions every 10 minutes

For detailed technical documentation, see the [Transaction System](../../wiki/Transaction-System) page in the wiki.

#### Transaction History System

BankApp implements a comprehensive transaction history system that allows users to view, filter, sort, and export history of financial operations on their bank accounts in a secure and efficient manner.

- **Multi-layered Architecture**: Clean separation between controller, service, and data presentation layers
- **Advanced Filtering**: Robust filtering by date, amount, transaction type, and text search
- **Dynamic Sorting**: Flexible sorting options with toggling direction capabilities
- **Data Export**: Support for exporting transaction history in multiple formats (CSV, PDF)
- **Security-First Design**: Strict account ownership verification and authenticated access

For detailed technical documentation, see the [Transaction History System](../../wiki/Transaction-History-System) page in the wiki.

#### Registration System

BankApp implements a comprehensive user registration system that ensures secure account creation with automatic bank account setup and welcome bonus processing.

- **Multi-layered Architecture**: Clean separation of web, service, and validation layers
- **Comprehensive Validation**: Extensive validation of personal data, contact info, and security requirements
- **Automatic Account Setup**: Automated bank account creation and welcome bonus processing
- **Security-First Design**: Built-in protection against common vulnerabilities and data breaches
- **User-Friendly Experience**: Immediate feedback and clear error messaging
- 
For detailed technical documentation, see the [Registration System](../../wiki/Registration-System) page in the wiki.

#### Password Reset System

The BankApp implements a secure password reset system with the following features:

- **Three-tier architecture**: Web controller, REST API, and service layer
- **Token-based security**: Time-limited tokens for password reset operations
- **Security measures**: Rate limiting, information hiding, and secure error handling
- **Email notifications**: Automatic notifications at each stage of the process
- **Transactional processing**: Ensures data consistency during password changes

For detailed technical documentation, see the [Password Reset System](../../wiki/Password-Reset-System) page in the wiki.

#### Email Notification System

BankApp includes a flexible email notification system that handles various types of user communication. The system utilizes a multi-layered architecture and ensures reliable message delivery.

- **Multi-layered Architecture**: Separation of service layers, templates, and delivery
- **Template System**: Consistent formatting and responsive design for all emails
- **Flexible Integration**: Full integration with registration and password reset processes
- **Error Handling**: Comprehensive error handling with appropriate logging
- **Resend API**: Reliable email delivery through Resend API

For detailed technical documentation, see the [Email Notification System](../../wiki/Email-Notification-System) page in the wiki.

#### Token System

BankApp implements a secure and robust token system primarily used for password reset functionality, ensuring secure handling of sensitive operations through time-limited, single-use tokens.

- **Multi-layered Architecture**: Service layer for token generation, validation, and lifecycle management
- **Security Features**: SHA-256 hashing, rate limiting, and automatic token expiration
- **Token Management**: Single-use tokens with built-in expiration and cleanup mechanisms
- **Error Handling**: Comprehensive exception handling with proper security measures
- **Database Integration**: Efficient token storage with automated cleanup of expired tokens

For detailed technical documentation, see the [Token System](../../wiki/Token-System) page in the wiki.

#### Exception Handling System

BankApp implements a comprehensive exception handling system that ensures consistent error management, logging, and standardized API responses across the entire application.

- **Multi-layered Architecture**: Global exception handler, logging system, and validation processors
- **Standardized Responses**: Unified error response format with consistent HTTP status mapping
- **Validation Framework**: Specialized handling for DTO and parameter validation errors
- **Centralized Logging**: Differentiated logging levels with configurable stack trace handling
- **Error Classification**: Structured error codes system with proper categorization

For detailed technical documentation, see the [Exception Handling System](../../wiki/Exception-Handling-System) page in the wiki.

#### API Documentation System

BankApp implements a comprehensive API documentation system based on OpenAPI (Swagger) specification. The system provides interactive and up-to-date documentation for all API endpoints, supporting development and integration. 

- **OpenAPI 3.0 Standard**: Implementation of the industry-standard specification for API documentation
- **Interactive Documentation**: Swagger UI integration for testing and exploring API endpoints
- **Code-First Approach**: Documentation automatically synchronized with codebase
- **Detailed Examples**: Request and response examples for all endpoints
- **Security Integration**: Authentication requirements documented for each endpoint

##### Accessing Documentation

The API documentation is available at the following URLs when the application is running:

- **Swagger UI**: `/swagger-ui.html`
- **OpenAPI Specification**: `/v3/api-docs`
- **YAML format**: `/v3/api-docs.yaml`

For detailed technical documentation, see the [API Documentation System](../../wiki/API-Documentation-System) page in the wiki.

## What Needs to Be Done on the Frontend?

- **Web App for Managing Bank Accounts**: The current version needs UI/UX improvements and optimization.
- **Mobile App for Managing Bank Accounts**: The same as the web app but optimized for mobile devices.
- **Web/Mobile (Android/iOS) App Simulating an ATM/Cash Deposit Machine**: Enabling deposits and withdrawals in a simulated environment.
- **???**: If you have a great idea, I’d love to hear it!

The scope of work depends on how many people get involved and how fast we progress.

## What Needs to Be Done on the Backend?

The backend is mostly complete but still requires improvements, refactoring of few modules and optimizations. I also have a few ideas for additional features, so if you have solid backend skills, there’s definitely work to be done!

## Who Am I Looking For?

- **Frontend Developers**: I need someone who is already comfortable with frontend development or really wants to master it! Basic HTML, CSS, and JS knowledge won’t be enough – experience with frameworks (React, Angular, or others) would be very useful. Also, the frontend will communicate with the backend via API, so if you’ve worked with APIs before or want to learn, this is a great opportunity!
- **Backend Developers**: Strong Java skills are required. Spring is a plus, but not necessary. If you have experience with Hibernate and MySQL, that’s even better!

- People who can help wrap up the project within 1-2 weeks (or longer if the project expands).

## What Do I Offer?

- Hands-on experience in a real project.
- Collaboration in a friendly and supportive environment.
- A great project you can add to your portfolio.

## Additional Information

The frontend and backend will communicate via API, so the frontend will be in a separate repository.

If you're interested, reach out to me on GitHub or email me at pawel@mackiewicz.info

Looking forward to working with you!

Pawel