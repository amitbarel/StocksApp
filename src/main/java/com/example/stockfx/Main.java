package com.example.stockfx;

import Controller.OpeningController;
import Controller.ViewProtocols;
import DBClasses.DBController;
import Model.Handler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public void start(Stage stage) throws IOException {
        stage.setOnHidden(arg0 -> {
            DBController.closeConnection();
            Handler.shutdownExecutor();
        });

        DBController.getConn();

        if(!DBController.checkConnection()) {
            ViewProtocols.systemCloseError("Connection could not be established!\n");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("OpeningScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 750, 500);
        OpeningController opc  = fxmlLoader.getController();
        stage.setTitle("Welcome!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}