package fr.bank.account.kata.service;

import fr.bank.account.kata.error.*;
import fr.bank.account.kata.model.Account;
import fr.bank.account.kata.model.Balance;
import fr.bank.account.kata.model.Operation;
import fr.bank.account.kata.model.OperationType;
import fr.bank.account.kata.repository.AccountRepository;
import fr.bank.account.kata.repository.OperationRepository;
import fr.bank.account.kata.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
public class AccountServiceTest {

    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private OperationRepository operationRepository;

    @BeforeEach
    public void init() {
        accountService = new AccountServiceImpl(accountRepository, operationRepository);
    }

    @Test
    public void should_success_when_create_non_existent_account() {
        //Given
        String accountId = UUID.randomUUID().toString();
        Mockito.when(accountRepository.existsAccountById(eq(accountId))).thenReturn(false);

        //When
        accountService.createAccount(accountId);

        //Then
        Mockito.verify(accountRepository).save(Account.builder().id(accountId).build());
    }


    @Test
    public void should_fail_when_create_existent_account() {
        //Given
        String accountId = UUID.randomUUID().toString();
        Mockito.when(accountRepository.existsAccountById(eq(accountId))).thenReturn(true);

        Assertions.assertThrows(
                //Then
                AccountAlreadyExistsException.class,
                //When
                () -> accountService.createAccount(accountId)
        );
    }


    @Test
    public void should_fail_when_execute_operations_on_nonexistent_account() {
        //Given
        String accountId = UUID.randomUUID().toString();
        Mockito.when(accountRepository.existsAccountById(eq(accountId))).thenReturn(false);
        //Mockito.when(accountRepository.getAccountById(eq(accountId))).thenReturn(Optional.empty());
        //Mockito.when(operationRepository.findLastOperationByAccountId(eq(accountId))).thenReturn(Optional.of(Operation.builder().amount(100d).build()));

        Assertions.assertThrows(
                //Then
                AccountNotFoundException.class,
                //When
                () -> accountService.deposit(accountId, 100d)
        );

        //And

        Assertions.assertThrows(
                //Then
                AccountNotFoundException.class,
                //When
                () -> accountService.withdraw(accountId, 100d)
        );

        //And

        Assertions.assertThrows(
                //Then
                AccountNotFoundException.class,
                //When
                () -> accountService.withdrawAll(accountId)
        );

    }

    @Test
    public void should_fail_when_execute_operations_with_zero_or_null_amount() {
        //Given
        String accountId = UUID.randomUUID().toString();

        Assertions.assertThrows(
                //Then
                NullOrZeroAmountException.class,
                //When
                () -> accountService.deposit(accountId, 0d)
        );

        //And

        Assertions.assertThrows(
                //Then
                NullOrZeroAmountException.class,
                //When
                () -> accountService.withdraw(accountId, null)
        );
    }

    @Test
    public void should_fail_when_execute_operations_with_negative_amount() {
        //Given
        String accountId = UUID.randomUUID().toString();

        Assertions.assertThrows(
                //Then
                NegativeAmountException.class,
                //When
                () -> accountService.deposit(accountId, -100d)
        );
    }

    @Test
    public void should_success_when_execute_deposit_operation() {
        //Given
        String accountId = UUID.randomUUID().toString();
        Mockito.when(accountRepository.existsAccountById(eq(accountId))).thenReturn(true);
        Mockito.when(accountRepository.getAccountById(eq(accountId))).thenReturn(Optional.of(Account.builder()
                .id(accountId).build()));
        Mockito.when(operationRepository.findLastOperationByAccountId(eq(accountId)))
                .thenReturn(Optional.of(Operation.builder().balance(1000d).build()));

        //When
        accountService.deposit(accountId, 100d);

        //Then
        Mockito.verify(operationRepository).save(argThat(new OperationMatcher(Operation.builder()
                .operationType(OperationType.CREDIT)
                .account(Account.builder().id(accountId).build())
                .amount(100d)
                .balance(1100d)
                .build())));
    }

    @Test
    public void should_success_when_execute_withdraw_operation() {
        //Given
        String accountId = UUID.randomUUID().toString();
        Mockito.when(accountRepository.existsAccountById(eq(accountId))).thenReturn(true);
        Mockito.when(accountRepository.getAccountById(eq(accountId))).thenReturn(Optional.of(Account.builder()
                .id(accountId).build()));
        Mockito.when(operationRepository.findLastOperationByAccountId(eq(accountId)))
                .thenReturn(Optional.of(Operation.builder().balance(1000d).build()));

        //WHen
        accountService.withdraw(accountId, 100d);

        //Then
        Mockito.verify(operationRepository).save(argThat(new OperationMatcher(Operation.builder()
                .operationType(OperationType.DEBIT)
                .account(Account.builder().id(accountId).build())
                .amount(100d)
                .balance(900d)
                .build())));
    }


    @Test
    public void should_fail_when_execute_withdraw_operation_on_insufficient_balance() {
        //Given
        String accountId = UUID.randomUUID().toString();
        Mockito.when(accountRepository.existsAccountById(eq(accountId))).thenReturn(true);
        Mockito.when(accountRepository.getAccountById(eq(accountId))).thenReturn(Optional.of(Account.builder()
                .id(accountId).build()));
        Mockito.when(operationRepository.findLastOperationByAccountId(eq(accountId)))
                .thenReturn(Optional.of(Operation.builder().balance(1000d).build()));

        Assertions.assertThrows(
                //Then
                InsufficientBalanceException.class,
                //When
                () -> accountService.withdraw(accountId, 1100d)
        );
    }

    @Test
    public void should_success_when_withdraw_all() {
        //Given
        String accountId = UUID.randomUUID().toString();
        Mockito.when(accountRepository.existsAccountById(eq(accountId))).thenReturn(true);
        Mockito.when(accountRepository.getAccountById(eq(accountId))).thenReturn(Optional.of(Account.builder()
                .id(accountId).build()));
        Mockito.when(operationRepository.findLastOperationByAccountId(eq(accountId)))
                .thenReturn(Optional.of(Operation.builder().balance(1000d).build()));

        //When
        accountService.withdrawAll(accountId);

        //Then
        Mockito.verify(operationRepository).save(argThat(new OperationMatcher(Operation.builder()
                .operationType(OperationType.DEBIT)
                .account(Account.builder().id(accountId).build())
                .amount(1000d)
                .balance(0d)
                .build())));
    }


    @Test
    public void should_success_when_list_operations_history() {
        //Given
        String accountId = UUID.randomUUID().toString();
        Account account = Account.builder().id(accountId).build();
        Mockito.when(accountRepository.existsAccountById(eq(accountId))).thenReturn(true);

        List<Operation> operations = Arrays.asList(
                Operation.builder()
                        .account(account).operationType(OperationType.CREDIT)
                        .amount(100d)
                        .balance(1000d)
                        .build(),
                Operation.builder()
                        .account(account).operationType(OperationType.CREDIT)
                        .amount(150d)
                        .balance(1150d)
                        .build(),
                Operation.builder()
                        .account(account).operationType(OperationType.DEBIT)
                        .amount(50d)
                        .balance(1100d)
                        .build());

        Mockito.when(operationRepository.finOperationsdByAccountId(accountId)).thenReturn(operations);

        //When
        final List<Operation> returnedOperations = accountService.listOperations(accountId);

        //Then
        Assertions.assertEquals(operations, returnedOperations);
    }

    @Test
    public void should_fail_when_list_operations_history_of_an_nonexistent_account() {
        //Given
        String accountId = UUID.randomUUID().toString();
        Mockito.when(accountRepository.existsAccountById(eq(accountId))).thenReturn(false);

        Assertions.assertThrows(
                //Then
                AccountNotFoundException.class,
                //When
                () -> accountService.listOperations(accountId)
        );
    }

    @Test
    public void should_success_when_get_account_balance() {
        //Given
        String accountId = UUID.randomUUID().toString();
        Mockito.when(accountRepository.existsAccountById(eq(accountId))).thenReturn(true);
        Mockito.when(operationRepository.findLastOperationByAccountId(eq(accountId))).thenReturn(Optional.of(Operation.builder()
                .account(Account.builder().build()).operationType(OperationType.CREDIT)
                .amount(150d)
                .balance(1150d)
                .build()));

        //When
        final Balance balance = accountService.getBalance(accountId);

        //Then
        Assertions.assertEquals(1150d, balance.getAmount());
    }

    @Test
    public void should_success_and_return_zero_balance_when_no_operations_on_account() {
        //Given
        String accountId = UUID.randomUUID().toString();
        Mockito.when(accountRepository.existsAccountById(eq(accountId))).thenReturn(true);
        Mockito.when(operationRepository.findLastOperationByAccountId(eq(accountId))).thenReturn(Optional.empty());

        //When
        final Balance balance = accountService.getBalance(accountId);

        //Then
        Assertions.assertEquals(0d, balance.getAmount());
    }

    @Test
    public void should_fail_when_get_balance_on_nonexistent_account() {
        //Given
        String accountId = UUID.randomUUID().toString();
        Mockito.when(accountRepository.existsAccountById(eq(accountId))).thenReturn(false);

        Assertions.assertThrows(
                //Then
                AccountNotFoundException.class,
                //When
                () -> accountService.getBalance(accountId)
        );

    }
}

class OperationMatcher implements ArgumentMatcher<Operation> {

    private Operation left;

    public OperationMatcher(Operation left) {
        this.left = left;
    }

    @Override
    public boolean matches(Operation right) {
        return left.equals(right);
    }
}
