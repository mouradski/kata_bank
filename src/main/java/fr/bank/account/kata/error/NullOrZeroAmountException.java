package fr.bank.account.kata.error;

public class NullOrZeroAmountException extends RuntimeException {
    public NullOrZeroAmountException() {
        super("Operation with Null or Zero amount not allowed.");
    }
}
