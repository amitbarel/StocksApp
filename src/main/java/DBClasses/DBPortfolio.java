package DBClasses;

import Model.Company;
import Model.Portfolio;
import Model.CompanyStock;
import exceptions.DataCorruptionException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBPortfolio {
    private static final String PORTFOLIO_TABLE = "portfolio";
    private static final String COL_PORTFOLIO_ID = "portfolio_id";
    private static final String COL_BALANCE = "balance";

    private static final String PORTFOLIO_STOCKS_TABLE = "stock_portfolio";
    private static final String COL_COMPANY_ID = "company_id";
    private static final String COL_QUANTITY = "quantity";

    private static final String WATCHLIST_TABLE = "stock_watchlist";

    public static Portfolio loadPortfolioByID(int portfolioID) throws SQLException, DataCorruptionException {
        Portfolio prt = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            stmt = DBController.getConn().prepareStatement(
                    "SELECT * FROM " + PORTFOLIO_TABLE + " WHERE " + COL_PORTFOLIO_ID +" = ?");
            stmt.setInt(1,portfolioID);
            rs = stmt.executeQuery();
            if(rs.next()){
                prt = new Portfolio(portfolioID,rs.getDouble(COL_BALANCE));
            }
        }
        finally {
            DBController.closeQuery(stmt,rs);
        }
        return prt;
    }


    public static int createNewPortfolio(Portfolio portfolio) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = DBController.getConn().prepareStatement(
                    "INSERT INTO " + PORTFOLIO_TABLE + " " + DBController.colBuilder(COL_BALANCE) + " VALUES (?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setDouble(1,portfolio.getBalance());
            if(stmt.executeUpdate()>0){
                rs = stmt.getGeneratedKeys();
                if(rs.next())
                    return rs.getInt(1);
            }
        } finally {
            DBController.closeQuery(stmt, rs);
        }
        return 0;
    }

    public static ArrayList<CompanyStock> getPortfolioStocks(int portfolioID) throws SQLException {
        ArrayList<CompanyStock> stocks = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            stmt = DBController.getConn().prepareStatement(
                    "SELECT * FROM " + DBCompany.COMPANY_TABLE + " NATURAL JOIN "
                            + PORTFOLIO_STOCKS_TABLE + " WHERE " + COL_PORTFOLIO_ID + " = ?");
            stmt.setInt(1, portfolioID);
            rs = stmt.executeQuery();
            while(rs.next()){
                stocks.add(new CompanyStock(DBCompany.getCompanyFromRs(rs),rs.getInt(COL_QUANTITY)));
            }
        } finally{
            DBController.closeQuery(stmt,rs);
        }
        return stocks;
    }

    public static int getStockCountPortfolio(int portfolioID, int companyID) throws SQLException, DataCorruptionException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            stmt = DBController.getConn().prepareStatement(
                    "SELECT " + COL_QUANTITY + " FROM " + PORTFOLIO_STOCKS_TABLE +
                    " WHERE " + COL_COMPANY_ID + " = ? AND " +
                    COL_PORTFOLIO_ID + " = ?");
            stmt.setInt(1,companyID);
            stmt.setInt(2,portfolioID);
            rs = stmt.executeQuery();
            if(rs.next())
                return rs.getInt(COL_QUANTITY);
            else
                throw new DataCorruptionException();
        } finally{
            DBController.closeQuery(stmt, rs);
        }
    }

    public static void addToWatchlist(int portfolioID, Company c) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = DBController.getConn().prepareStatement(
                    "INSERT INTO " + WATCHLIST_TABLE  + " VALUES (?,?) ");
            stmt.setInt(1,portfolioID);
            stmt.setInt(2,c.getCompanyID());
            stmt.executeUpdate();
        }
        finally {
            DBController.closeQuery(stmt, null);
        }
    }

    public static void removeFromWatchlist(int portfolioID, Company c) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = DBController.getConn().prepareStatement(
                    "DELETE FROM " + WATCHLIST_TABLE  + " WHERE " + DBCompany.COL_ID + " =? AND " + COL_PORTFOLIO_ID + " =?");
            stmt.setInt(1,c.getCompanyID());
            stmt.setInt(2,portfolioID);
            stmt.executeUpdate();
        }
        finally {
            DBController.closeQuery(stmt, null);
        }
    }

    public static ArrayList<Company> loadWatchlist(int portfolioID) throws SQLException {
        ArrayList<Company> watchlist = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = DBController.getConn().prepareStatement(
                    "SELECT * FROM " + WATCHLIST_TABLE + " NATURAL JOIN " + DBCompany.COMPANY_TABLE
                            + " WHERE " + COL_PORTFOLIO_ID + " = ?");
            stmt.setInt(1,portfolioID);
            rs = stmt.executeQuery();
            while(rs.next())
                watchlist.add(DBCompany.getCompanyFromRs(rs));
            }
         finally {
            DBController.closeQuery(stmt, rs);
        }

        return watchlist;
    }

    public static ArrayList<CompanyStock> loadWatchlistInventory(int portfolioID) throws SQLException {
        ArrayList<CompanyStock> watchlist = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = DBController.getConn().prepareStatement(
                    "SELECT * FROM " + WATCHLIST_TABLE + " NATURAL JOIN " + DBCompany.COMPANY_TABLE +
                            " NATURAL JOIN " + DBCompany.INVENTORY_TABLE +
                            " WHERE " + COL_PORTFOLIO_ID + " = ?");
            stmt.setInt(1,portfolioID);
            rs = stmt.executeQuery();
            while(rs.next())
                watchlist.add(DBCompany.getCompanyStockFromRs(rs));
        }
        finally {
            DBController.closeQuery(stmt, rs);
        }

        return watchlist;
    }

    public static boolean isCompanyInPortfolio(int portfolioID,int companyID) throws SQLException, DataCorruptionException {
        PreparedStatement stmt=null;
        ResultSet rs = null;
        try{
            stmt = DBController.getConn().prepareStatement(
                    "SELECT * FROM " + PORTFOLIO_STOCKS_TABLE +
                            " WHERE " + COL_PORTFOLIO_ID + "=? AND " +
                            COL_COMPANY_ID + "=?");
            stmt.setInt(1, portfolioID);
            stmt.setInt(2, companyID);
            rs = stmt.executeQuery();
            return rs.next();
        } finally{
            DBController.closeQuery(stmt, rs);
        }
    }


    public static boolean isCompanyInWatchlist(int portfolioID,int companyID) throws SQLException, DataCorruptionException {
        PreparedStatement stmt=null;
        ResultSet rs = null;
        try{
            stmt = DBController.getConn().prepareStatement(
                    "SELECT * FROM " + WATCHLIST_TABLE +
                            " WHERE " + COL_PORTFOLIO_ID + "=? AND " +
                    COL_COMPANY_ID + "=?");
            stmt.setInt(1, portfolioID);
            stmt.setInt(2, companyID);
            rs = stmt.executeQuery();
            return rs.next();
        } finally{
            DBController.closeQuery(stmt, rs);
        }
    }

    public static void addStockToPortfolio(int portfolioID,int companyID, int quantity) throws  SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            rs = getStockPortfolioRow(stmt, portfolioID, companyID);
            if(rs.next())
                updateStockPortfolio(portfolioID, companyID, quantity);
            else
                addNewStockToPortfolio(portfolioID, companyID, quantity);
        } finally {
            DBController.closeQuery(stmt, rs);
        }
    }

    private static ResultSet getStockPortfolioRow(PreparedStatement stmt, int portfolioID, int companyID) throws SQLException {
        stmt = DBController.getConn().prepareStatement(
                "SELECT * FROM " + PORTFOLIO_STOCKS_TABLE +
                        " WHERE " + COL_PORTFOLIO_ID + " = ? AND " + COL_COMPANY_ID + " = ?");
        stmt.setInt(1, portfolioID);
        stmt.setInt(2, companyID);
        return stmt.executeQuery();
    }

    private static void addNewStockToPortfolio(int portfolioID, int companyID, int quantity) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = DBController.getConn().prepareStatement(
                    "INSERT INTO " + PORTFOLIO_STOCKS_TABLE  + " VALUES (?,?,?) ");
            stmt.setInt(1,portfolioID);
            stmt.setInt(2,companyID);
            stmt.setInt(3,quantity);
            stmt.executeUpdate();
        }
        finally {
            DBController.closeQuery(stmt, null);
        }
    }

    private static void updateStockPortfolio(int portfolioID, int companyID, int quantity) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = DBController.getConn().prepareStatement(
                    "UPDATE " + PORTFOLIO_STOCKS_TABLE  +
                            " SET " + COL_QUANTITY + "=" + COL_QUANTITY + "+?" +
                            " WHERE " + COL_PORTFOLIO_ID + "=?" +
                            " AND " + COL_COMPANY_ID + "=?");
            stmt.setInt(1,quantity);
            stmt.setInt(2,portfolioID);
            stmt.setInt(3,companyID);
            stmt.executeUpdate();
        }
        finally {
            DBController.closeQuery(stmt, null);
        }
    }

    public static void removeStockFromPortfolio(int portfolioID, int companyID, int quantity) throws SQLException, DataCorruptionException{
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            rs = getStockPortfolioRow(stmt, portfolioID, companyID);
            if(rs.next()) {
                if (rs.getInt(COL_QUANTITY) == quantity)
                    deleteStockPortfolio(portfolioID, companyID);
                else
                    updateStockPortfolio(portfolioID, companyID, quantity * -1);
            }
            else
                throw new DataCorruptionException();
        }
        finally {
            DBController.closeQuery(stmt, rs);
        }
    }

    private static void deleteStockPortfolio(int portfolioID, int companyID) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = DBController.getConn().prepareStatement(
                    "DELETE FROM " + PORTFOLIO_STOCKS_TABLE +
                            " WHERE " + COL_PORTFOLIO_ID + "=? AND " +
                            COL_COMPANY_ID + "= ?");
            stmt.setInt(1,portfolioID);
            stmt.setInt(2,companyID);
            stmt.executeUpdate();
        } finally{
            DBController.closeQuery(stmt, null);
        }
    }

    public static void changeBalance(int portfolioID, double transactionChange) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = DBController.getConn().prepareStatement(
                    "UPDATE " + PORTFOLIO_TABLE  + " SET " + COL_BALANCE + " = " + COL_BALANCE + "+?" + "WHERE " + COL_PORTFOLIO_ID + " = ?");
            stmt.setDouble(1,transactionChange);
            stmt.setInt(2,portfolioID);
            stmt.executeUpdate();
        }
        finally {
            DBController.closeQuery(stmt, null);
        }
    }
}

