# Transaction History System in BankApp

BankApp implements a comprehensive transaction history system that allows users to view, filter, sort, and export
history of financial operations on their bank accounts in a secure and efficient manner.

## System Architecture

The transaction history system is built according to a multi-layered architecture pattern:

1. **Controller Layer** (`TransactionHistoryRestController`)
    - Handles HTTP requests related to transaction history
    - Implements REST API endpoints for retrieving and exporting data
   - Manages authorization via Spring Security annotations
   - Exposes REST API interface through `TransactionHistoryRestControllerInterface`

2. **Service Layer** (`TransactionHistoryService`)
    - Provides a unified interface for operations related to transaction history
    - Verifies user permissions to access transaction data
    - Delegates filtering and sorting tasks to specialized services
    - Coordinates data export in various formats
   - Manages pagination of results with a limit of 100 recent transactions

3. **Filtering and Sorting Layer** (`TransactionFilterService`)
    - Implements business logic for filtering transactions based on multiple criteria
    - Provides a mechanism for sorting results by different fields
    - Handles text search in transaction titles and details

4. **Data Export Layer** (`TransactionExporter`, various implementations)
    - Implements strategies for data export in different formats
   - Currently supports CSV (default) and PDF formats
    - Uses the strategy pattern for flexible handling of various export formats

## System Features

### Displaying Transactions

1. User selects an account from the list of available accounts
2. System retrieves transaction history associated with the given account
3. Data is presented in both table and card formats for better readability
4. Identification of incoming and outgoing transactions with appropriate formatting (colors, +/- signs)
5. Display of all relevant transaction details (date, amount, title, type, parties)

### Transaction Filtering

The system allows filtering transactions based on the following criteria:

- Date range (`dateFrom`, `dateTo`)
- Transaction type (`type` - DEPOSIT, WITHDRAWAL, TRANSFER_OWN, etc.)
- Transaction status (`status` - NEW, COMPLETED, FAILED, etc.)
- Amount range (`amountFrom`, `amountTo`)
- Text search (`query`) - searches transaction titles and account data

Filtering is done by:

1. Passing filtering parameters to the `TransactionHistoryService`
2. Applying filters through `TransactionFilterService` using Java streams
3. Returning a filtered, paginated list of results

### Transaction Sorting

The system allows sorting results by various criteria:

- Transaction date (default sorting, descending - newest first)
- Transaction amount
- Transaction type

Sorting is implemented dynamically with the ability to toggle the sorting direction (ascending/descending).

### Data Export

The system offers export of transaction history in various formats:

- CSV (default format)
- PDF (with formatting, colors, and headers)

Export process:

1. User selects the format and initiates export
2. Server identifies the appropriate `TransactionExporter` based on the format
3. Transaction data is retrieved, filtered, and sorted
4. Data is converted to the selected format and sent to the client
5. Browser automatically downloads the file with results

## Security and Validation

The system implements several security mechanisms:

- **Account Ownership Verification**:
    - Spring Security's `@PreAuthorize` annotation ensures that users have access only to their own transactions
    - Authentication uses `@AuthenticationPrincipal` to access the current user
    - Custom `IdAccountAuthorizationService` validates account ownership

- **Error Handling**:
    - Centralization of exception handling through hierarchy of exceptions
    - Base exception class `TransactionBaseException` with error codes
    - Specific exceptions like `InsufficientFundsException` with user-friendly messages
    - Safe communication of error information to the user
    - Detailed diagnostic logging for developers

- **API Security**:
    - All endpoints require user authentication
  - REST endpoints are protected with proper authorization checks

## Detailed Implementation

- **Design Patterns**:
    - Strategy (`TransactionExporter` with implementations like `CsvTransactionExporter`, `PdfTransactionExporter`)
    - Builder (`AbstractTransactionBuilder` for transaction creation)
    - Facade (`TransactionHistoryService`)
    - DTO (`TransactionFilterRequest`, `TransactionResponse`)
    - Decorator (wrapping HTTP responses for export)

- **Pagination**:
    - Implementation through Spring Data's `PageImpl` class
    - Dynamic loading of subsequent pages in the user interface
  - Custom pagination in `TransactionHistoryService.createPaginatedResponse`
    - Infinite scrolling using JavaScript

- **Extensibility**:
    - Adding new export formats requires only implementing the `TransactionExporter` interface
  - Dynamic identification and use of exporters through dependency injection

- **Frontend**:
    - Modular JavaScript architecture divided into:
        - Application state (`TransactionState`)
        - Server communication API (`TransactionAPI`)
        - UI Components (`TransactionUI`)
        - Event handling (`TransactionEvents`)
    - Responsive user interface working on various devices

## Future Improvements

- **Export to New Formats** - ability to add export to XLSX, JSON or other formats
- **Transaction Grouping** - aggregation of transactions by month, category, or other criteria
- **Data Visualization** - charts and graphs showing trends and patterns in transactions
- **Transaction Categorization** - automatic assignment of transactions to categories for better analysis

The transaction history system is a key element of BankApp, enabling users to have full insight into their financial
operations with the possibility of advanced analysis and data export.