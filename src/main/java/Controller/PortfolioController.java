package Controller;

import Model.CompanyStock;
import Model.Handler;
import Model.StockChangeEvent;
import com.example.stockfx.Main;
import exceptions.DataCorruptionException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;

public class PortfolioController {
    @FXML
    private VBox portfolioDisplay;

    @FXML
    private Label balanceLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label portfolioIDLabel;

    @FXML
    private RadioButton sortByNameRdo;

    @FXML
    private RadioButton sortByPriceRdo;

    @FXML
    private RadioButton sortByTotalRdo;

    @FXML
    private RadioButton sortByQuantityRdo;

    public void initialize() {
        try {
            initializeRdoButtons();
            nameLabel.setText(Handler.getTrader().getUsername());
            portfolioIDLabel.setText(String.valueOf(Handler.getTrader().getPortfolioID()));
            setBalanceLabel();
            initStockDisplay();
        } catch (SQLException e) {
            ViewProtocols.sqlExceptionError(e);
        } catch (DataCorruptionException e) {
            ViewProtocols.dataCorruptionError(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void OpenFundsChat(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("FundsChat.fxml"));
            Parent root = fxmlLoader.load();
            FundsChatController fcc = fxmlLoader.getController();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Funds");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            setBalanceLabel();
        } catch (SQLException e) {
            ViewProtocols.sqlExceptionError(e);
        } catch (DataCorruptionException e) {
            ViewProtocols.dataCorruptionError(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void sortEvent(MouseEvent event) {
        try {
            initStockDisplay();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            ViewProtocols.sqlExceptionError(e);
        } catch (DataCorruptionException e) {
            ViewProtocols.dataCorruptionError(e);
        }
    }

    private void initializeRdoButtons(){
        ToggleGroup sortBy = new ToggleGroup();
        sortByNameRdo.setToggleGroup(sortBy);
        sortByQuantityRdo.setToggleGroup(sortBy);
        sortByPriceRdo.setToggleGroup(sortBy);
        sortByTotalRdo.setToggleGroup(sortBy);
        sortBy.selectToggle(sortByNameRdo); //default value
    }

    private void initStockDisplay() throws IOException, SQLException, DataCorruptionException {
        portfolioDisplay.getChildren().clear();
        ArrayList<CompanyStock> stocks = Handler.getTrader().getPortfolio().getPortfolioStocks();
        if(sortByNameRdo.isSelected())
            stocks.sort(Comparator.comparing(CompanyStock::getCompanyName));
        else if(sortByQuantityRdo.isSelected())
            stocks.sort(Comparator.comparing(CompanyStock::getQuantity).reversed());
        else if(sortByPriceRdo.isSelected())
            stocks.sort(Comparator.comparing(CompanyStock::getCompanyPrice).reversed());
        else if(sortByTotalRdo.isSelected())
            stocks.sort(Comparator.comparing(CompanyStock::getTotalValue).reversed());

        for (CompanyStock stock : stocks) {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("StockDisplay.fxml"));
            Parent root = fxmlLoader.load();
            StockDisplayController sdc = fxmlLoader.getController();
            portfolioDisplay.getChildren().add(sdc.initialize(stock));
        }
    }

    private void setBalanceLabel() throws SQLException, DataCorruptionException {
        balanceLabel.setText(Handler.getTrader().getPortfolio().getBalanceString());
    }

    public void handleStockChangeEvent(StockChangeEvent event){
        try {
            initStockDisplay();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            ViewProtocols.sqlExceptionError(e);
        } catch (DataCorruptionException e) {
            ViewProtocols.dataCorruptionError(e);
        }
    }

}
