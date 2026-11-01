package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class MainController {

    private final TemperatureConverter converter = new TemperatureConverter();
    private final TemperatureUnitDAO unitDAO = new TemperatureUnitDAO();
    private final TempRecordDAO recordDAO = new TempRecordDAO();

    @FXML
    private ResourceBundle resources;
    @FXML
    private TextField inputField;
    @FXML
    private ToggleGroup directionGroup;
    @FXML
    private RadioButton fToC;
    @FXML
    private Label resultLabel;
    @FXML
    private ListView<TempRecord> historyList;
    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        refreshHistory();
    }

    @FXML
    private void handleConvert() {
        double input;
        try {
            input = Double.parseDouble(inputField.getText());
        } catch (NumberFormatException e) {
            resultLabel.setText(resources.getString("numberinput.prompt"));
            return;
        }

        boolean fahrenheitToCelsius = directionGroup.getSelectedToggle() == fToC;
        double result = fahrenheitToCelsius
                ? converter.fahrenheitToCelsius(input)
                : converter.celsiusToFahrenheit(input);
        double celsius = fahrenheitToCelsius ? result : input;

        String text = MessageFormat.format(resources.getString("result.text"), String.format("%.2f", result));
        if (converter.isExtremeTemperature(celsius)) {
            text += " " + resources.getString("extreme.warning");
        }
        resultLabel.setText(text);

        saveRecord(input, result, fahrenheitToCelsius ? "Fahrenheit" : "Celsius",
                fahrenheitToCelsius ? "Celsius" : "Fahrenheit");
    }

    private void saveRecord(double input, double result, String fromName, String toName) {
        try {
            TemperatureUnit from = unitDAO.findByName(fromName);
            TemperatureUnit to = unitDAO.findByName(toName);
            recordDAO.save(new TempRecord(input, from, result, to));
            refreshHistory();
        } catch (SQLException e) {
            statusLabel.setText(MessageFormat.format(resources.getString("error.database"), e.getMessage()));
        }
    }

    private void refreshHistory() {
        try {
            historyList.getItems().setAll(recordDAO.findRecent(10));
            statusLabel.setText("");
        } catch (SQLException e) {
            statusLabel.setText(MessageFormat.format(resources.getString("error.database"), e.getMessage()));
        }
    }
}
