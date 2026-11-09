package com.example;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class MainController {

    static final int HISTORY_SIZE = 5;

    private final TemperatureConverter converter = new TemperatureConverter();
    private final UnitDAO unitDAO = new UnitDAO();
    private final ConversionDAO conversionDAO = new ConversionDAO(unitDAO);

    @FXML
    private ResourceBundle resources;
    @FXML
    private ComboBox<Language> languageBox;
    @FXML
    private ComboBox<String> categoryBox;
    @FXML
    private TextField inputField;
    @FXML
    private ComboBox<Unit> fromUnitBox;
    @FXML
    private ComboBox<Unit> toUnitBox;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label resultLabel;
    @FXML
    private ListView<Conversion> historyList;
    @FXML
    private Label statusLabel;

    private Language language;
    private Consumer<Language> onLanguageChange;

    @FXML
    private void initialize() {
        language = Language.fromLocale(resources.getLocale());

        languageBox.getItems().setAll(Language.values());
        languageBox.setValue(language);
        languageBox.valueProperty().addListener((obs, old, selected) -> {
            if (selected != null && selected != language && onLanguageChange != null) {
                Platform.runLater(() -> onLanguageChange.accept(selected));
            }
        });

        // Category labels are static UI text, so they come from the ResourceBundle
        categoryBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(String category) {
                if (category == null) {
                    return "";
                }
                String key = "category." + category;
                return resources.containsKey(key) ? resources.getString(key) : category;
            }

            @Override
            public String fromString(String text) {
                return text;
            }
        });
        categoryBox.valueProperty().addListener((obs, old, category) -> loadUnits(category));
        fromUnitBox.valueProperty().addListener((obs, old, unit) -> showDescriptions());
        toUnitBox.valueProperty().addListener((obs, old, unit) -> showDescriptions());

        try {
            List<String> categories = unitDAO.findCategories();
            categoryBox.getItems().setAll(categories);
            if (!categories.isEmpty()) {
                categoryBox.setValue(categories.get(0));
            }
        } catch (SQLException e) {
            showDatabaseError(e);
        }
        refreshHistory();
    }

    void setOnLanguageChange(Consumer<Language> onLanguageChange) {
        this.onLanguageChange = onLanguageChange;
    }

    /** Unit names and descriptions come from the database in the selected language. */
    private void loadUnits(String category) {
        if (category == null) {
            return;
        }
        try {
            List<Unit> units = unitDAO.findByCategory(category, language.getCode());
            fromUnitBox.getItems().setAll(units);
            toUnitBox.getItems().setAll(units);
            if (!units.isEmpty()) {
                fromUnitBox.setValue(units.get(0));
                toUnitBox.setValue(units.get(Math.min(1, units.size() - 1)));
            }
        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    private void showDescriptions() {
        Unit from = fromUnitBox.getValue();
        Unit to = toUnitBox.getValue();
        StringBuilder text = new StringBuilder();
        if (from != null) {
            text.append(from.getName()).append(": ").append(from.getDescription());
        }
        if (to != null && to != from) {
            text.append('\n').append(to.getName()).append(": ").append(to.getDescription());
        }
        descriptionLabel.setText(text.toString());
    }

    @FXML
    private void handleConvert() {
        Unit from = fromUnitBox.getValue();
        Unit to = toUnitBox.getValue();
        if (from == null || to == null) {
            resultLabel.setText(resources.getString("units.missing"));
            return;
        }

        double input;
        try {
            input = Double.parseDouble(inputField.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            resultLabel.setText(resources.getString("numberinput.prompt"));
            return;
        }

        double result = converter.convert(input, from, to);
        String text = MessageFormat.format(resources.getString("result.text"),
                String.format("%.2f", input), from.getName(), String.format("%.2f", result), to.getName());
        if (converter.isExtreme(input, from)) {
            text += " " + resources.getString("extreme.warning");
        }
        resultLabel.setText(text);

        try {
            conversionDAO.save(new Conversion(input, from, result, to, language.getCode()));
            refreshHistory();
        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    private void refreshHistory() {
        try {
            historyList.getItems().setAll(conversionDAO.findRecent(HISTORY_SIZE, language.getCode()));
        } catch (SQLException e) {
            showDatabaseError(e);
        }
    }

    private void showDatabaseError(SQLException e) {
        statusLabel.setText(MessageFormat.format(resources.getString("error.database"), e.getMessage()));
    }
}
