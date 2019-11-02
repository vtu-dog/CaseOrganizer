package app.controllers;

import javafx.fxml.FXML;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import app.dialogs.Dialogs;
import app.ftpconn.FTPConn;
import app.xml.BasicCase;


public class MainController {

    private FTPConn conn = null;
    private ObservableList<BasicCase> cases = FXCollections.observableArrayList();
    private ObservableList<BasicCase> files = FXCollections.observableArrayList();

    @FXML private Button searchButton;
    @FXML private TextField searchBar;

    @FXML private CheckBox priorityCheckBox;
    @FXML private CheckBox archivedCheckBox;
    @FXML private CheckBox activeCheckBox;
    @FXML private CheckBox pendingCheckBox;

    @FXML private ListView<BasicCase> caseList;

    @FXML private Button deleteCaseButton;
    @FXML private Button linkCaseButton;
    @FXML private Button showCaseLinksButton;

    public MainController (FTPConn conn) {
        this.conn = conn;
    }

    @FXML
    public void initialize () {
        deleteCaseButton.setDisable(true);
        linkCaseButton.setDisable(true);
        showCaseLinksButton.setDisable(true);

        caseList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BasicCase>() {
            @Override
            public void changed(ObservableValue<? extends BasicCase> observable, BasicCase oldValue, BasicCase newValue) {
                if (newValue == null) {
                    deleteCaseButton.setDisable(true);
                    linkCaseButton.setDisable(true);
                    showCaseLinksButton.setDisable(true);
                } else {
                    deleteCaseButton.setDisable(false);
                    linkCaseButton.setDisable(false);
                    showCaseLinksButton.setDisable(false);
                }
            }
        });
    }


    @FXML
    public void setIsActive () {
        if (activeCheckBox.isSelected())
            archivedCheckBox.setSelected(false);
        else {
            archivedCheckBox.setSelected(true);
            priorityCheckBox.setSelected(false);
            pendingCheckBox.setSelected(false);
        }
    }

    @FXML
    public void setIsPriority () {
        if (priorityCheckBox.isSelected()) {
            archivedCheckBox.setSelected(false);
            activeCheckBox.setSelected(true);
            pendingCheckBox.setSelected(false);
        }
    }

    @FXML
    public void setIsPending () {
        if (pendingCheckBox.isSelected()) {
            activeCheckBox.setSelected(true);
            priorityCheckBox.setSelected(false);
            archivedCheckBox.setSelected(false);
        }
    }

    @FXML
    public void setIsArchived () {
        if (archivedCheckBox.isSelected()) {
            activeCheckBox.setSelected(false);
            priorityCheckBox.setSelected(false);
            pendingCheckBox.setSelected(false);
        } else {
            activeCheckBox.setSelected(true);
        }
    }

    @FXML
    public void search () throws Exception {
        String query = searchBar.getText().trim().toLowerCase();
        List<String> dirs = conn.listDirs();
        List<BasicCase> newCases = new ArrayList<BasicCase>();

        for (String d : dirs) {
            BasicCase c = conn.readMetadata(d);

            if (c.getLetterNumber().toLowerCase().contains(query) ||
                c.getCaseNumber().toLowerCase()  .contains(query) ||
                c.getCompany().toLowerCase()     .contains(query) ||
                c.getFrom().toLowerCase()        .contains(query) ||
                c.getConcerning().toLowerCase()  .contains(query) ||
                c.getComments().toLowerCase()    .contains(query) )

            if (c.getIsPriority() == priorityCheckBox.isSelected() && c.getIsArchived() == archivedCheckBox.isSelected() &&
                c.getIsActive() == activeCheckBox.isSelected() && c.getIsPending()  == pendingCheckBox.isSelected())

                newCases.add(c);
        }

        cases.setAll(newCases);
        caseList.setItems(cases);
    }

    @FXML
    public void addCase () throws Exception {
        String s = Dialogs.TextInputDialog("Tworzenie nowej sprawy", "Wprowadź nr pisma:");

        if (s == null)
            return;

        if (!s.equals("")) {
            try {
                conn.createCase(s);
                Dialogs.InfoDialog("Sprawa utworzona pomyślnie", "Nie zapomnij o odświeżeniu listy poprzez ponowne kliknięcie przycisku Szukaj");
            } catch (Exception ignore) {
                Dialogs.InfoDialog("Nieprawidłowy nr pisma", "Nr pisma nie może pojawiać się w więcej niż jednej sprawie");
            }
        } else {
            Dialogs.InfoDialog("Nieprawidłowy nr pisma", "Po usunięciu spacji nr pisma jest pusty");
        }
    }

    @FXML
    public void deleteCase () throws Exception {
        BasicCase c = caseList.getSelectionModel().getSelectedItem();

        if (c == null)
            return;

        Boolean confirm = Dialogs.ConfirmationDialog("Zamierzasz usunąć sprawę o nr pisma " + c.getLetterNumber());

        if (confirm) {
            try {
                conn.deleteCase(c);
                Dialogs.InfoDialog("Sprawa usunięta pomyślnie", "Nie zapomnij o odświeżeniu listy poprzez ponowne kliknięcie przycisku Szukaj");
            } catch (Exception ignore) {
                Dialogs.InfoDialog("Nie można było usunąć sprawy", "Prawdopodobnie została ona już usunięta - odśwież listę przyciskiem Szukaj");
            }
        }
    }

    @FXML
    public void linkCase () {
        BasicCase c = caseList.getSelectionModel().getSelectedItem();

        if (c == null)
            return;

        String s = Dialogs.TextInputDialog("Powiązywanie spraw", "Wprowadź nr pisma powiązywanej sprawy:");

        if (s == null)
            return;

        if (!s.equals("")) {
            try {
                if (cases.stream().filter(x -> x.toString().equals(s)).collect(Collectors.toList()).size() >= 1) {
                    List<String> links = c.getLinks();
                    links.add(s);
                    c.setLinks(links);
                    conn.replaceMetadata(c);
                    Dialogs.InfoDialog("Sprawa powiązana pomyślnie", "Aby wyświetlić wszystkie powiązania, kliknij Pokaż powiązane");
                } else {
                    Dialogs.InfoDialog("Nie można było powiązać sprawy", "Prawdopodobnie wybrany nr pisma nie został zarejestrowany");
                }
            } catch (Exception ignore) {
                Dialogs.InfoDialog("Nie można było powiązać sprawy", "Prawdopodobnie wybrany nr pisma nie został zarejestrowany");
            }
        } else {
            Dialogs.InfoDialog("Nie można było powiązać sprawy", "Po usunięciu spacji nr pisma jest pusty");
        }
    }

    @FXML
    public void showCaseLinks () {
        BasicCase c = caseList.getSelectionModel().getSelectedItem();
        List<String> links = c.getLinks();
        List<BasicCase> filteredLinks = new ArrayList<BasicCase>();

        for (BasicCase bc : cases)
            if (links.contains(bc.toString()))
                filteredLinks.add(bc);

        cases.setAll(filteredLinks);
        caseList.setItems(cases);
    }

}
