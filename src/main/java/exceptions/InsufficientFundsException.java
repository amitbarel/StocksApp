package exceptions;

public class InsufficientFundsException extends Exception{
    final static String message = "Insufficient funds to complete transaction!";

    public InsufficientFundsException() {
        super(message);
    }
}
