package Controller;

import Model.CompanyStock;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class StockDisplayController {

    @FXML
    private Label changeLabel;

    @FXML
    private HBox stockHBox;

    @FXML
    private Label nameLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label quanLabel;

    @FXML
    private Label symbolLabel;

    @FXML
    private Label valueLabel;

    public HBox initialize(CompanyStock ts){
        symbolLabel.setText(ts.getCompanySymbol());
        nameLabel.setText(ts.getCompanyName());
        priceLabel.setText(ts.getCompanyPriceString());
        changeLabel.setText(ts.getCompanyChangePercentage());
        quanLabel.setText(String.valueOf(ts.getQuantity()));
        valueLabel.setText(String.format("%,.2f", ts.getTotalValue()) + " $");
        return stockHBox;
    }
}
