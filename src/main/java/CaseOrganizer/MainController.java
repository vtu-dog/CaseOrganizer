package app.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import app.dialogs.Dialogs;
import app.ftpconn.FTPConn;
import app.xml.BasicCase;


public class MainController {

    private FTPConn conn = null;
    private ObservableList<BasicCase> cases = FXCollections.observableArrayList();
    private ObservableList<String> files = FXCollections.observableArrayList();

    @FXML private Button searchButton;
    @FXML private TextField searchBar;

    @FXML private CheckBox priorityCheckBox;
    @FXML private CheckBox archivedCheckBox;
    @FXML private CheckBox activeCheckBox;
    @FXML private CheckBox pendingCheckBox;

    @FXML private ListView<BasicCase> caseList;
    @FXML private ListView<String> fileList;

    @FXML private Button deleteCaseButton;
    @FXML private Button linkCaseButton;
    @FXML private Button showCaseLinksButton;

    @FXML private Button saveCaseButton;
    @FXML private Button restoreDefaultsButton;

    @FXML private Button addFileButton;
    @FXML private Button deleteFileButton;
    @FXML private Button downloadFileButton;
    @FXML private Button downloadAllFilesButton;

    @FXML private CheckBox priorityCaseCheckBox;
    @FXML private CheckBox archivedCaseCheckBox;
    @FXML private CheckBox activeCaseCheckBox;
    @FXML private CheckBox pendingCaseCheckBox;

    @FXML private TextField friendlyName;
    @FXML private TextField letterNumber;
    @FXML private TextField company;
    @FXML private TextField from;
    @FXML private TextField concerning;
    @FXML private TextField inspectionPeriod;
    @FXML private TextField plannedEndDate;
    @FXML private TextField caseNumber;
    @FXML private TextField correspondenceDescription;
    @FXML private TextField remaining;
    @FXML private TextField deliveryMethod;
    @FXML private TextField plannedReplyDate;
    @FXML private TextField deliveryConfirmation;
    @FXML private TextField receivedFrom;
    @FXML private TextField hearing;
    @FXML private TextField comments;
    @FXML private DatePicker dateReceived;
    @FXML private DatePicker replyDeadline;


    public MainController (FTPConn conn) {
        this.conn = conn;
    }

    @FXML
    public void initialize () {
        deleteCaseButton.setDisable(true);
        linkCaseButton.setDisable(true);
        showCaseLinksButton.setDisable(true);

        saveCaseButton.setDisable(true);
        restoreDefaultsButton.setDisable(true);

        addFileButton.setDisable(true);
        deleteFileButton.setDisable(true);
        downloadFileButton.setDisable(true);
        downloadAllFilesButton.setDisable(true);

        letterNumber.setEditable(false);

        caseList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BasicCase>() {
            @Override
            public void changed(ObservableValue<? extends BasicCase> observable, BasicCase oldValue, BasicCase newValue) {
                if (newValue == null) {
                    deleteCaseButton.setDisable(true);
                    linkCaseButton.setDisable(true);
                    showCaseLinksButton.setDisable(true);

                    saveCaseButton.setDisable(true);
                    restoreDefaultsButton.setDisable(true);

                    if (fileList.getSelectionModel().getSelectedItem() == null) {
                        addFileButton.setDisable(true);
                        downloadAllFilesButton.setDisable(true);
                    }

                    updateCaseInfo(new BasicCase());
                    emptyFileList();

                } else {
                    deleteCaseButton.setDisable(false);
                    linkCaseButton.setDisable(false);
                    showCaseLinksButton.setDisable(false);
                    saveCaseButton.setDisable(false);
                    restoreDefaultsButton.setDisable(false);
                    addFileButton.setDisable(false);
                    updateFileList(newValue);
                    updateCaseInfo(newValue);
                }
            }
        });

        fileList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue == null) {
                    deleteFileButton.setDisable(true);
                    downloadFileButton.setDisable(true);
                } else {
                    addFileButton.setDisable(false);
                    deleteFileButton.setDisable(false);
                    downloadFileButton.setDisable(false);
                    downloadAllFilesButton.setDisable(false);
                }
            }
        });

    }


    private void emptyFileList () {
        List<String> newFiles = new ArrayList<String>();
        files.setAll(newFiles);
        fileList.setItems(files);
    }


    private void updateFileList (BasicCase caseObj) {
        try {
            List<String> newFiles = conn.listDirs(caseObj.getLetterNumber());
            newFiles = newFiles.stream().filter(x -> !x.equals("meta.xml")).collect(Collectors.toList());

            if (newFiles.size() == 0)
                downloadAllFilesButton.setDisable(true);
            else
                downloadAllFilesButton.setDisable(false);

            files.setAll(newFiles);
            fileList.setItems(files);
        } catch (Exception ignore) { }
    }

    private void updateCaseInfo (BasicCase caseObj) {
        activeCaseCheckBox.setSelected(caseObj.getIsActive());
        priorityCaseCheckBox.setSelected(caseObj.getIsPriority());
        pendingCaseCheckBox.setSelected(caseObj.getIsPending());
        archivedCaseCheckBox.setSelected(caseObj.getIsArchived());

        friendlyName.setText(caseObj.getFriendlyName());
        letterNumber.setText(caseObj.getLetterNumber());
        company.setText(caseObj.getCompany());
        from.setText(caseObj.getFrom());
        concerning.setText(caseObj.getConcerning());
        inspectionPeriod.setText(caseObj.getInspectionPeriod());
        plannedEndDate.setText(caseObj.getPlannedEndDate());
        caseNumber.setText(caseObj.getCaseNumber());
        correspondenceDescription.setText(caseObj.getCorrespondenceDescription());
        remaining.setText(caseObj.getRemaining());
        deliveryMethod.setText(caseObj.getDeliveryMethod());
        plannedReplyDate.setText(caseObj.getPlannedReplyDate());
        deliveryConfirmation.setText(caseObj.getDeliveryConfirmation());
        receivedFrom.setText(caseObj.getReceivedFrom());
        hearing.setText(caseObj.getHearing());
        comments.setText(caseObj.getComments());
        dateReceived.setValue(caseObj.getDateReceived().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        replyDeadline.setValue(caseObj.getReplyDeadline().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }


    @FXML
    public void saveCase () throws Exception {
        BasicCase c = new BasicCase();

        c.setIsActive(activeCaseCheckBox.isSelected());
        c.setIsPriority(priorityCaseCheckBox.isSelected());
        c.setIsPending(pendingCaseCheckBox.isSelected());
        c.setIsArchived(archivedCaseCheckBox.isSelected());

        c.setFriendlyName(friendlyName.getText());
        c.setLetterNumber(letterNumber.getText());
        c.setCompany(company.getText());
        c.setFrom(from.getText());
        c.setConcerning(concerning.getText());
        c.setInspectionPeriod(inspectionPeriod.getText());
        c.setPlannedEndDate(plannedEndDate.getText());
        c.setCaseNumber(caseNumber.getText());
        c.setCorrespondenceDescription(correspondenceDescription.getText());
        c.setRemaining(remaining.getText());
        c.setDeliveryMethod(deliveryMethod.getText());
        c.setPlannedReplyDate(plannedReplyDate.getText());
        c.setDeliveryConfirmation(deliveryConfirmation.getText());
        c.setReceivedFrom(receivedFrom.getText());
        c.setHearing(hearing.getText());
        c.setComments(comments.getText());
        c.setDateReceived(java.sql.Date.valueOf(dateReceived.getValue()));
        c.setReplyDeadline(java.sql.Date.valueOf(replyDeadline.getValue()));

        try {
            conn.replaceMetadata(c);
            Dialogs.InfoDialog("Dane sprawy zapisane pomyślnie", "Nie zapomnij o odświeżeniu listy poprzez ponowne kliknięcie przycisku Szukaj");
        }
        catch (Exception e) {
            try {
                conn.noop();
                Dialogs.WarningDialog("Nie można było zapisać danych sprawy", "Prawdopodobnie została ona usunięta", e);
            } catch (Exception ignore) {
                Dialogs.NoopFailedDialog();
            }
        }
    }


    @FXML
    public void restoreDefaults () {
        BasicCase c = caseList.getSelectionModel().getSelectedItem();
        if (c != null)
            updateCaseInfo(c);
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
    public void setIsCasePriority () {
        if (priorityCaseCheckBox.isSelected()) {
            archivedCaseCheckBox.setSelected(false);
            activeCaseCheckBox.setSelected(true);
            pendingCaseCheckBox.setSelected(false);
        }
    }

    @FXML
    public void setIsCaseActive () {
        if (activeCaseCheckBox.isSelected())
            archivedCaseCheckBox.setSelected(false);
        else {
            archivedCaseCheckBox.setSelected(true);
            priorityCaseCheckBox.setSelected(false);
            pendingCaseCheckBox.setSelected(false);
        }
    }

    @FXML
    public void setIsCasePending () {
        if (pendingCaseCheckBox.isSelected()) {
            activeCaseCheckBox.setSelected(true);
            priorityCaseCheckBox.setSelected(false);
            archivedCaseCheckBox.setSelected(false);
        }
    }

    @FXML
    public void setIsCaseArchived () {
        if (archivedCaseCheckBox.isSelected()) {
            activeCaseCheckBox.setSelected(false);
            priorityCaseCheckBox.setSelected(false);
            pendingCaseCheckBox.setSelected(false);
        } else {
            activeCaseCheckBox.setSelected(true);
        }
    }



    @FXML
    public void search () throws Exception {
        try {
            String query = searchBar.getText().trim().toLowerCase();
            List<String> dirs = conn.listDirs(null);
            List<BasicCase> newCases = new ArrayList<BasicCase>();

            for (String d : dirs) {
                BasicCase c = conn.readMetadata(d);

                if (c.getFriendlyName().toLowerCase()              .contains(query) ||
                    c.getLetterNumber().toLowerCase()              .contains(query) ||
                    c.getCaseNumber().toLowerCase()                .contains(query) ||
                    c.getCompany().toLowerCase()                   .contains(query) ||
                    c.getFrom().toLowerCase()                      .contains(query) ||
                    c.getConcerning().toLowerCase()                .contains(query) ||
                    c.getComments().toLowerCase()                  .contains(query) ||
                    c.getCorrespondenceDescription().toLowerCase() .contains(query) ||
                    c.getDeliveryMethod().toLowerCase()            .contains(query) ||
                    c.getReceivedFrom().toLowerCase()              .contains(query) )

                    {
                        if (activeCheckBox.isSelected() && !priorityCheckBox.isSelected() && !pendingCheckBox.isSelected()) {
                            if (c.getIsActive())
                                newCases.add(c);
                        }

                        else
                            if (c.getIsActive() == activeCheckBox.isSelected() && c.getIsArchived() == archivedCheckBox.isSelected() &&
                                c.getIsPriority() == priorityCheckBox.isSelected() && c.getIsPending()  == pendingCheckBox.isSelected())

                                newCases.add(c);
                    }
            }

            cases.setAll(newCases);
            caseList.setItems(cases);

            if (newCases.size() == 0) {
                Dialogs.InfoDialog("Nie znaleziono spraw o zadanych parametrach", "Upewnij się, że do wyszukiwania dobrano odpowiednie tagi");
            }
        }
        catch (Exception e) {
            try {
                conn.noop();
                Dialogs.WarningDialog("Błąd wyszukiwania", "Utracono połączenie z serwerem", e);
            } catch (Exception ignore) {
                Dialogs.NoopFailedDialog();
            }
        }
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
            } catch (Exception e) {
                try {
                    conn.noop();
                    Dialogs.WarningDialog("Nieprawidłowy nr pisma", "Nr pisma nie może pojawiać się w więcej niż jednej sprawie", e);
                } catch (Exception ignore) {
                    Dialogs.NoopFailedDialog();
                }
            }
        } else {
            Dialogs.WarningDialog("Nieprawidłowy nr pisma", "Po usunięciu spacji nr pisma jest pusty", null);
        }
    }

    @FXML
    public void deleteCase () throws Exception {
        BasicCase c = caseList.getSelectionModel().getSelectedItem();

        if (c == null)
            return;

        Boolean confirm = Dialogs.ConfirmationDialog("Zamierzasz usunąć sprawę " + c.toString());

        if (confirm) {
            try {
                conn.deleteCase(c);
                Dialogs.InfoDialog("Sprawa usunięta pomyślnie", "Nie zapomnij o odświeżeniu listy poprzez ponowne kliknięcie przycisku Szukaj");
            } catch (Exception e) {
                try {
                    conn.noop();
                    Dialogs.WarningDialog("Nie można było usunąć sprawy", "Prawdopodobnie została ona już usunięta - odśwież listę przyciskiem Szukaj", e);
                } catch (Exception ignore) {
                    Dialogs.NoopFailedDialog();
                }
            }
        }
    }

    @FXML
    public void linkCase () throws Exception {
        BasicCase c = caseList.getSelectionModel().getSelectedItem();

        if (c == null)
            return;

        String s = Dialogs.TextInputDialog("Powiązywanie spraw", "Wprowadź nr pisma powiązywanej sprawy:");

        if (s == null)
            return;

        List<BasicCase> allCases = new ArrayList<BasicCase>();
        List<String> dirs = conn.listDirs(null);

        for (String d : dirs) {
            BasicCase bc = conn.readMetadata(d);
            allCases.add(bc);
        }

        if (!s.equals("")) {
            try {
                if (c.getLetterNumber().equals(s))
                    Dialogs.WarningDialog("Nie można było powiązać sprawy", "Powiązywanie sprawy z sobą samą jest niedozwolone", null);

                else if (allCases.stream().filter(x -> x.toString().equals(s)).collect(Collectors.toList()).size() >= 1) {
                    List<String> links = c.getLinks();
                    links.add(s);
                    c.setLinks(links);
                    conn.replaceMetadata(c);
                    Dialogs.InfoDialog("Sprawa powiązana pomyślnie", "Aby wyświetlić wszystkie powiązania, kliknij Pokaż powiązane");
                }

                else {
                    Dialogs.WarningDialog("Nie można było powiązać sprawy", "Wybrany nr pisma nie został zarejestrowany", null);
                }
            } catch (Exception e) {
                try {
                    conn.noop();
                    Dialogs.WarningDialog("Nie można było powiązać sprawy", "Prawdopodobnie wybrany nr pisma nie został zarejestrowany", e);
                } catch (Exception ignore) {
                    Dialogs.NoopFailedDialog();
                }
            }
        } else {
            Dialogs.WarningDialog("Nie można było powiązać sprawy", "Po usunięciu spacji nr pisma jest pusty", null);
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



    @FXML
    public void addFile () throws Exception {
        BasicCase c = caseList.getSelectionModel().getSelectedItem();
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile == null)
            return;

        if (selectedFile.length() / (1024*1024) >= 50) {
            Dialogs.InfoDialog("Wybrany plik nie może być dodany", "Przekroczono limit 50MB");
            return;
        }

        try {
            conn.uploadFile(selectedFile, c.getLetterNumber());
            updateFileList(c);
            Dialogs.InfoDialog("Pomyślnie dodano plik", null);
        } catch (Exception e) {
            try {
                conn.noop();
                Dialogs.WarningDialog("Nie można było dodać pliku", null, e);
            } catch (Exception ignore) {
                Dialogs.NoopFailedDialog();
            }
        }
    }

    @FXML
    public void deleteFile () throws Exception {
        String s = fileList.getSelectionModel().getSelectedItem();
        BasicCase c = caseList.getSelectionModel().getSelectedItem();

        if (s == null || c == null)
            return;

        Boolean confirm = Dialogs.ConfirmationDialog("Zamierzasz usunąć plik " + s);

        if (confirm) {
            try {
                conn.deleteFile(c.getLetterNumber(), s);
                updateFileList(c);
                Dialogs.InfoDialog("Plik usunięty pomyślnie", null);
            } catch (Exception e) {
                try {
                    conn.noop();
                    Dialogs.WarningDialog("Nie można było usunąć pliku", "Prawdopodobnie został on już usunięty", e);
                } catch (Exception ignore) {
                    Dialogs.NoopFailedDialog();
                }
            }
        }
    }

    @FXML
    public void downloadFile () throws Exception {
        String s = fileList.getSelectionModel().getSelectedItem();
        BasicCase c = caseList.getSelectionModel().getSelectedItem();

        if (s == null || c == null)
            return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(s);
        File selectedFile = fileChooser.showSaveDialog(null);

        if (selectedFile == null)
            return;

        try {
            ByteArrayOutputStream baos = conn.downloadFile(c.getLetterNumber(), s);
            FileOutputStream fos = new FileOutputStream(selectedFile);
            baos.writeTo(fos);
            Dialogs.InfoDialog("Plik pobrany pomyślnie", null);
        }
        catch (Exception e) {
            try {
                conn.noop();
                Dialogs.WarningDialog("Nie można było pobrać pliku", "Prawdopodobnie został on usunięty", e);
            } catch (Exception ignore) {
                Dialogs.NoopFailedDialog();
            }
        }

    }

    @FXML
    public void downloadAllFiles () throws Exception {
        String s = fileList.getSelectionModel().getSelectedItem();
        BasicCase c = caseList.getSelectionModel().getSelectedItem();

        if (c == null)
            return;

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory == null)
            return;

        try {
            for (String file : files) {
                ByteArrayOutputStream baos = conn.downloadFile(c.getLetterNumber(), s);
                FileOutputStream fos = new FileOutputStream(new File(selectedDirectory.toString() + "/" + file));
                baos.writeTo(fos);
            }
            Dialogs.InfoDialog("Pliki pobrano pomyślnie", null);
        }
        catch (Exception e) {
            try {
                conn.noop();
                Dialogs.WarningDialog("Nie można było pobrać plików", null, e);
            } catch (Exception ignore) {
                Dialogs.NoopFailedDialog();
            }
        }
    }

}
