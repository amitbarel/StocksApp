package Controller;

import Model.Company;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class StockChangeController {

    @FXML
    private Label changeLbl;

    @FXML
    private Label priceLbl;

    @FXML
    private Label symbolLbl;

    @FXML
    private HBox changeHBox;

    public HBox init(Company c){
        symbolLbl.setText(c.getSymbol());
        changeLbl.setText(c.getChangePercentage());
        if (c.getChange() > 0)
            changeLbl.setTextFill(Color.GREEN);
        else if (c.getChange() < 0)
            changeLbl.setTextFill(Color.RED);
        priceLbl.setText(c.getPriceString());

        return changeHBox;
    }
}
