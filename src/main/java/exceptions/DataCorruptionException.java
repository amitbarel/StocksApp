package exceptions;

public class DataCorruptionException extends Exception {
    private static final long serialVersionUID = 1L;
    private static String messageTemplate = "Data Corruption: ";
    public DataCorruptionException() {
        super(messageTemplate + "not given");
    }

    public DataCorruptionException(String message) {
        super(messageTemplate + message);
    }

    public DataCorruptionException(String colName, String tableName){
        super(messageTemplate + colName + " not found in table: " + tableName);
    }
}