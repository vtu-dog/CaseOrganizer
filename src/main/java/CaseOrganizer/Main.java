package app.main;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

import it.sauronsoftware.ftp4j.FTPIllegalReplyException;

import app.controllers.MainController;
import app.dialogs.Dialogs;
import app.ftpconn.FTPConn;


public class Main extends Application {

    private FTPConn conn;

    @Override
    public void start (Stage stage) {

        try {
            Configuration config = new PropertiesConfiguration("./config.ini");
            String host = config.getString("host");
            String user = config.getString("user");
            String passwd = config.getString("password");

            FTPConn conn = new FTPConn(host, user, passwd);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI.fxml"));
            MainController controller = new MainController();
            controller.setFTPConnection(conn);

            loader.setController(controller);

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent t) {
                    Platform.exit();
                    System.exit(0);
                }
            });

            stage.setTitle("Zarządca postępowań");
            stage.setResizable(false);
            stage.setScene(new Scene(loader.load(), Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE));
            stage.show();
        }

        catch (IOException e) {
            Dialogs.ErrorDialog("Błąd połączenia z serwerem");
            System.exit(1);
        }

        catch (Exception e) {
            Dialogs.ExceptionDialog(e);
            if (conn != null) conn.closeConn();
            System.exit(1);
        }

    }

}
