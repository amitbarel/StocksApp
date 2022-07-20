package Controller;

import DBClasses.DBTrader;
import Model.Handler;
import Model.Trader;
import com.example.stockfx.Main;
import exceptions.DataCorruptionException;
import exceptions.TraderNameExistsException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class OpeningController {
    private static final int MAX_STR_LENGTH = 16;

    @FXML
    private AnchorPane openingRoot;

    @FXML
    private Button logButton;

    @FXML
    private TextField traderName;

    @FXML
    private Button startSimulation;

    @FXML
    private Label errorLabel;

    @FXML
    private ComboBox<String> traders;

    public void initialize(){
        try {
            resetButtons();
            ArrayList<String> traderNames = DBTrader.getTraderNames();
            if (traderNames.isEmpty())
                logButton.setDisable(true);
            else
                setTraders(traderNames);

            traderName.textProperty().addListener((observableValue, oldValue, newValue) -> {
                startSimulation.setDisable(newValue.isBlank());
                if (newValue.length() > MAX_STR_LENGTH)
                    traderName.setText(newValue.substring(0, MAX_STR_LENGTH));
            });
        } catch (SQLException e) {
            ViewProtocols.sqlExceptionError(e);
        }
    }

    @FXML
    void logButtonPressed(ActionEvent event) {
        resetButtons();
        traders.setVisible(true);
    }

    @FXML
    void signButtonPressed(ActionEvent event) {
        resetButtons();
        traderName.setVisible(true);
    }

    private void resetButtons(){
        traders.setVisible(false);
        traders.valueProperty().set(null);
        traderName.setVisible(false);
        traderName.clear();
        errorLabel.setVisible(false);
        startSimulation.setDisable(true);
    }

    private void setTraders(ArrayList<String> traderNames)
    {
        traders.getItems().setAll(traderNames);
    }

    @FXML
    void OnChooseTrader(ActionEvent event) {
        startSimulation.setDisable(false);
    }

    @FXML
    void buttonPressed(ActionEvent event) {

    }

    @FXML
    void chooseTrader(MouseEvent event) {

    }

    public void openNewScreen(MouseEvent mouseEvent) {
        try {
            String username = "default";
            if (traders.isVisible()) {
                username = traders.getSelectionModel().getSelectedItem();
            } else if (traderName.isVisible()) {
                username = traderName.getText();
                if (Handler.getTraderNames().contains(username))
                    throw new TraderNameExistsException();
                else if (Handler.saveNewTraderToDB(new Trader(0,username,0)) == 0)
                    throw new SQLException("Trader creation failed!");
            }

            Handler.initHandler(Handler.getTraderByUsername(username));

            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("MainScreen.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage)openingRoot.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Main Screen");

        } catch (SQLException e) {
            ViewProtocols.sqlExceptionError(e);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DataCorruptionException e) {
            ViewProtocols.dataCorruptionError(e);
        } catch (TraderNameExistsException e) {
            errorLabel.setVisible(true);
        }
    }
}