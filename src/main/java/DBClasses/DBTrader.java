package DBClasses;

import Model.Portfolio;
import Model.Trader;
import exceptions.DataCorruptionException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBTrader {
    protected static final String TRADER_TABLE = "trader";
    protected static final String COL_TRADER_ID = "trader_id";
    protected static final String COL_USERNAME = "username";
    protected static final String COL_PORTFOLIO_ID = "portfolio_id";

    public static Trader getTraderByUsername(String username) throws SQLException, DataCorruptionException {
        Trader trader;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = DBController.getConn().prepareStatement(
                    "SELECT * FROM " + TRADER_TABLE + " WHERE " + COL_USERNAME + "=?");
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next())
                trader = getTraderFromRs(rs);
            else
                throw new DataCorruptionException();
        } finally {
            DBController.closeQuery(stmt,rs);
        }
        return trader;
    }

    public static Trader getTraderFromRs(ResultSet rs) throws SQLException {
        return new Trader(rs.getInt(COL_TRADER_ID),
                            rs.getString(COL_USERNAME),
                                rs.getInt(COL_PORTFOLIO_ID));
    }

    public static ArrayList<String> getTraderNames() throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<String> result = new ArrayList<>();
        try {
            stmt = DBController.getConn().prepareStatement(
                    "SELECT " + COL_USERNAME + " FROM " + TRADER_TABLE + " ORDER BY " + COL_TRADER_ID);
            rs = stmt.executeQuery();
            while(rs.next())
                result.add(rs.getString(COL_USERNAME));

        } finally {
            DBController.closeQuery(stmt, rs);
        }
        return result;
    }

    public static int createNewTrader(Trader trader) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            DBController.getConn().setAutoCommit(false);
            int portfolioID = DBPortfolio.createNewPortfolio(new Portfolio());
            if(portfolioID==0)
                return 0;

            stmt = DBController.getConn().prepareStatement(
                    "INSERT INTO " + TRADER_TABLE + " " + DBController.colBuilder(COL_USERNAME, COL_PORTFOLIO_ID)
            + " VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, trader.getUsername());
            stmt.setInt(2, portfolioID);
            if(stmt.executeUpdate()>0){
                rs = stmt.getGeneratedKeys();
                if(rs.next())
                    return rs.getInt(1);
            }
            DBController.getConn().commit();
        } catch (SQLException e) {
            e.printStackTrace();
            DBController.getConn().rollback();
        } finally{
            DBController.closeQuery(stmt,rs);
            DBController.getConn().setAutoCommit(true);
        }
        return 0;
    }
}