package Model;

public class Company {
    private int id;
    private String symbol;
    private String name;
    private Double price; //average price for a stock
    private Double change;
    private int IPOYear;
    private long volume; //number of stocks traded
    private String industry;
    private long stockInventory; //counts how many stocks available for purchase initially

    public Company(String symbol, String name, Double price, int IPOYear, String industry, int stockInventory) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.change = 0.0;
        this.IPOYear = IPOYear;
        this.volume = 0;
        this.industry = industry;
        this.stockInventory = stockInventory;
    }

    public Company(int id, String symbol, String name, Double price, Double change, int IPOYear,long volume, String industry, long stockInventory) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.change = change;
        this.IPOYear = IPOYear;
        this.volume = volume;
        this.industry = industry;
        this.stockInventory = stockInventory;
    }

    public String getSymbol() {
        return symbol;
    }

    public Double getPrice() {
        return price;
    }

    public String getPriceString(){
        return String.format("%.2f",price)+"$";
    }

    public Double getChange() {
        return change;
    }

    public String getChangePercentage(){
        return String.format("%.2f",change*100) + "%";
    }

    public String getName() {
        return name;
    }

    public int getIPOYear() {
        return IPOYear;
    }

    public long getVolume() {
        return volume;
    }

    public String getIndustry() {
        return industry;
    }

    public long getStockInventory() {
        return stockInventory;
    }

    public int getCompanyID() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof  Company))
            return false;
        return symbol.equals(((Company) obj).getSymbol());
    }
}
