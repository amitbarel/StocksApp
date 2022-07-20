package DBClasses;

import Model.Company;
import Model.CompanyStock;
import exceptions.CompanyNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBCompany {
    protected static final String COMPANY_TABLE = "company";
    protected static final String COL_ID = "company_id";
    protected static final String COL_SYMBOL = "symbol";
    protected static final String COL_NAME = "company_name";
    protected static final String COL_PRICE = "price";
    protected static final String COL_CHANGE = "price_change";
    protected static final String COL_IPO = "IPO_year";
    protected static final String COL_VOLUME = "volume";
    protected static final String COL_INVENTORY = "stock_inventory";
    protected static final String COL_INDUSTRY = "industry";

    protected static final String INVENTORY_TABLE = "stock_market_inventory";
    protected static final String COL_QUANTITY = "quantity";

    public static int getCompanyCount() throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            stmt = DBController.getConn().prepareStatement("SELECT COUNT(*) FROM " + COMPANY_TABLE);
            rs = stmt.executeQuery();
            if(rs.next())
                return rs.getInt(1);
            else
                return 0;
        } finally {
            DBController.closeQuery(stmt, rs);
        }
    }

    public static ArrayList<Company> loadCompanies() throws SQLException {
        ArrayList<Company> companies = new ArrayList<>();
        PreparedStatement pst = null;
        ResultSet rs = null;
        try{
            pst = DBController.getConn().prepareStatement("SELECT * FROM " + COMPANY_TABLE);
            rs = pst.executeQuery();
            while(rs.next()){
                companies.add(getCompanyFromRs(rs));
            }
        }
        finally {
            DBController.closeQuery(pst,rs);
        }
        return companies;
    }

    public static ArrayList<CompanyStock> loadCompanyInventory() throws SQLException{
        ArrayList<CompanyStock> companyStock = new ArrayList<>();
        PreparedStatement pst = null;
        ResultSet rs = null;
        try{
            pst = DBController.getConn().prepareStatement(
                    "SELECT * FROM " + COMPANY_TABLE + " NATURAL JOIN " + INVENTORY_TABLE);
            rs = pst.executeQuery();
            while(rs.next()){
                companyStock.add(getCompanyStockFromRs(rs));
            }
        }
        finally {
            DBController.closeQuery(pst,rs);
        }
        return companyStock;
    }

    public static Company loadCompanyByID(int companyID) throws SQLException, CompanyNotFoundException {
        Company company;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try{
            pst = DBController.getConn().prepareStatement(
                    "SELECT * FROM " + COMPANY_TABLE + " WHERE " + COL_ID + "=?");
            pst.setInt(1,companyID);
            rs = pst.executeQuery();
            if(rs.next())
                company = getCompanyFromRs(rs);
            else
                throw new CompanyNotFoundException(companyID);
        }
        finally{
            DBController.closeQuery(pst,rs);
        }
        return company;
    }

    public static Company getCompanyFromRs(ResultSet rs) throws SQLException {
        return new Company(
                rs.getInt(COL_ID),
                rs.getString(COL_SYMBOL),
                rs.getString(COL_NAME),
                rs.getDouble(COL_PRICE),
                rs.getDouble(COL_CHANGE),
                rs.getInt(COL_IPO),
                rs.getLong(COL_VOLUME),
                rs.getString(COL_INDUSTRY),
                rs.getLong(COL_INVENTORY));
    }

    public static CompanyStock getCompanyStockFromRs(ResultSet rs) throws SQLException {
        return new CompanyStock(getCompanyFromRs(rs),rs.getInt(COL_QUANTITY));
    }

    public static int getCompanyInventoryCount(int companyID) throws SQLException, CompanyNotFoundException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            stmt = DBController.getConn().prepareStatement(
                    "SELECT " + COL_QUANTITY + " FROM " + INVENTORY_TABLE +
                            " WHERE " + COL_ID +"=?");
            stmt.setInt(1,companyID);
            rs = stmt.executeQuery();
            if(rs.next())
                return rs.getInt(COL_QUANTITY);
            else
                throw new CompanyNotFoundException(companyID);
        } finally{
            DBController.closeQuery(stmt,rs);
        }
    }

    public static void sellCompanyInventory(int companyID, int quantity) throws SQLException, CompanyNotFoundException {
        PreparedStatement stmt = null;
        try{
            stmt = DBController.getConn().prepareStatement(
                    "UPDATE " + INVENTORY_TABLE + " SET " + COL_QUANTITY + "=" + COL_QUANTITY + "-?" +
                    " WHERE " + COL_ID + "=?");
            stmt.setInt(1, quantity);
            stmt.setInt(2, companyID);
            if(stmt.executeUpdate()==0)
                throw new CompanyNotFoundException(companyID);
            addVolumeTraded(companyID,quantity);
        } finally{
            DBController.closeQuery(stmt,null);
        }
    }

    public static void buyCompanyInventory(int companyID, int quantity) throws SQLException, CompanyNotFoundException {
        PreparedStatement stmt = null;
        try{
            stmt = DBController.getConn().prepareStatement(
                    "UPDATE " + INVENTORY_TABLE + " SET " + COL_QUANTITY + "=" + COL_QUANTITY + "+?" +
                            " WHERE " + COL_ID + "=?");
            stmt.setInt(1, quantity);
            stmt.setInt(2, companyID);
            if(stmt.executeUpdate()==0)
                throw new CompanyNotFoundException(companyID);
            addVolumeTraded(companyID, quantity);

        } finally{
            DBController.closeQuery(stmt,null);
        }
    }

    private static void addVolumeTraded(int companyID, int volumeTraded) throws SQLException, CompanyNotFoundException {
        PreparedStatement stmt = null;
        try{
            stmt = DBController.getConn().prepareStatement(
                    "UPDATE " + COMPANY_TABLE + " SET " + COL_VOLUME + "=" + COL_VOLUME + "+?" +
                            " WHERE " + COL_ID + "=?");
            stmt.setInt(1, volumeTraded);
            stmt.setInt(2, companyID);
            if(stmt.executeUpdate()==0)
                throw new CompanyNotFoundException(companyID);

        } finally{
            DBController.closeQuery(stmt,null);
        }
    }

    public static void updateStockPrice(int companyID, double newPrice, double priceChange) throws SQLException, CompanyNotFoundException {
        PreparedStatement stmt = null;
        try{
            stmt = DBController.getConn().prepareStatement(
                    "UPDATE " + COMPANY_TABLE + " SET " + COL_PRICE + "=?, " +
                    COL_CHANGE + "=? WHERE " + COL_ID + "=?");
            stmt.setDouble(1,newPrice);
            stmt.setDouble(2,priceChange);
            stmt.setInt(3,companyID);
            if(stmt.executeUpdate()==0)
                throw new CompanyNotFoundException(companyID);
        } finally{
            DBController.closeQuery(stmt,null);
        }
    }

}
