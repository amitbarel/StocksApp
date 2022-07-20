package Model;

import DBClasses.DBController;
import exceptions.CompanyNotFoundException;
import exceptions.DataCorruptionException;
import exceptions.InsufficientFundsException;
import exceptions.InsufficientStockAmountException;

import java.sql.SQLException;

public class BuyStock implements Order {
    private int companyID;
    private int quantity;

    public BuyStock(int companyID, int quantity) {
        this.companyID = companyID;
        this.quantity = quantity;
    }

    @Override
    public void execute(Trader trader, StockMarket stockMarket) throws NullPointerException, InsufficientFundsException, InsufficientStockAmountException, CompanyNotFoundException, SQLException, DataCorruptionException {
        try {
            DBController.getConn().setAutoCommit(false);
            stockMarket.sellStockInventory(companyID,quantity);
            trader.getPortfolio().buyStock(stockMarket.getCompany(companyID),quantity);
            DBController.getConn().commit();
        } catch (Exception e) {
            DBController.getConn().rollback();
            throw e;
        } finally {
            DBController.getConn().setAutoCommit(true);
        }
    }
}
