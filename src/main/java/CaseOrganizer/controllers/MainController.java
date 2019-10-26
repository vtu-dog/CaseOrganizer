package app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import app.controllers.SearchButtonController;

public class MainController {

    private String conn = null;

    @FXML private Button searchBox;
    @FXML private SearchBoxController searchBoxController;

    public MainController () {
        System.out.println("MainController");


    }

    public void setFTPConnection (String conn) {
        this.conn = conn;
    }

}
