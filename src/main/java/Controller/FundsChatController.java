package Controller;

import Model.Handler;
import exceptions.DataCorruptionException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.SQLException;

public class FundsChatController {

    @FXML
    private Button AddFundsBtn;

    @FXML
    private Label fundsLabel;

    @FXML
    private TextField amount;

    public void initialize() {
        try {
            AddFundsBtn.setDisable(true);
            fundsLabel.setText(Handler.getTrader().getPortfolio().getBalanceString());
            amount.textProperty().addListener((observableValue, oldValue, newValue) -> {
                AddFundsBtn.setDisable(newValue.isBlank());

                if (!newValue.matches("\\d*")) {
                    amount.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });
        } catch (SQLException e) {
            ViewProtocols.sqlExceptionError(e);
        } catch (DataCorruptionException e) {
            ViewProtocols.dataCorruptionError(e);
        }
    }

    @FXML
    void AddFunds(MouseEvent event) {
        try {
            Handler.getTrader().getPortfolio().addFunds(Double.parseDouble(amount.getText()));
            ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DataCorruptionException e) {
            e.printStackTrace();
        }
    }
}

