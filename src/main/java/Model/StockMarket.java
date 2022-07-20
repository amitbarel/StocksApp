package Model;

import DBClasses.DBCompany;
import exceptions.CompanyNotFoundException;
import exceptions.InsufficientStockAmountException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

public class StockMarket implements StockMarketObservable {
    private String name;
    private HashSet<StockMarketObserver> traders = new HashSet<>(); //traders observers

    public StockMarket(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getCompanyCount() throws SQLException{
        return DBCompany.getCompanyCount();
    }

    public ArrayList<Company> getCompanies() throws SQLException {
        return DBCompany.loadCompanies();
    }

    public ArrayList<CompanyStock> getCompanyInventory() throws SQLException{
        return DBCompany.loadCompanyInventory();
    }

    public synchronized Company getCompany(int companyID) throws SQLException, CompanyNotFoundException {
        Company c = DBCompany.loadCompanyByID(companyID);
        if(c==null)
            throw new CompanyNotFoundException(companyID);
        return c;
    }

    public synchronized void setStockPrice(int companyID, Double price, Double change) throws CompanyNotFoundException, SQLException {
        DBCompany.updateStockPrice(companyID,price,change);
        notifyAllStockPriceChange(DBCompany.loadCompanyByID(companyID));
    }

    public int getStockInventoryByID(int companyID) throws SQLException, CompanyNotFoundException {
        return DBCompany.getCompanyInventoryCount(companyID);
    }

    public void buyStockInventory(int companyID, int quantity) throws CompanyNotFoundException, SQLException {
        if(getCompany(companyID)==null)
            throw new CompanyNotFoundException(companyID);

        DBCompany.buyCompanyInventory(companyID,quantity);
    }

    public void sellStockInventory(int companyID, int quantity) throws CompanyNotFoundException, InsufficientStockAmountException, SQLException {
        int inventoryCount = getStockInventoryByID(companyID);
        if(inventoryCount==-1)
            throw new CompanyNotFoundException(companyID);

        if(quantity>inventoryCount)
            throw new InsufficientStockAmountException(getName());

        DBCompany.sellCompanyInventory(companyID, quantity);
    }

    @Override
    public void addStockMarketObserver(StockMarketObserver so) {
        traders.add(so);
    }

    @Override
    public void removeStockMarketObserver(StockMarketObserver so) {
        traders.remove(so);
    }

    @Override
    public void notifyAllStockPriceChange(Company company) {
        for(StockMarketObserver smo : traders){
            smo.notifyUpdateStockPriceChange(this,company);
        }
    }
}
