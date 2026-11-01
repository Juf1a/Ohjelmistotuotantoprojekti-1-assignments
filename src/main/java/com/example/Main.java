package com.example;

import javafx.scene.control.ChoiceDialog;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private Locale locale;

    public Main() {
    }

    // Lets tests skip the language dialog
    public Main(Locale locale) {
        this.locale = locale;
    }

    static Locale localeFor(String choice) {
        return switch (choice) {
            case "Finnish" -> new Locale("fi", "FI");
            case "Swedish" -> new Locale("sv", "SE");
            case "Japanese" -> new Locale("ja", "JP");
            case "Arabic" -> new Locale("ar", "SA");
            default -> new Locale("en", "US");
        };
    }

    private Locale askForLocale() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("English", "English", "Finnish", "Swedish", "Japanese", "Arabic");
        dialog.setTitle("Language");
        dialog.setHeaderText("Select your language");
        String choice = dialog.showAndWait().orElse("English");
        return localeFor(choice);
    }

    @Override
    public void start(Stage stage) throws IOException {
        if (locale == null) {
            locale = askForLocale();
        }
        ResourceBundle bundle = ResourceBundle.getBundle("MessagesBundle", locale);

        Parent root = FXMLLoader.load(getClass().getResource("/MainView.fxml"), bundle);
        if (locale.getLanguage().equals("ar")) {
            root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        }

        stage.setTitle(bundle.getString("app.title"));
        stage.setScene(new Scene(root, 360, 460));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
