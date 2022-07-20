package Controller;

import Model.Handler;
import Model.StockChangeEvent;
import com.example.stockfx.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainScreenController {

    @FXML
    private VBox changesVbox;

    @FXML
    private Label helloUser;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private AnchorPane mainRoot;

    @FXML
    void CommitTA(MouseEvent event) {
        try {
            mainPane.getChildren().clear();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("TransactionsScreen.fxml"));
            Parent tRoot = fxmlLoader.load();
            TransactionsController tc = fxmlLoader.getController();
            mainPane.getChildren().add(tRoot);
            mainRoot.addEventFilter(StockChangeEvent.ANY, tc::handleStockChangeEvent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void Exit(MouseEvent event) {
        ((Stage)mainRoot.getScene().getWindow()).close();
    }

    @FXML
    void ShowHistory(MouseEvent event) {
        try {
            mainPane.getChildren().clear();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("HistoryScreen.fxml"));
            Parent historyRoot = fxmlLoader.load();
            HistoryController hc = fxmlLoader.getController();
            mainPane.getChildren().add(historyRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ShowMarket(MouseEvent event) {
        try {
            mainPane.getChildren().clear();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("StocksShow.fxml"));
            Parent stocksRoot = fxmlLoader.load();
            StocksShowController ssc = fxmlLoader.getController();
            mainPane.getChildren().add(stocksRoot);
            mainRoot.addEventFilter(StockChangeEvent.ANY, ssc::handleStockChangeEvent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ShowPortfolio(MouseEvent event) {
        try {
            mainPane.getChildren().clear();
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("PortfolioScreen.fxml"));
            Parent portfolioRoot = fxmlLoader.load();
            PortfolioController pc = fxmlLoader.getController();
            mainPane.getChildren().add(portfolioRoot);
            mainRoot.addEventFilter(StockChangeEvent.PORTFOLIO_CHANGE, pc::handleStockChangeEvent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        helloUser.setText("Hello " + Handler.getTrader().getUsername());
        Handler.getTrader().setMainRoot(mainRoot);
        mainRoot.addEventFilter(StockChangeEvent.WATCHLIST_CHANGE, this::handleStockChangeEvent);
        Handler.initStockPriceChangeThread();
    }

    public void handleStockChangeEvent(StockChangeEvent event){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("StockChange.fxml"));
            Parent root = fxmlLoader.load();
            StockChangeController scc = fxmlLoader.getController();
            changesVbox.getChildren().add(0,scc.init(event.getCompany()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
