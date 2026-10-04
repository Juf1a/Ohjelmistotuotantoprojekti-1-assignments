package com.example;

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

public class Main extends Application {

    private final TemperatureConverter converter = new TemperatureConverter();
    private final TemperatureUnitDAO unitDAO = new TemperatureUnitDAO();
    private final TempRecordDAO recordDAO = new TempRecordDAO();
    private final ListView<TempRecord> historyList = new ListView<>();
    private final Label statusLabel = new Label();

    @Override
    public void start(Stage stage) {
        TextField inputField = new TextField();
        inputField.setPromptText("Enter temperature");

        ToggleGroup directionGroup = new ToggleGroup();
        RadioButton fToC = new RadioButton("Fahrenheit to Celsius");
        RadioButton cToF = new RadioButton("Celsius to Fahrenheit");
        fToC.setToggleGroup(directionGroup);
        cToF.setToggleGroup(directionGroup);
        fToC.setSelected(true);

        Button convertButton = new Button("Convert");
        Label resultLabel = new Label();

        convertButton.setOnAction(event -> {
            double input;
            try {
                input = Double.parseDouble(inputField.getText());
            } catch (NumberFormatException e) {
                resultLabel.setText("Please enter a valid number.");
                return;
            }

            boolean fahrenheitToCelsius = directionGroup.getSelectedToggle() == fToC;
            double result = fahrenheitToCelsius
                    ? converter.fahrenheitToCelsius(input)
                    : converter.celsiusToFahrenheit(input);
            double celsius = fahrenheitToCelsius ? result : input;

            String text = String.format("Result: %.2f", result);
            if (converter.isExtremeTemperature(celsius)) {
                text += " (Extreme temperature!)";
            }
            resultLabel.setText(text);

            saveRecord(input, result, fahrenheitToCelsius ? "Fahrenheit" : "Celsius",
                    fahrenheitToCelsius ? "Celsius" : "Fahrenheit");
        });

        historyList.setPrefHeight(180);

        VBox root = new VBox(10, inputField, fToC, cToF, convertButton, resultLabel,
                new Label("Recent conversions:"), historyList, statusLabel);
        root.setPadding(new Insets(15));

        refreshHistory();

        stage.setTitle("Temperature Converter");
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
            statusLabel.setText("Database error: " + e.getMessage());
        }
    }

    private void refreshHistory() {
        try {
            historyList.getItems().setAll(recordDAO.findRecent(10));
            statusLabel.setText("");
        } catch (SQLException e) {
            statusLabel.setText("Database error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
