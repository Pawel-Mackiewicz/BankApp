package info.mackiewicz.bankapp;

import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.Person;
import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.model.TransactionType;
import info.mackiewicz.bankapp.service.AccountService;
import info.mackiewicz.bankapp.service.PersonService;
import info.mackiewicz.bankapp.service.TransactionService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AsyncTransferTransactionsIntegrationTest {

    @Autowired
    private PersonService personService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    // Wstrzyknięty executor asynchroniczny
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Test
    public void testAsyncTransferTransactionsWithTimeAndDbRetrieval() {
        // Krok 1: Utwórz osobę i 10 kont, każde z saldem początkowym 1000.00.
        Person person = new Person();
        person.setName("Test");
        person.setLastname("User");
        person.setDateOfBirth(LocalDate.of(1990, 1, 1));
        person.setPESEL("11111111111");
        person = personService.createPerson(person);

        List<Account> accounts = new ArrayList<>();
        int numAccounts = 20;
        BigDecimal initialBalance = new BigDecimal("1000.00");
        for (int i = 0; i < numAccounts; i++) {
            Account account = accountService.createAccount(person);
            account = accountService.deposit(account.getId(), initialBalance);
            accounts.add(account);
        }

        // Krok 2: Oblicz łączną sumę sald przed transakcjami.
        BigDecimal initialTotal = accounts.stream()
                .map(a -> accountService.getAccountById(a.getId()).getBalance())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Krok 3: Utwórz 200 losowych transakcji transferu.
        Random random = new Random(42);
        int numTransactions = 100;
        List<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < numTransactions; i++) {
            int fromIndex = random.nextInt(numAccounts);
            int toIndex;
            do {
                toIndex = random.nextInt(numAccounts);
            } while (toIndex == fromIndex);

            Account fromAccount = accounts.get(fromIndex);
            Account toAccount = accounts.get(toIndex);
            BigDecimal amount = new BigDecimal(random.nextInt(100) + 1);

            Transaction transferTx = new Transaction(fromAccount, toAccount, amount, TransactionType.TRANSFER);
            transferTx = transactionService.createTransaction(transferTx);
            transactions.add(transferTx);
        }

        // Krok 4: Mierzenie czasu przetwarzania oraz uruchomienie asynchronicznego przetwarzania.
        long startTime = System.currentTimeMillis();
        transactionService.processAllTransactions(transactions);

        // Dodatkowo czekamy, aż pula wątków (executor) nie będzie miała aktywnych zadań.
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .until(() -> taskExecutor.getActiveCount() == 0);

        long endTime = System.currentTimeMillis();
        long totalProcessingTime = endTime - startTime;
        System.out.println("Total asynchronous processing time (ms): " + totalProcessingTime);

        // Opcjonalna asercja dotycząca czasu – np. poniżej 10 sekund.
        assertTrue(totalProcessingTime < 10000, "Processing time should be less than 10000 ms");

        // Krok 5: Odczytaj wszystkie transakcje z bazy i sprawdź ich liczbę.
        List<Transaction> allTransactions = transactionService.getAllTransactions();
        assertTrue(allTransactions.size() >= numTransactions,
                "There should be at least " + numTransactions + " transactions in the DB.");

        // Krok 6: Sprawdź integralność danych – łączna suma sald musi pozostać niezmieniona.
        BigDecimal finalTotal = accounts.stream()
                .map(a -> accountService.getAccountById(a.getId()).getBalance())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(initialTotal, finalTotal, "Total balance should remain unchanged after processing all transfers.");
    }
}
