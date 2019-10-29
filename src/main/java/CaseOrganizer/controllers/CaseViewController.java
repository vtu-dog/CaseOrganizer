package app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Button;

public class CaseViewController {

    @FXML private ListView caseList;

    @FXML private Button addCaseButton;

    @FXML private Button archiveCaseButton;

    @FXML private ToggleButton editCaseToggleButton;

    @FXML private Button linkCaseButton;

    @FXML private Button deleteCaseButton;

    public CaseViewController () {
        System.out.println("CaseViewController");
    }

}
