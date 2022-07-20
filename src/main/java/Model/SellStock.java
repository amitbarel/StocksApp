package Model;

import DBClasses.DBController;
import exceptions.CompanyNotFoundException;
import exceptions.DataCorruptionException;
import exceptions.InsufficientStockAmountException;

import java.sql.SQLException;

public class SellStock implements Order {
    private int companyID;
    private int quantity;

    public SellStock(int companyID, int quantity) {
        this.companyID = companyID;
        this.quantity = quantity;
    }

    @Override
    public void execute(Trader trader, StockMarket stockMarket) throws NullPointerException, InsufficientStockAmountException, SQLException, DataCorruptionException, CompanyNotFoundException {
        try {
            DBController.getConn().setAutoCommit(false);
            stockMarket.buyStockInventory(companyID,quantity);
            trader.getPortfolio().sellStock(stockMarket.getCompany(companyID),quantity);
            DBController.getConn().commit();
        } catch (Exception e) {
            DBController.getConn().rollback();
            throw e;
        } finally {
            DBController.getConn().setAutoCommit(true);
        }
    }
}
