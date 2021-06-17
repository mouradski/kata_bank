package fr.bank.account.kata.error;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException() {
        super("Account Not Found.");
    }
}
