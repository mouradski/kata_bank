package fr.bank.account.kata.service.impl;

import fr.bank.account.kata.error.*;
import fr.bank.account.kata.model.Account;
import fr.bank.account.kata.model.Balance;
import fr.bank.account.kata.model.Operation;
import fr.bank.account.kata.model.OperationType;
import fr.bank.account.kata.repository.AccountRepository;
import fr.bank.account.kata.repository.OperationRepository;
import fr.bank.account.kata.service.AccountService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;

    private OperationRepository operationRepository;

    public AccountServiceImpl(AccountRepository accountRepository,
                              OperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }

    public Account createAccount(String accountId) {
        if (accountRepository.existsAccountById(accountId)) {
            throw new AccountAlreadyExistsException();
        }

        return accountRepository.save(Account.builder().id(accountId).build());
    }

    public Balance deposit(String accountId, Double amount) {
        return executeOperation(accountId, amount, OperationType.CREDIT);
    }

    public Balance withdraw(String accountId, Double amount) {
        return executeOperation(accountId, amount, OperationType.DEBIT);
    }

    public Balance withdrawAll(String accountId) {
        return executeOperation(accountId, getBalanceAmount(accountId), OperationType.DEBIT);
    }

    public List<Operation> listOperations(String accountId) {
        if (!accountRepository.existsAccountById(accountId)) {
            throw new AccountNotFoundException();
        }

        return operationRepository.finOperationsdByAccountId(accountId).stream()
                .collect(Collectors.toList());
    }

    public Balance getBalance(String accountId) {

        if (!accountRepository.existsAccountById(accountId)) {
            throw new AccountNotFoundException();
        }

        Optional<Operation> lastOperation = getLastOperation(accountId);

        if (lastOperation.isPresent()) {
            return Balance.builder()
                    .amount(lastOperation.get().getBalance())
                    .date(lastOperation.get().getDate())
                    .build();
        } else {
            return Balance.builder()
                    .amount(0d)
                    .date(OffsetDateTime.now())
                    .build();
        }
    }

    private Balance executeOperation(String accountId, Double amount, OperationType operationType) {
        if (amount == null || amount == 0) {
            throw new NullOrZeroAmountException();
        }

        if (amount < 0) {
            throw new NegativeAmountException();
        }

        final Account account = getAccount(accountId);

        final Double newBalance = calculateNewBalance(accountId, amount, operationType);

        final OffsetDateTime operationDateTime = OffsetDateTime.now();

        operationRepository.save(Operation.builder()
                .account(account)
                .operationType(operationType)
                .amount(amount)
                .balance(newBalance)
                .date(operationDateTime).build());

        return Balance.builder().date(operationDateTime).amount(newBalance).build();
    }

    private Account getAccount(String accountId) {
        Optional<Account> account = accountRepository.getAccountById(accountId);

        if (account.isPresent()) {
            return account.get();
        } else {
            throw new AccountNotFoundException();
        }
    }

    private Double calculateNewBalance(String accountId, Double operationAmount, OperationType operationType) {
        final Double lastBalance = getBalanceAmount(accountId);

        if (OperationType.CREDIT.equals(operationType)) {
            return lastBalance + operationAmount;
        } else {
            if (operationAmount > lastBalance) {
                throw new InsufficientBalanceException();
            }
            return lastBalance - operationAmount;
        }
    }

    private Double getBalanceAmount(String accountId) {
        if (!accountRepository.existsAccountById(accountId)) {
            throw new AccountNotFoundException();
        }

        Optional<Operation> lastOperation = getLastOperation(accountId);

        if (lastOperation.isPresent()) {
            return lastOperation.get().getBalance();
        } else {
            return 0d;
        }
    }

    private Optional<Operation> getLastOperation(String accountId) {
        return operationRepository.findLastOperationByAccountId(accountId);
    }
}
