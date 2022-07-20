package Model;

import DBClasses.DBPortfolio;
import DBClasses.DBTransaction;
import exceptions.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class Portfolio {
    private int portfolioID;
    private Double balance; //balance in USD

    public Portfolio(int portfolioID, Double balance) throws DataCorruptionException {
        if(balance<0)
            throw new DataCorruptionException();
        this.portfolioID = portfolioID;
        this.balance = balance;
    }

    public Portfolio(){
        this.portfolioID = 0;
        this.balance = 0.0;
    }

    public int getPortfolioID() {
        return portfolioID;
    }

    public Double getBalance() {
        return balance;
    }

    public String getBalanceString(){
        return String.format("%.2f",getBalance())+"$";
    }

    public void addFunds(Double amount) throws SQLException {
        DBPortfolio.changeBalance(portfolioID,amount);
    }

    public void removeFunds(Double amount) throws SQLException {
        DBPortfolio.changeBalance(portfolioID,amount*-1);
    }

    public void buyStock(Company company, int quantity) throws InsufficientFundsException, SQLException {
        double transactionPrice = company.getPrice()*quantity;
        if(transactionPrice>balance)
            throw new InsufficientFundsException();

        DBPortfolio.addStockToPortfolio(portfolioID, company.getCompanyID(), quantity);
        removeFunds(transactionPrice);
        addTransaction(eTransaction.BuyStock, company, quantity, company.getPrice());
    }

    public void sellStock(Company company, int quantity) throws InsufficientStockAmountException, SQLException, DataCorruptionException {
        if(quantity>DBPortfolio.getStockCountPortfolio(portfolioID,company.getCompanyID()))
            throw new InsufficientStockAmountException("Portfolio");

        double transactionPrice = company.getPrice()*quantity;
        DBPortfolio.removeStockFromPortfolio(portfolioID,company.getCompanyID(), quantity);
        addFunds(transactionPrice);
        addTransaction(eTransaction.SellStock, company, quantity, company.getPrice());
    }

    public ArrayList<CompanyStock> getPortfolioStocks() throws SQLException {
        return DBPortfolio.getPortfolioStocks(portfolioID);
    }

    public void addTransaction(eTransaction type, Company company, int quantity, double stockPrice) throws SQLException {
        DBTransaction.addTransaction(portfolioID,
                new StockTransaction(
                        type,
                        company,
                        quantity,
                        stockPrice,
                        new Date()));
    }

    public ArrayList<StockTransaction> getTransactions() throws SQLException, DataCorruptionException {
        return DBTransaction.loadTransactions(portfolioID);
    }

    public ArrayList<Company> getWatchlist() throws SQLException {
        return DBPortfolio.loadWatchlist(portfolioID);
    }

    public ArrayList<CompanyStock> getWatchlistInventory() throws SQLException {
        return DBPortfolio.loadWatchlistInventory(portfolioID);
    }

    public void addToWatchlist(Company c) throws NullPointerException, SQLException, DataCorruptionException, CompanyNotFoundException {
        Protocols.nullPointerCheck(this,c,"Company");
        if(getWatchlist().contains(c))
            throw new CompanyNotFoundException(c.getCompanyID());
        DBPortfolio.addToWatchlist(portfolioID, c);
    }

    public void removeFromWatchlist(Company c) throws NullPointerException, SQLException, DataCorruptionException, CompanyNotFoundException {
        Protocols.nullPointerCheck(this,c,"Company");
        if(!getWatchlist().contains(c))
            throw new CompanyNotFoundException(c.getCompanyID());
        DBPortfolio.removeFromWatchlist(portfolioID,c);
    }
}