# Active Context

## Current Focus
Refactoring the Account domain model to improve modularity and maintainability through interface extraction.

## Active Tasks
1. Create new interfaces for Account domain:
   - FinancialOperations
   - AccountOwnerInfo
   - IbanHolder
2. Implement interfaces in Account class
3. Update related services to use interfaces
4. Update unit tests

## Next Steps
1. Review existing service layer for potential interface usage
2. Consider creating dedicated implementations for special account types
3. Update documentation to reflect new architecture

## Technical Decisions
- Keep interfaces focused and cohesive
- Use Java interfaces for better abstraction
- Follow Interface Segregation Principle
- Maintain backward compatibility during refactoring
