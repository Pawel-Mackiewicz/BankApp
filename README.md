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
  - **Additional Tools**: Git, Docker
- **Online**: [bankapp.mackiewicz.info](http://bankapp.mackiewicz.info)
- **Repo**: https://github.com/Pawel-Mackiewicz/BankApp.git

## What Needs to Be Done on the Frontend?

- **Web App for Managing Bank Accounts**: The current version needs UI/UX improvements and optimization.
- **Mobile App for Managing Bank Accounts**: The same as the web app but optimized for mobile devices.
- **Web/Mobile (Android/iOS) App Simulating an ATM/Cash Deposit Machine**: Enabling deposits and withdrawals in a simulated environment.
- **???**: If you have a great idea, I’d love to hear it!

The scope of work depends on how many people get involved and how fast we progress.

## What Needs to Be Done on the Backend?

The backend is mostly complete but still requires improvements and optimizations. I also have a few ideas for additional features, so if you have solid backend skills, there’s definitely work to be done!

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

## How to run locally

### Requiriments
- Java 21 or newer (never tested it on older version)
- Maven
- MySQL
- Git

### Steps

1. **Clone repo**
   ```bash
   git clone https://github.com/Pawel-Mackiewicz/BankApp.git
   cd BankApp
   ```

2. **DB Config**
   - Create MySQL db and name it bankapp
   - Create a copy of the `.env.example` file and name it `.env`

   - Fill in the `.env` file with your credentials:
   ```bash
   # Database configuration
   DB_URL=jdbc:mysql://localhost:3306/bankapp
   DB_USERNAME=your_username
   DB_PASSWORD=your_password
   PORT=8080

   # Spring Security credentials
   SPRING_SECURITY_USER_NAME=your_admin_username
   SPRING_SECURITY_USER_PASSWORD=your_admin_password

   # Resend email configuration
   RESEND_API_KEY=your_resend_api_key
   APP_BASE_URL=http://localhost:8080
   ```

3. **Build and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
   
The application is configured to check for an admin user on startup. If you've set the `SPRING_SECURITY_USER_NAME` and `SPRING_SECURITY_USER_PASSWORD` in your `.env` file, an admin account will be automatically created on first run.

   The application will be available at: `http://localhost:8080`

## Project Structure

```bash
BankApp/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── info/
│   │   │       └── mackiewicz/
│   │   │           └── bankapp/
│   │   │               ├── account/            # Account functionality
│   │   │               │   ├── controller/     # Account controllers
│   │   │               │   ├── model/          # Account data models
│   │   │               │   ├── repository/     # Account repositories
│   │   │               │   └── service/        # Account services
│   │   │               ├── config/             # Spring configuration
│   │   │               ├── security/           # Security configuration
│   │   │               ├── transaction/        # Transaction functionality
│   │   │               │   ├── controller/     # Transaction controllers
│   │   │               │   ├── model/          # Transaction data models
│   │   │               │   ├── repository/     # Transaction repositories
│   │   │               │   └── service/        # Transaction services
│   │   │               │       └── strategy/   # Transaction strategies
│   │   │               ├── user/               # User functionality
│   │   │               │   ├── controller/     # User controllers
│   │   │               │   ├── model/          # User data models
│   │   │               │   ├── repository/     # User repositories
│   │   │               │   ├── service/        # User services
│   │   │               │   └── validation/     # User data validation
│   │   │               ├── exception/          # Exception handling
│   │   │               └── util/               # Utility classes
│   │   └── resources/
│   │       ├── static/                         # Static resources (CSS, JS, images)
│   │       ├── templates/                      # Thymeleaf templates
│   │       └── application.properties          # Application configuration
│   └── test/                                   # Unit and integration tests
│       ├── java/
│       │   └── info/
│       │       └── mackiewicz/
│       │           └── bankapp/                # Application module tests
│       └── resources/                          # Test resources
├── .env                                        # Local environment variables
├── Dockerfile                                  # Docker configuration
├── pom.xml                                     # Maven configuration and dependencies
└── README.md                                   # Project documentation
```

## Continuous Integration

This project uses GitHub Actions for continuous integration. The pipeline runs on every push to `main` and `develop` branches, as well as on pull requests to these branches.

### Pipeline Features

- Builds and tests the application using Maven on Java 21
- Runs unit tests and verifies code coverage with JaCoCo
- Stores test results and code coverage reports as artifacts

### Artifacts

After each workflow run, the following artifacts are available:
- Test results: Detailed reports from unit test execution
- Coverage report: JaCoCo analysis showing test coverage metrics

You can view workflow runs in the [Actions tab](https://github.com/pawel-mackiewicz/BankApp/actions) of the repository.