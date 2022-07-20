package Controller;

import Model.Handler;
import Model.StockTransaction;
import exceptions.DataCorruptionException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.ArrayList;

public class HistoryController {

    @FXML
    private TableColumn<StockTransaction, String> dateCol;

    @FXML
    private TableColumn<StockTransaction, Integer> quantityCol;

    @FXML
    private TableColumn<StockTransaction, Double> priceCol;

    @FXML
    private TableColumn<StockTransaction, String> symbolCol;

    @FXML
    private TableColumn<StockTransaction, String> typeCol;

    @FXML
    private TableColumn<StockTransaction, Double> totalCol;

    @FXML
    private TableView<StockTransaction> transactionsTable;

    public void initialize() {
        transactionsTable.getItems().clear();
        transactionsTable.setPlaceholder(new Label("No transactions made"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        symbolCol.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setCellFactory(c -> new TableCell<>() {
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
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        totalCol.setCellValueFactory(new PropertyValueFactory<>("transactionTotal"));
        totalCol.setCellFactory(c -> new TableCell<>() {
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

        try {
            ArrayList<StockTransaction> transactions = Handler.getTrader().getPortfolio().getTransactions();
            for (StockTransaction st : transactions) {
                transactionsTable.getItems().add(st);
            }
        } catch (SQLException e) {
            ViewProtocols.sqlExceptionError(e);
        } catch (DataCorruptionException e) {
            ViewProtocols.dataCorruptionError(e);
        }
    }

    }
