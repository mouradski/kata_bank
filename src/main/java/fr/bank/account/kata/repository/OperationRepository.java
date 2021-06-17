package fr.bank.account.kata.repository;


import fr.bank.account.kata.model.Operation;

import java.util.List;
import java.util.Optional;

public interface OperationRepository {
    Operation save(Operation operation);

    List<Operation> finOperationsdByAccountId(String accountId);

    Optional<Operation> findLastOperationByAccountId(String accountId);
}
