package Controller;

import javafx.scene.control.Alert;

public class ViewProtocols {
    public static void systemCloseError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
        System.exit(0);
    }

    public static void dataCorruptionError(Exception e) {
        systemCloseError("Data Corruption Error! Data in database is corrupted!\n"
                + e.getMessage());
    }

    public static void sqlExceptionError(Exception e) {
        systemCloseError("SQL Exception! Transaction could not be complete.\n"
                + e.getMessage());
    }
}
