# Transaction System in BankApp

BankApp implements a comprehensive transaction processing system that ensures secure, scalable, and reliable financial
operations between bank accounts.

## System Architecture

The transaction system follows a multi-layered architecture:

1. **Controller Layer** (`TransactionController`)
    - Handles HTTP requests related to transactions
    - Implements REST API endpoints for CRUD operations and processing
    - Validates input data and converts it to model objects

2. **Facade Layer** (`TransactionService`)
    - Provides a unified interface for transaction-related operations
    - Delegates tasks to specialized internal services
    - Centralizes transaction management for the entire application

3. **Processing Layer** (`TransactionProcessor`, `TransactionProcessingService`)
    - Implements business logic for transactions
    - Manages account locks during financial operations
    - Handles asynchronous transaction processing

4. **Execution Layer** (`TransactionExecutorRegistry`, `TransactionExecutor`)
    - Implements execution strategies for different transaction types
    - Uses the strategy pattern for extensible processing

## Transaction Lifecycle

### Transaction Registration

1. Client creates a request via `TransactionController`
2. System builds a `Transaction` object using the appropriate builder
3. `TransactionCommandService` registers the transaction with `NEW` status
4. Transaction is saved to the database but not yet processed

### Transaction Processing

1. `TransactionProcessingService` retrieves transactions with `NEW` status
2. For each transaction:
    - Data validation is performed
    - `TransactionProcessor` asynchronously processes the transaction
    - `AccountLockManager` locks involved accounts
    - Transaction status is updated to `PENDING`
    - The appropriate `TransactionExecutor` performs the financial operation
    - Transaction status is updated to `DONE` (or appropriate error)
    - Account locks are released

### Error Handling

When an error occurs during processing:

1. `TransactionErrorHandler` centralizes handling of all error types
2. Transaction status is updated to an appropriate error code
3. All account locks are released regardless of operation outcome

## Transaction Types

The system supports the following transaction types (`TransactionType`):

- **DEPOSIT**: Deposit to an account (no source account)
- **WITHDRAWAL**: Withdrawal from an account
- **TRANSFER_OWN**: Transfer between user's own accounts
- **TRANSFER_INTERNAL**: Transfer to another customer within the same bank
- **TRANSFER_EXTERNAL**: External bank transfer with 1% fee (placeholder - not implemented)
- **FEE**: Fee charged for banking services

## Transaction Statuses

Each transaction has a specific status (`TransactionStatus`):

- **Processing statuses**:
    - `NEW`: Transaction has been created but processing hasn't started
    - `PENDING`: Transaction is being processed

- **Success status**:
    - `DONE`: Transaction was successfully completed

- **Error statuses**:
    - `INSUFFICIENT_FUNDS`: Not enough funds in the source account
    - `VALIDATION_ERROR`: Transaction data validation error
    - `EXECUTION_ERROR`: Error during transaction execution
    - `SYSTEM_ERROR`: Unexpected system error

## Security Mechanisms

- **Lock Management**:
    - `AccountLockManager` ensures atomicity through account locking
    - Locks are always released, even in case of errors (in `finally` block)
    - Detailed logging of all lock/unlock operations

- **Asynchronous Processing**:
    - `@Async` annotation on `TransactionProcessor` enables non-blocking processing
    - Increases system throughput, especially for high transaction volumes

- **Centralized Error Handling**:
    - `TransactionErrorHandler` isolates error handling logic
    - Different strategies for various error types (business vs. system)
    - Detailed logging with appropriate levels (WARN, ERROR)

## Implementation Details

- **Design Patterns**:
    - Facade (`TransactionService`)
    - Strategy (`TransactionExecutor`)
    - Builder (`Transaction.buildTransfer()`, `buildWithdrawal()`, etc.)
    - Registry (`TransactionExecutorRegistry`)

- **Logging**:
    - Detailed logging at DEBUG/INFO/WARN/ERROR levels
    - Diagnostic information for each processing stage
    - Special information for security-related operations

- **Extensibility**:
    - Adding new transaction types requires only:
        1. Adding a value to the `TransactionType` enum
        2. Implementing a corresponding `TransactionExecutor`
        3. Registering the executor in `TransactionExecutorRegistry`
    - No modifications needed to the main processing logic

- **Batch Processing**:
    - `TransactionProcessingService.processAllNewTransactions()` enables periodic processing of pending transactions
    - Implemented as a scheduled task via `SchedulerService` running every 10 minutes

## Future Enhancements

- **External Transfers**: The `TRANSFER_EXTERNAL` type is defined but not fully implemented as it requires integration
  with external banking systems
- **Advanced Notifications**: While notification infrastructure exists through `TransactionErrorNotifier`, it's not
  actively used yet
- **Transaction Analytics**: Future versions will include transaction analysis and reporting features

The transaction system forms the backbone of BankApp's financial operations, ensuring that all monetary transactions are
processed securely, consistently, and reliably.