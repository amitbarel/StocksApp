package Model;

import javafx.application.Platform;

import java.util.Random;

public class StockPriceChanger implements Runnable{
    private final static double MIN_PERCENT_CHANGE = -3.0;
    private final static double MAX_PERCENT_CHANGE = 3.0;
    private int companyCount;

    public StockPriceChanger(int companyCount){
        this.companyCount = companyCount;
    }

    @Override
    public void run() {
        Platform.runLater(()->{
            try{
                Random rand = new Random(); //instance of random class
                int companyChosen = rand.nextInt(companyCount)+1;
                Company c = Handler.getStockMarket().getCompany(companyChosen);
                double percentChange = (MIN_PERCENT_CHANGE + (MAX_PERCENT_CHANGE - MIN_PERCENT_CHANGE) * rand.nextDouble())*0.01;
                double priceChange = c.getPrice() + c.getPrice()*percentChange;
                Handler.getStockMarket().setStockPrice(companyChosen,priceChange,percentChange);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
