package Model;

import java.util.Date;

public class StockTransaction {
    private eTransaction type;
    private Company company;
    private int quantity;
    private Double transactionPrice;
    private Date date;

    public StockTransaction(eTransaction type, Company company, int quantity, Double transactionPrice, Date date) {
        this.type = type;
        this.company = company;
        this.quantity = quantity;
        this.transactionPrice = transactionPrice;
        this.date = date;
    }

    public StockTransaction(int type, Company company, int quantity, Double transactionPrice, Date date) {
        this.type = eTransaction.values()[type-1];
        this.company = company;
        this.quantity = quantity;
        this.transactionPrice = transactionPrice;
        this.date = date;
    }

    public eTransaction getType() {
        return type;
    }

    public Company getCompany() {
        return company;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice(){
       return company.getPrice();
    }

    public String getSymbol(){
        return company.getSymbol();
    }

    public Double getTransactionTotal() {
        return company.getPrice() * quantity;
    }

    public Date getDate() {
        return date;
    }
}
