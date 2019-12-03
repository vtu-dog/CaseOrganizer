package app.dialogs;

import java.io.File;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.Optional;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import org.apache.commons.configuration.PropertiesConfiguration;


public class Dialogs {

    private static final String header = "Wystąpił błąd, proszę skontaktować się z pomocą techniczną";

    public static void ExceptionDialog (Exception ex) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Błąd");

        String s = "Aby usprawnić naprawę, nie zapomnij o podaniu poniższego komunikatu o błędzie, a także opisu czynności, które go spowodowały";
        alert.setHeaderText(header + "\n" + s);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);
        alert.getDialogPane().setContent(expContent);

        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setResizable(false);
        alert.showAndWait();
    }

    public static void ErrorDialog (String msg) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setResizable(false);
        alert.showAndWait();
    }

    public static void WarningDialog (String msg1, String msg2, Exception ex) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Ostrzeżenie");
        alert.setHeaderText(msg1);
        alert.setContentText(msg2);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        if (ex != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionText = sw.toString();

            TextArea textArea = new TextArea(exceptionText);
            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(textArea, 0, 0);

            alert.getDialogPane().setExpandableContent(expContent);
        }

        alert.setResizable(false);
        alert.showAndWait();
    }

    public static void InfoDialog (String msg1, String msg2) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText(msg1);
        alert.setContentText(msg2);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setResizable(false);
        alert.showAndWait();
    }

    public static boolean ConfirmationDialog (String msg) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Potwierdzenie");
        alert.setHeaderText(msg);
        alert.setContentText("Czy na pewno chcesz to zrobić?");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setResizable(false);

        Optional<ButtonType> result = alert.showAndWait();
        return (result.get() == ButtonType.OK);
    }

    public static String TextInputDialog (String msg1, String msg2) {
        TextInputDialog alert = new TextInputDialog("");
        alert.setTitle("Wybór");
        alert.setHeaderText(msg1);
        alert.setContentText(msg2);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setResizable(false);

        Optional<String> result = alert.showAndWait();

        if (result.isPresent()) {
            return result.get().trim();
        }
        else {
            return null;
        }
    }

    public static void NoopFailedDialog () {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Ostrzeżenie");
        alert.setHeaderText("Połączenie z serwerem zostało zerwane");
        alert.setContentText("Program wyłączy się, by zapobiec utracie danych");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setResizable(false);
        alert.showAndWait();
        System.exit(0);
    }

    public static boolean LoginDialog () throws Exception {
        Dialog<ButtonType> alert = new Dialog<ButtonType>();
        alert.setTitle("Logowanie");
        alert.setHeaderText("Zaloguj się");

        ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
        alert.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        PropertiesConfiguration config = null;

        try {
            config = new PropertiesConfiguration("./config.ini");
        }
        catch (Exception ignore) {
            File f = new File("./config.ini");
            f.createNewFile();
            config = new PropertiesConfiguration("./config.ini");
            config.setProperty("host", "");
            config.setProperty("user", "");
            config.setProperty("passwd", "");
            config.save();
        }

        TextField host = new TextField();
        String hostText = config.getString("host");
        if (hostText.equals(""))
            host.setPromptText("adres serwera");
        else
            host.setText(hostText);

        TextField user = new TextField();
        String userText = config.getString("user");
        if (userText.equals(""))
            user.setPromptText("nazwa użytkownika");
        else
            user.setText(userText);

        PasswordField passwd = new PasswordField();
        String passwdText = config.getString("passwd");
        if (passwdText.equals(""))
            passwd.setPromptText("hasło");
        else
            passwd.setText(passwdText);

        grid.add(new Label("Adres serwera:"), 0, 0);
        grid.add(host, 1, 0);
        grid.add(new Label("Nazwa użytkownika:"), 0, 1);
        grid.add(user, 1, 1);
        grid.add(new Label("Hasło:"), 0, 2);
        grid.add(passwd, 1, 2);

        alert.getDialogPane().setContent(grid);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setResizable(false);

        Optional<ButtonType> result = alert.showAndWait();
        alert.close();

        if (result.get().getButtonData() == ButtonData.OK_DONE) {
            config.setProperty("host", host.getText().trim());
            config.setProperty("user", user.getText().trim());
            config.setProperty("passwd", passwd.getText().trim());
            config.save();
            return true;
        }
        else {
            return false;
        }

    }

}
