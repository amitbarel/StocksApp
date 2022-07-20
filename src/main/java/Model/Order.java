package Model;

public interface Order {
   void execute(Trader trader, StockMarket stockMarket) throws Exception;
}
