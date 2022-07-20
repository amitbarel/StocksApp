package Model;

import java.util.ArrayList;

public class Broker {
    private ArrayList<Order> orderList = new ArrayList<>();
    private Trader trader;
    private StockMarket stockMarket;

    public Broker(Trader trader, StockMarket stockMarket){
        this.trader = trader;
        this.stockMarket = stockMarket;
    }

    public void takeOrder(Order order) {
        orderList.add(order);
    }

    public void placeOrders() throws Exception {
        try {
            for (Order order : orderList) {
                order.execute(trader, stockMarket);
            }
        }
        finally {
            orderList.clear();
        }
    }
}
