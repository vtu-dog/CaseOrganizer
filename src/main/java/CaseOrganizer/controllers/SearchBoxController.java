package app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

public class SearchBoxController {

    @FXML private Button searchButton;
    @FXML private TextField searchBar;
    @FXML private CheckBox priorityCheckBox;
    @FXML private CheckBox archivedCheckBox;
    @FXML private CheckBox activeCheckBox;
    @FXML private CheckBox pendingCheckBox;


    public SearchBoxController () {
        System.out.println("SearchBoxController");
    }

}
