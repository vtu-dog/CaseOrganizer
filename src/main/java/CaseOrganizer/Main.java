package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.*;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/GUI.fxml"));

        stage.setTitle("Zarządca postępowań");
        stage.setScene(new Scene(root, 500, 500));
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
