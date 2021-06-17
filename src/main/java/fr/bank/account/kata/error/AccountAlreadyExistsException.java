package fr.bank.account.kata.error;

public class AccountAlreadyExistsException extends RuntimeException {
    public AccountAlreadyExistsException() {
        super("Account already exists.");
    }
}
