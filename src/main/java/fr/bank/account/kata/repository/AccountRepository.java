package fr.bank.account.kata.repository;


import fr.bank.account.kata.model.Account;

import java.util.Optional;

public interface AccountRepository {
    Account save(Account account);

    boolean existsAccountById(String accountId);

    Optional<Account> getAccountById(String accountId);
}
