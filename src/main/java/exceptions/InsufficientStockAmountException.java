package exceptions;

public class InsufficientStockAmountException extends Exception{
    final static String message = " has insufficient stocks to complete transaction!";

    public InsufficientStockAmountException(String objectName) {
        super(objectName + message);
    }
}
