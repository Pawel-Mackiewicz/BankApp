```bash
# BankApp Project Structure

BankApp/
├── src/                                          # Source code directory
│   ├── main/                                     # Main application code
│   │   ├── java/                                 # Java source files
│   │   │   └── info/
│   │   │       └── mackiewicz/
│   │   │           └── bankapp/                  
│   │   │               ├── core/                 # Core business domain modules
│   │   │               │   ├── account/          # Account domain module
│   │   │               │   │   ├── exception/    # Account-specific exceptions
│   │   │               │   │   ├── model/        # Account entity models and DTOs
│   │   │               │   │   ├── repository/   # JPA repositories for accounts
│   │   │               │   │   ├── service/      # Account business services
│   │   │               │   │   ├── util/         # Account utility classes
│   │   │               │   │   └── validation/   # Account validation logic
│   │   │               │   │ 
│   │   │               │   ├── transaction/      # Transaction domain module
│   │   │               │   │   ├── exception/    # Transaction-specific exceptions
│   │   │               │   │   ├── model/        # Transaction entity models
│   │   │               │   │   ├── repository/   # JPA repositories for transactions
│   │   │               │   │   ├── service/      # Transaction business services
│   │   │               │   │   └── validation/   # Transaction validation logic
│   │   │               │   │ 
│   │   │               │   └── user/             # User domain module
│   │   │               │       ├── exception/    # User-specific exceptions
│   │   │               │       ├── model/        # User entity models and DTOs
│   │   │               │       ├── repository/   # JPA repositories for users
│   │   │               │       └── service/      # User business services
│   │   │               │   
│   │   │               ├── presentation/         # Presentation layer (UI controllers for Thymleaf engine)
│   │   │               │   
│   │   │               ├── shared/               # Shared components used across modules
│   │   │               │   ├── annotations/      # Custom annotations
│   │   │               │   ├── config/           # Common configuration classes
│   │   │               │   ├── exception/        # Common exception types
│   │   │               │   ├── service/          # Shared services
│   │   │               │   ├── util/             # Utility classes
│   │   │               │   └── validation/       # Common validation mechanisms
│   │   │               │   
│   │   │               ├── system/               # System-wide functionalities
│   │   │               │   ├── banking/          # Banking system operations
│   │   │               │   │   ├── history/      # Transaction history functionality
│   │   │               │   │   │   ├── controller/   # History REST controllers
│   │   │               │   │   │   ├── exception/    # History-specific exceptions
│   │   │               │   │   │   └── service/      # History services
│   │   │               │   │   │ 
│   │   │               │   │   ├── operations/   # Banking operations (transfers, etc.)
│   │   │               │   │   │   ├── controller/   # Operations REST controllers
│   │   │               │   │   │   └── service/      # Operations services
│   │   │               │   │   │ 
│   │   │               │   │   └── shared/       # Shared code for banking subsystems
│   │   │               │   │
│   │   │               │   ├── error/            # Application error handling
│   │   │               │   │   └── handling/     # Global error handling functionality
│   │   │               │   │       ├── core/     # Core error handling mechanisms
│   │   │               │   │       ├── dto/      # Error response DTOs
│   │   │               │   │       ├── logger/   # Error logging components
│   │   │               │   │       ├── mapping/  # Exception to response mapping
│   │   │               │   │       ├── service/  # Error handling services
│   │   │               │   │       └── util/     # Error handling utilities
│   │   │               │   │
│   │   │               │   ├── notification/     # Notification subsystem
│   │   │               │   │   └── email/        # Email notification service
│   │   │               │   │       ├── exception/    # Email-specific exceptions
│   │   │               │   │       └── template/     # Email templates
│   │   │               │   │
│   │   │               │   ├── recovery/         # Account recovery features
│   │   │               │   │   └── password/     # Password recovery functionality
│   │   │               │   │       ├── controller/   # Password reset controllers
│   │   │               │   │       │   ├── dto/      # Password reset DTOs
│   │   │               │   │       ├── exception/    # Password recovery exceptions
│   │   │               │   │       └── service/      # Password recovery services
│   │   │               │   │
│   │   │               │   ├── registration/     # User registration system
│   │   │               │   │   ├── controller/   # Registration controllers
│   │   │               │   │   ├── dto/          # Registration data transfer objects
│   │   │               │   │   ├── exception/    # Registration exceptions
│   │   │               │   │   └── service/      # Registration services
│   │   │               │   │
│   │   │               │   ├── shared/           # Shared code for system modules
│   │   │               │   │
│   │   │               │   ├── token/            # Token management system
│   │   │               │   │   ├── model/        # Token entity models
│   │   │               │   │   ├── repository/   # Token repositories
│   │   │               │   │   └── service/      # Token management services
│   │   │               │   │
│   │   │               │   └── transaction/      # Transaction processing system
│   │   │               │       └── processing/   # Transaction execution engine
│   │   │               │           ├── core/     # Core processing components
│   │   │               │           ├── error/    # Error handling for transactions
│   │   │               │           ├── helpers/  # Helper classes for processing
│   │   │               │           └── locking/  # Concurrency control mechanisms
│   │   │               │
│   │   │               └── BankAppApplication.java  # Main Spring Boot application class
│   │   │
│   │   └── resources/                            # Application resources
│   │       ├── db/                               # Database related resources
│   │       │   └── migration/                    # Flyway database migrations
│   │       ├── static/                           # Static web resources
│   │       │   ├── css/                          # CSS stylesheets
│   │       │   └── js/                           # JavaScript files
│   │       ├── templates/                        # Thymeleaf HTML templates
│   │       ├── application.yaml                  # Main application configuration
│   │       └── logback-spring.xml                # Logging configuration
│   │   
│   └── test/                                     # Test code
│       ├── java/                                
│       │   └── info/
│       │       └── mackiewicz/
│       │           └── bankapp/                  # Test packages mirror main structure
│       │               ├── core/                 # Tests for core domain objects
│       │               ├── integration/          # Integration tests between components
│       │               ├── system/               # Tests for system functionalities
│       │               └── testutils/            # Testing utilities and helpers
│       │     
│       └── resources/                            # Test resources
│           └── application-test.yaml             # Test-specific configuration
│  
├── .github/                                      # GitHub configuration
│   └── workflows/                                # GitHub Actions CI/CD workflows
│       ├── ci.yml                                # Continuous Integration workflow
│       └── ec2-deploy.yml                        # AWS EC2 deployment workflow
│   
├── wiki/                                         # Project documentation
├── Dockerfile                                    # Docker configuration for containerization
├── .env.example                                  # Example environment variables template
├── pom.xml                                       # Maven project configuration
├── README.md                                     # Project overview documentation
└── directory-tree.md                             # This file - project structure documentation
```