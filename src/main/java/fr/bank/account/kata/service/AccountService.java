package fr.bank.account.kata.service;

import fr.bank.account.kata.model.Account;
import fr.bank.account.kata.model.Balance;
import fr.bank.account.kata.model.Operation;

import java.util.List;

public interface AccountService {
    Account createAccount(String accountId);

    Balance deposit(String accountId, Double amount);

    Balance withdraw(String accountId, Double amount);

    Balance withdrawAll(String accountId);

    List<Operation> listOperations(String accountId);

    Balance getBalance(String accountId);
}
