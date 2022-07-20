package Model;

public class CompanyStock {
    private Company company;
    private int quantity;

    public CompanyStock(Company company, int quantity){
        this.company = company;
        this.quantity = quantity;
    }

    public Company getCompany() {
        return company;
    }

    public String getCompanyName() {
        return company.getName();
    }

    public String getCompanySymbol(){return company.getSymbol();}

    public double getCompanyPrice(){
        return company.getPrice();
    }

    public String getCompanyPriceString() {
        return company.getPriceString();
    }

    public double getCompanyChange(){
        return company.getChange();
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCompanyChangePercentage(){
        return company.getChangePercentage();
    }

    public double getTotalValue(){
        return quantity*company.getPrice();
    }
}
