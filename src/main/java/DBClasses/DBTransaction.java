package DBClasses;

import Model.Company;
import Model.StockTransaction;
import Model.eTransaction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class DBTransaction {
    private static final String TRANSACTION_TABLE = "stock_transaction";
    private static final String COL_PORTFOLIO_ID = "portfolio_id";
    private static final String COL_COMPANY_ID = "company_id";
    private static final String COL_TYPE_ID = "transaction_type_id";
    private static final String COL_QUANTITY = "quantity";
    private static final String COL_PRICE = "price";
    private static final String COL_DATE = "transaction_date";

    public static ArrayList<StockTransaction> loadTransactions(int portfolioID) throws SQLException {
        ArrayList<StockTransaction> transactions = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            stmt = DBController.getConn().prepareStatement(
                    "SELECT * FROM " + TRANSACTION_TABLE + " INNER JOIN " + DBCompany.COMPANY_TABLE +
                            " ON " + TRANSACTION_TABLE + "." + COL_COMPANY_ID + "=" + DBCompany.COMPANY_TABLE + "." + COL_COMPANY_ID +
                            " WHERE " + COL_PORTFOLIO_ID + "=? " +
                            " ORDER BY " + COL_DATE + " DESC");
            stmt.setInt(1, portfolioID);
            rs = stmt.executeQuery();
            while(rs.next()){
                Company c = DBCompany.getCompanyFromRs(rs);
                int type = rs.getInt(COL_TYPE_ID);
                int quantity = rs.getInt(COL_QUANTITY);
                Date date = rs.getDate(COL_DATE);
                double total = rs.getInt(COL_PRICE) * quantity;
                transactions.add(new StockTransaction(type,c,quantity,total,date));
            }
        } finally{
            DBController.closeQuery(stmt,rs);
        }

        return transactions;

    }

    public static void addTransaction(int portfolioID, StockTransaction st) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = DBController.getConn().prepareStatement(
                    "INSERT INTO " + TRANSACTION_TABLE  + " VALUES (?,?,?,?,?,?) ");
            stmt.setInt(1,portfolioID);
            stmt.setInt(2,st.getCompany().getCompanyID());
            stmt.setInt(3, eTransaction.getETransactionValue(st.getType()));
            stmt.setInt(4,st.getQuantity());
            stmt.setDouble(5,st.getPrice());
            stmt.setString(6, LocalDateTime.now().toString());
            stmt.executeUpdate();
        }
        finally {
            DBController.closeQuery(stmt, null);
        }
    }

}
