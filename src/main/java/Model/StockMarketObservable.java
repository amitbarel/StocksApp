package Model;

public interface StockMarketObservable {
    void addStockMarketObserver(StockMarketObserver so);
    void removeStockMarketObserver(StockMarketObserver so);
    void notifyAllStockPriceChange(Company company);
}
