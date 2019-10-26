package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.*;
import app.controllers.*;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI.fxml"));
        MainController controller = new MainController();
        loader.setController(controller);

        stage.setTitle("Zarządca postępowań");
        stage.setScene(new Scene(loader.load(), 500, 500));
        stage.show();

        /*
        // connection example
        try {
            FTPConn conn = new FTPConn("localhost", "admin", "admin");
            conn.createCase("123abcaaa");
            conn.closeConn();
        } catch (Exception e) { System.out.println(e); }
        */
    }
}
