package Model;

public interface StockMarketObserver {
    void notifyUpdateStockPriceChange(StockMarketObservable sobs, Company company);
}
