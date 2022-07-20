    package Controller;

    import Model.Company;
    import Model.Handler;
    import Model.StockChangeEvent;
    import exceptions.CompanyNotFoundException;
    import exceptions.DataCorruptionException;
    import javafx.fxml.FXML;
    import javafx.scene.control.Label;
    import javafx.scene.control.TableCell;
    import javafx.scene.control.TableColumn;
    import javafx.scene.control.TableView;
    import javafx.scene.control.cell.PropertyValueFactory;
    import javafx.scene.input.MouseEvent;
    import javafx.scene.paint.Color;

    import java.sql.SQLException;
    import java.util.ArrayList;

    public class StocksShowController {

        @FXML
        private TableView<Company> stocksTable;

        @FXML
        private TableColumn<Company, Double> changeCol;

        @FXML
        private TableColumn<Company, String> nameCol;

        @FXML
        private TableColumn<Company, Double> priceCol;

        @FXML
        private TableColumn<Company, String> symbolCol;

        @FXML
        private TableColumn<Company, Integer> volumeCol;

        @FXML
        private TableColumn<Company, Integer> yearCol;

        @FXML
        private TableColumn<Company, Double> watchChangeCol;

        @FXML
        private TableColumn<Company, String> watchNameCol;

        @FXML
        private TableColumn<Company, Double> watchPriceCol;

        @FXML
        private TableColumn<Company, String> watchSymbolCol;

        @FXML
        private TableColumn<Company, Integer> watchVolumeCol;

        @FXML
        private TableColumn<Company, Integer> watchYearCol;

        @FXML
        private TableView<Company> watchlistTable;

        @FXML
        private Label resultMsg;

        private void setErrorMsg(String msg){
            resultMsg.setTextFill(Color.RED);
            resultMsg.setText(msg);
            resultMsg.setVisible(true);
        }

        private void setSuccessMsg(String msg){
            resultMsg.setTextFill(Color.GREEN);
            resultMsg.setText(msg);
            resultMsg.setVisible(true);
        }

        public void initialize() {
            resultMsg.setVisible(false);
            try {
                initStockMarketTable();
                initWatchlistTable();
            } catch (SQLException e) {
                ViewProtocols.sqlExceptionError(e);
            } catch (DataCorruptionException e) {
                ViewProtocols.dataCorruptionError(e);
            }
        }

        protected void handleStockChangeEvent(StockChangeEvent event) {
            try {
                reloadTables();
            } catch (SQLException e) {
                ViewProtocols.sqlExceptionError(e);
            } catch (DataCorruptionException e) {
                ViewProtocols.dataCorruptionError(e);
            }
        }

        public void reloadTables() throws SQLException, DataCorruptionException {
            loadCompaniesToTable(stocksTable, Handler.getStockMarket().getCompanies());
            loadCompaniesToTable(watchlistTable, Handler.getTrader().getPortfolio().getWatchlist());
        }

        public void initStockMarketTable() throws SQLException {
            initCompanyTable(
                    stocksTable,
                    nameCol,
                    symbolCol,
                    priceCol,
                    changeCol,
                    yearCol,
                    volumeCol,
                    Handler.getStockMarket().getCompanies());
            stocksTable.setPlaceholder(new Label("No stocks to show!"));
        }
        
        public void initWatchlistTable() throws SQLException, DataCorruptionException {
            initCompanyTable(
                    watchlistTable,
                    watchNameCol,
                    watchSymbolCol,
                    watchPriceCol,
                    watchChangeCol,
                    watchYearCol,
                    watchVolumeCol,
                    Handler.getTrader().getPortfolio().getWatchlist());
            watchlistTable.setPlaceholder(new Label("No stocks in watchlist!"));
        }
        
        public void initCompanyTable(TableView<Company> table,
                                     TableColumn<Company, String> nameCol,
                                     TableColumn<Company, String> symbolCol,
                                     TableColumn<Company, Double> priceCol,
                                     TableColumn<Company, Double> changeCol,
                                     TableColumn<Company, Integer> yearCol,
                                     TableColumn<Company, Integer> volumeCol,
                                     ArrayList<Company> companies){
            table.getItems().clear();
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
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
            changeCol.setCellValueFactory(new PropertyValueFactory<>("change"));
            changeCol.setCellFactory(c -> new TableCell<>() {
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
            yearCol.setCellValueFactory(new PropertyValueFactory<>("IPOYear"));
            volumeCol.setCellValueFactory(new PropertyValueFactory<>("volume"));
            loadCompaniesToTable(table, companies);
        }

        public void loadCompaniesToTable(TableView<Company> table, ArrayList<Company> companies) {
            table.getItems().clear();
            for (Company c : companies) {
                table.getItems().add(c);
            }
        }

        @FXML
        void addToWatch(MouseEvent event) {
            try {
                Company c = stocksTable.getSelectionModel().getSelectedItem();
                Handler.getTrader().getPortfolio().addToWatchlist(c);
                setSuccessMsg(c.getSymbol() + " stock added to watchlist");
                reloadTables();
            } catch (NullPointerException e){
                setErrorMsg("No stocks selected.");
            } catch (SQLException e) {
                ViewProtocols.sqlExceptionError(e);
            } catch (DataCorruptionException e) {
                ViewProtocols.dataCorruptionError(e);
            } catch (CompanyNotFoundException e){
                setErrorMsg("Stock already exists in watchlist");
            }
        }

        @FXML
        void removeFromWatch(MouseEvent event) {
            try {
                Company c = null;
                Company stocksSelected = stocksTable.getSelectionModel().getSelectedItem();
                Company watchSelected = watchlistTable.getSelectionModel().getSelectedItem();
                if (watchSelected != null)
                    c = watchSelected;
                else if (stocksSelected != null)
                    c = stocksSelected;

                Handler.getTrader().getPortfolio().removeFromWatchlist(c);
                setSuccessMsg(c.getSymbol() + " stock removed from watchlist");
                reloadTables();
            } catch (NullPointerException e){
                setErrorMsg("No stocks selected.");
            } catch (SQLException e) {
                ViewProtocols.sqlExceptionError(e);
            } catch (DataCorruptionException e) {
                ViewProtocols.dataCorruptionError(e);
            } catch (CompanyNotFoundException e){
                setErrorMsg("Stock does not exist in watchlist");
            }
        }
    }
