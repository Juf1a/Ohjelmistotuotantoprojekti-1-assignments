package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ResourceBundle;

public class Main extends Application {

    private final Language initialLanguage;

    public Main() {
        this(Language.ENGLISH);
    }

    public Main(Language initialLanguage) {
        this.initialLanguage = initialLanguage;
    }

    @Override
    public void start(Stage stage) throws IOException {
        showView(stage, initialLanguage);
        stage.show();
    }

    /**
     * Loads the view with the language's ResourceBundle (UI labels). The controller then queries the
     * database in the same language (unit names and descriptions), so one switch changes both layers.
     */
    static void showView(Stage stage, Language language) throws IOException {
        ResourceBundle bundle = ResourceBundle.getBundle("MessagesBundle", language.getLocale());
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/MainView.fxml"), bundle);
        Parent root = loader.load();
        if (language.isRightToLeft()) {
            root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        }

        MainController controller = loader.getController();
        controller.setOnLanguageChange(selected -> {
            try {
                showView(stage, selected);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });

        Scene previous = stage.getScene();
        stage.setScene(previous == null
                ? new Scene(root, 440, 600)
                : new Scene(root, previous.getWidth(), previous.getHeight()));
        stage.setTitle(bundle.getString("app.title"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
