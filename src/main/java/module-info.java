module com.example.stockfx {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.stockfx to javafx.fxml;
    exports com.example.stockfx;
    exports Controller;
    opens Controller to javafx.fxml;
    exports Model;
    opens Model to javafx.fxml;
    exports DBClasses;
    opens DBClasses to javafx.fxml;
}