package Controller;

import Model.*;
import exceptions.DataCorruptionException;
import exceptions.InsufficientFundsException;
import exceptions.InsufficientStockAmountException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.sql.SQLException;
import java.util.ArrayList;

public class TransactionsController {

    @FXML
    private Label avlBudget;

    @FXML
    private Label budgetLbl;

    @FXML
    private TableView<CompanyStock> portfolioStocksTable;

    @FXML
    private TextField quantityField;

    @FXML
    private Button submitBtn;

    @FXML
    private TableView<CompanyStock> watchListTable;

    @FXML
    private TableColumn<CompanyStock, Double> changePort;

    @FXML
    private TableColumn<CompanyStock, Double> changeWL;

    @FXML
    private TableColumn<CompanyStock, Integer> inventoryWL;

    @FXML
    private TableColumn<CompanyStock, Integer> ownedPort;

    @FXML
    private ScrollPane portfolioScroll;

    @FXML
    private TableColumn<CompanyStock, Double> pricePort;

    @FXML
    private TableColumn<CompanyStock, Double> priceWL;

    @FXML
    private TableColumn<CompanyStock, String> symbolPort;

    @FXML
    private TableColumn<CompanyStock, String> symbolWL;

    @FXML
    private ScrollPane watchListScroll;

    @FXML
    private Label resultMsg;

    public void resetVisibility(){
        portfolioScroll.setVisible(false);
        watchListScroll.setVisible(false);
        quantityField.setVisible(false);
        quantityField.clear();
        submitBtn.setVisible(false);
        submitBtn.setDisable(true);
        avlBudget.setVisible(false);
        budgetLbl.setVisible(false);
        resultMsg.setVisible(false);
    }

    public void returnVisibility(){
        quantityField.setVisible(true);
        submitBtn.setVisible(true);
        avlBudget.setVisible(true);
        budgetLbl.setVisible(true);
    }

    public void initialize() {
        try {
            resetVisibility();
            setBudgetLbl();
            initializePortfolioTable();
            initializeWatchListTable();
            quantityField.textProperty().addListener((observableValue, oldValue, newValue) -> {
                submitBtn.setDisable(newValue.isBlank());
                if (!newValue.matches("\\d*")) {
                    quantityField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });
        } catch (SQLException e) {
            ViewProtocols.sqlExceptionError(e);
        } catch (DataCorruptionException e) {
            ViewProtocols.dataCorruptionError(e);
        }
    }

    @FXML
    private void openPortfolioStocks(MouseEvent event) {
        try {
            resetVisibility();
            loadPortfolioTable();
            portfolioScroll.setVisible(true);
            returnVisibility();
        } catch (SQLException e) {
            ViewProtocols.sqlExceptionError(e);
        } catch (DataCorruptionException e) {
            ViewProtocols.dataCorruptionError(e);
        }
    }

    @FXML
    void openWatchlistStocks(MouseEvent event) {
        try {
            resetVisibility();
            loadWatchlistTable();
            watchListScroll.setVisible(true);
            returnVisibility();
        } catch (SQLException e) {
            ViewProtocols.sqlExceptionError(e);
        } catch (DataCorruptionException e) {
            ViewProtocols.dataCorruptionError(e);
        }
    }

    private void initializePortfolioTable() throws SQLException, DataCorruptionException {

        portfolioStocksTable.setPlaceholder(new Label("Portfolio is empty"));
        symbolPort.setCellValueFactory(new PropertyValueFactory<>("companySymbol"));
        pricePort.setCellValueFactory(new PropertyValueFactory<>("companyPrice"));
        pricePort.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (value == null || empty) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", value));
                }
            }
        });
        changePort.setCellValueFactory(new PropertyValueFactory<>("companyChange"));
        changePort.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (value == null || empty) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", value * 100) + "%");
                }
            }
        });
        ownedPort.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        loadPortfolioTable();
    }

    private void loadPortfolioTable() throws SQLException, DataCorruptionException {
        portfolioStocksTable.getItems().clear();
        ArrayList<CompanyStock> stocks = Handler.getTrader().getPortfolio().getPortfolioStocks();
        for (CompanyStock ts : stocks)
            portfolioStocksTable.getItems().add(ts);
    }

    private void initializeWatchListTable() throws SQLException, DataCorruptionException {
        watchListTable.setPlaceholder(new Label("Add some stocks to your watchlist!"));
        symbolWL.setCellValueFactory(new PropertyValueFactory<>("companySymbol"));
        priceWL.setCellValueFactory(new PropertyValueFactory<>("companyPrice"));
        priceWL.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (value == null || empty) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", value));
                }
            }
        });
        changeWL.setCellValueFactory(new PropertyValueFactory<>("companyChange"));
        changeWL.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (value == null || empty) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", value * 100) + "%");
                }
            }
        });
        inventoryWL.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        loadWatchlistTable();
    }

    private void loadWatchlistTable() throws SQLException, DataCorruptionException {
        watchListTable.getItems().clear();
        ArrayList<CompanyStock> watchlist = Handler.getTrader().getPortfolio().getWatchlistInventory();
        for (CompanyStock c : watchlist)
            watchListTable.getItems().add(c);
    }

    @FXML
    void executeTransaction(MouseEvent event) {
        if((portfolioScroll.isVisible() && portfolioStocksTable.getSelectionModel().getSelectedItem()==null) ||
                (watchListScroll.isVisible() && watchListTable.getSelectionModel().getSelectedItem()==null))
        {
            setErrorMsg("No stocks selected!");
        }
        else{
            try {
                Order order = null;
                if (portfolioScroll.isVisible()) {
                    order = new SellStock(
                            portfolioStocksTable.getSelectionModel().getSelectedItem().getCompany().getCompanyID(),
                            Integer.parseInt(quantityField.getText()));
                } else if (watchListScroll.isVisible()) {
                    order = new BuyStock(
                            watchListTable.getSelectionModel().getSelectedItem().getCompany().getCompanyID(),
                            Integer.parseInt(quantityField.getText()));
                }
                Handler.getBroker().takeOrder(order);
                Handler.getBroker().placeOrders();
                setBudgetLbl();
                if (portfolioScroll.isVisible())
                    initializePortfolioTable();
                else if (watchListScroll.isVisible())
                    initializeWatchListTable();

                setSuccessMsg();
            } catch (Exception e) {
                if (e instanceof InsufficientFundsException){
                    setErrorMsg("Not enough funds to complete transaction.");
                }
                else if(e instanceof InsufficientStockAmountException) {
                    if (portfolioScroll.isVisible())
                        setErrorMsg("Not enough stock owned to complete transaction.");
                    else if (watchListScroll.isVisible())
                        setErrorMsg("Not enough available inventory to buy.");
                }
                else{
                    e.printStackTrace();
                }
            }
        }
    }

    private void setBudgetLbl() throws SQLException, DataCorruptionException {
        budgetLbl.setText(Handler.getTrader().getPortfolio().getBalanceString());
    }

    private void setErrorMsg(String msg){
        resultMsg.setTextFill(Color.RED);
        resultMsg.setText(msg);
        resultMsg.setVisible(true);
    }

    private void setSuccessMsg(){
        resultMsg.setTextFill(Color.GREEN);
        resultMsg.setText("Transaction successful!");
        resultMsg.setVisible(true);
    }

    public void handleStockChangeEvent(StockChangeEvent event){
        try {
            if(event.getEventType()==StockChangeEvent.PORTFOLIO_CHANGE && portfolioScroll.isVisible())
                loadPortfolioTable();
            else if(event.getEventType()==StockChangeEvent.WATCHLIST_CHANGE && watchListScroll.isVisible())
                loadWatchlistTable();
        } catch (SQLException e) {
            ViewProtocols.sqlExceptionError(e);
        } catch (DataCorruptionException e) {
            ViewProtocols.dataCorruptionError(e);
        }
    }
}
