package Model;

public enum eTransaction {
    BuyStock, SellStock;

    public static int getETransactionValue(eTransaction t){
        if(t == BuyStock)
            return 1;
        else if(t == SellStock)
            return 2;
        else
            return 0;
    }
}
