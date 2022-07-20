package exceptions;

public class TraderNameExistsException extends Exception {
    private static final String msg = "Trader name already exists!";
    public TraderNameExistsException() {
        super(msg);
    }
}
