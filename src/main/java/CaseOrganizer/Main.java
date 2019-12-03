package app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Control;
import javafx.stage.WindowEvent;

import org.apache.commons.configuration.PropertiesConfiguration;

import app.controllers.MainController;
import app.dialogs.Dialogs;
import app.ftpconn.FTPConn;


public class Main extends Application {

    private FTPConn conn;

    @Override
    public void start (Stage stage) {

        try {
            Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
                Dialogs.ExceptionDialog(new Exception(throwable));
                if (conn != null) conn.closeConn();
                System.exit(0);
            });

            if (!Dialogs.LoginDialog()) {
                System.exit(0);
            }

            PropertiesConfiguration config = new PropertiesConfiguration("./config.ini");
            String host = config.getString("host");
            String user = config.getString("user");
            String passwd = config.getString("passwd");

            try {
                conn = new FTPConn(host, user, passwd);
            }
            catch (Exception e) {
                Dialogs.WarningDialog("Nie można było nawiązać połączenia z serwerem", "Dane logowania są nieprawidłowe, lub serwer nie odpowiada", null);
                System.exit(0);
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI.fxml"));
            MainController controller = new MainController(conn);
            loader.setController(controller);

            stage.setOnCloseRequest(new EventHandler<WindowEvent> () {
                @Override
                public void handle (WindowEvent t) {
                    Platform.exit();
                    System.exit(0);
                }
            });

            stage.setTitle("Zarządca postępowań");
            stage.setResizable(false);
            stage.setScene(new Scene(loader.load(), Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE));
            stage.show();
        }

        catch (Exception e) {
            Dialogs.ExceptionDialog(e);
            if (conn != null) conn.closeConn();
            System.exit(0);
        }

    }

}
