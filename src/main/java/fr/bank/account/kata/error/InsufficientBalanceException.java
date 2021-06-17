package fr.bank.account.kata.error;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException() {
        super("Insufficient Balance.");
    }
}
