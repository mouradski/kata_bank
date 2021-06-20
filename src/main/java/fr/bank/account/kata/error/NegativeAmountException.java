package fr.bank.account.kata.error;

public class NegativeAmountException extends RuntimeException {
    public NegativeAmountException() {
        super("Operation with negative amount not allowed.");
    }
}
