package app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;

public class FileViewController {

    @FXML private ListView fileList;

    @FXML private Button addFileButton;

    @FXML private Button downloadFileButton;

    @FXML private Button downloadAllFileButton;

    @FXML private Button deleteFileButton;

    public FileViewController () {
        System.out.println("FileViewController");
    }

}
