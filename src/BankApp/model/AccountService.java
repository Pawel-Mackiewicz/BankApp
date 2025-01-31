package BankApp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AccountService {
    private final List<Account> accounts;
    private final Random random;

    public AccountService() {
        this.accounts = new ArrayList<>();
        this.random = new Random();
    }

    public void initializeAccountsRandomly(int count) {
        for (int i = 0; i < count; i++) {
            int saldo = random.nextInt(1000, 5000);
            accounts.add(new Account(saldo));
        }
    }

    public Account getRandomAccount() {
        return accounts.get(random.nextInt(accounts.size()));
    }

    public Account getAccountById(int id) {
        return accounts.stream()
                .filter(account -> account.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public int getSumOfAllAccounts() {
        return accounts.stream()
                .mapToInt(Account::getBalance)
                .sum();
    }
}
