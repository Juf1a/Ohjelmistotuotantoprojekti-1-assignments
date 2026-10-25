package com.example;

import javafx.scene.control.ChoiceDialog;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.text.MessageFormat;

public class Main extends Application {

    private final TemperatureConverter converter = new TemperatureConverter();
    private final TemperatureUnitDAO unitDAO = new TemperatureUnitDAO();
    private final TempRecordDAO recordDAO = new TempRecordDAO();
    private final ListView<TempRecord> historyList = new ListView<>();
    private final Label statusLabel = new Label();
    private Locale locale;
    private ResourceBundle bundle;

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
            default -> new Locale("en", "US");
        };
    }

    private Locale askForLocale() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("English", "English", "Finnish", "Swedish", "Japanese");
        dialog.setTitle("Language");
        dialog.setHeaderText("Select your language");
        String choice = dialog.showAndWait().orElse("English");
        return localeFor(choice);
    }

    @Override
    public void start(Stage stage) {
        if (locale == null) {
            locale = askForLocale();
        }
        bundle = ResourceBundle.getBundle("MessagesBundle", locale);

        TextField inputField = new TextField();
        inputField.setPromptText(bundle.getString("input.prompt"));

        ToggleGroup directionGroup = new ToggleGroup();
        RadioButton fToC = new RadioButton(bundle.getString("fahtocel.radio"));
        RadioButton cToF = new RadioButton(bundle.getString("celtofah.radio"));
        fToC.setToggleGroup(directionGroup);
        cToF.setToggleGroup(directionGroup);
        fToC.setSelected(true);

        Button convertButton = new Button(bundle.getString("convert.button"));
        Label resultLabel = new Label();

        convertButton.setOnAction(event -> {
            double input;
            try {
                input = Double.parseDouble(inputField.getText());
            } catch (NumberFormatException e) {
                resultLabel.setText(bundle.getString("numberinput.prompt"));
                return;
            }

            boolean fahrenheitToCelsius = directionGroup.getSelectedToggle() == fToC;
            double result = fahrenheitToCelsius
                    ? converter.fahrenheitToCelsius(input)
                    : converter.celsiusToFahrenheit(input);
            double celsius = fahrenheitToCelsius ? result : input;

            String text = MessageFormat.format(bundle.getString("result.text"), String.format("%.2f", result));
            if (converter.isExtremeTemperature(celsius)) {
                text += " " + bundle.getString("extreme.warning");
            }
            resultLabel.setText(text);

            saveRecord(input, result, fahrenheitToCelsius ? "Fahrenheit" : "Celsius",
                    fahrenheitToCelsius ? "Celsius" : "Fahrenheit");
        });

        historyList.setPrefHeight(180);

        VBox root = new VBox(10, inputField, fToC, cToF, convertButton, resultLabel,
                new Label(bundle.getString("history.label")), historyList, statusLabel);
        root.setPadding(new Insets(15));

        refreshHistory();

        stage.setTitle(bundle.getString("app.title"));
        stage.setScene(new Scene(root, 360, 460));
        stage.show();
    }

    private void saveRecord(double input, double result, String fromName, String toName) {
        try {
            TemperatureUnit from = unitDAO.findByName(fromName);
            TemperatureUnit to = unitDAO.findByName(toName);
            recordDAO.save(new TempRecord(input, from, result, to));
            refreshHistory();
        } catch (SQLException e) {
            statusLabel.setText(MessageFormat.format(bundle.getString("error.database"), e.getMessage()));
        }
    }

    private void refreshHistory() {
        try {
            historyList.getItems().setAll(recordDAO.findRecent(10));
            statusLabel.setText("");
        } catch (SQLException e) {
            statusLabel.setText(MessageFormat.format(bundle.getString("error.database"), e.getMessage()));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
