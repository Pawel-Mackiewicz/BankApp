package info.mackiewicz.bankapp.account.service;

import info.mackiewicz.bankapp.account.service.interfaces.FinancialOperations;

public class AccountOperationsService implements FinancialOperations{
    @Override
    public void deposit(BigDecimal amount) {
        // TODO Auto-generated method stub
    }

    @Override
    public void withdraw(BigDecimal amount) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean canWithdraw(BigDecimal amount) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public BigDecimal getBalance() {
        // TODO Auto-generated method stub
        return null;
    }

}
