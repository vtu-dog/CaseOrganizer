package app.dialogs;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;


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

        alert.showAndWait();
    }

    public static void ErrorDialog (String msg) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.show();
    }

    public static void WarningDialog (String msg) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Ostrzeżenie");
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void InfoDialog (String msg1, String msg2) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Informacja");
        alert.setHeaderText(msg1);
        alert.setContentText(msg2);
        alert.showAndWait();
    }

    public static boolean ConfirmationDialog (String msg) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Potwierdzenie");
        alert.setHeaderText(msg);
        alert.setContentText("Czy na pewno chcesz to zrobić?");

        Optional<ButtonType> result = alert.showAndWait();
        return (result.get() == ButtonType.OK);
    }

    public static String TextInputDialog (String msg1, String msg2) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Wybór");
        dialog.setHeaderText(msg1);
        dialog.setContentText(msg2);

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            return result.get().trim();
        }
        else {
            return null;
        }
    }

}
