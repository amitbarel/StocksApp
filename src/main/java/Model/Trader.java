package Model;

import Controller.ViewProtocols;
import DBClasses.DBPortfolio;
import exceptions.DataCorruptionException;
import javafx.scene.Node;

import java.sql.SQLException;

public class Trader implements StockMarketObserver {
    private Node mainRoot; //needed for notifying update events
    private int traderID;
    private String username;
    private int portfolioID;

    public Trader(int traderID, String username, int portfolioID) {
        this.traderID = traderID;
        this.username = username;
        this.portfolioID = portfolioID;
    }

    public void setMainRoot(Node node){
        mainRoot = node;
    }

    public int getTraderID() {
        return traderID;
    }

    public String getUsername() {
        return username;
    }

    public int getPortfolioID() {
        return portfolioID;
    }

    public Portfolio getPortfolio() throws SQLException, DataCorruptionException {
        return DBPortfolio.loadPortfolioByID(portfolioID);
    }

    @Override
    public void notifyUpdateStockPriceChange(StockMarketObservable sobs, Company company) {
        try {
            mainRoot.fireEvent(new StockChangeEvent(StockChangeEvent.ANY,company));

            if(DBPortfolio.isCompanyInWatchlist(portfolioID,company.getCompanyID()))
                mainRoot.fireEvent(new StockChangeEvent(StockChangeEvent.WATCHLIST_CHANGE,company));

            if(DBPortfolio.isCompanyInPortfolio(portfolioID,company.getCompanyID()))
                mainRoot.fireEvent(new StockChangeEvent(StockChangeEvent.PORTFOLIO_CHANGE,company));

        } catch (SQLException e) {
            ViewProtocols.sqlExceptionError(e);
        } catch (DataCorruptionException e) {
            ViewProtocols.dataCorruptionError(e);
        }
    }
}
