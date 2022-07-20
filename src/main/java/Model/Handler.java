package Model;

import Controller.ViewProtocols;
import DBClasses.DBTrader;
import exceptions.DataCorruptionException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Handler {
    private static final String SM_NAME = "NYSE";
    private static final int PRICE_CHANGE_FREQ = 10; //frequency of price changes in seconds

    private static ScheduledExecutorService executor = null;
    private static StockMarket stockMarket;
    private static Broker broker;
    private static Trader trader;

    public static void initHandler(Trader t) {
        trader = t;
        stockMarket = new StockMarket(SM_NAME);
        stockMarket.addStockMarketObserver(trader);
        broker = new Broker(t, stockMarket);
    }

    public static StockMarket getStockMarket() {
        return stockMarket;
    }

    public static Broker getBroker() {
        return broker;
    }

    public static Trader getTrader() {
        return trader;
    }

    public static void initStockPriceChangeThread() {
        try {
            executor = Executors.newSingleThreadScheduledExecutor();
            Runnable StockPriceChanger = new StockPriceChanger(stockMarket.getCompanyCount());
            executor.scheduleAtFixedRate(StockPriceChanger::run, 5, PRICE_CHANGE_FREQ, TimeUnit.SECONDS);
        } catch (SQLException e) {
            ViewProtocols.sqlExceptionError(e);
        }
    }

    public static ArrayList<String> getTraderNames() throws SQLException {
        return DBTrader.getTraderNames();
    }

    public static int saveNewTraderToDB(Trader trader) throws SQLException {
        return DBTrader.createNewTrader(trader);
    }

    public static Trader getTraderByUsername(String username) throws SQLException, DataCorruptionException {
        return DBTrader.getTraderByUsername(username);
    }

    public static void shutdownExecutor() {
        if (executor != null)
            executor.shutdownNow();
    }

}
