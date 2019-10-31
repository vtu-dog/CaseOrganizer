package app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import app.controllers.SearchBoxController;
import app.ftpconn.FTPConn;


public class MainController {

    private FTPConn conn = null;

    @FXML private Button searchBox;
    @FXML private SearchBoxController searchBoxController;
    @FXML private CaseViewController caseViewController;
    @FXML private FileViewController fileViewController;


    public MainController () {
        System.out.println("MainController");


    }

    public void setFTPConnection (FTPConn conn) {
        this.conn = conn;
    }

}
