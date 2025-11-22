package main.java.com.lab.expressioncalculator;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class CalculatorController {

    private final ExpressionCalculationService expressionCalculationService;
    private final Stage stage;

    private TextArea expressionInputTextArea;
    private TextArea variableAssignmentsTextArea;
    private TextArea resultTextArea;
    private TextArea reversePolishNotationTextArea;
    private Button calculateButton;
    private Label statusLabel;

    public CalculatorController(ExpressionCalculationService expressionCalculationService, Stage stage) {
        this.expressionCalculationService = expressionCalculationService;
        this.stage = stage;
    }

    public Parent createRootPane() {
        VBox root = new VBox();
        root.getStyleClass().add("root-container");

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem openFromFileItem = new MenuItem("Load from file");
        openFromFileItem.setOnAction(event -> onOpenFromFile());
        MenuItem clearAllItem = new MenuItem("Clear all");
        clearAllItem.setOnAction(event -> onClearAll());
        fileMenu.getItems().addAll(openFromFileItem, clearAllItem);
        menuBar.getMenus().add(fileMenu);

        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("main-grid");

        ColumnConstraints firstColumn = new ColumnConstraints();
        firstColumn.setHgrow(Priority.ALWAYS);
        ColumnConstraints secondColumn = new ColumnConstraints();
        secondColumn.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(firstColumn, secondColumn);

        RowConstraints firstRow = new RowConstraints();
        firstRow.setVgrow(Priority.ALWAYS);
        RowConstraints secondRow = new RowConstraints();
        secondRow.setVgrow(Priority.ALWAYS);
        gridPane.getRowConstraints().addAll(firstRow, secondRow);

        expressionInputTextArea = new TextArea();
        expressionInputTextArea.setWrapText(true);
        expressionInputTextArea.setPrefRowCount(4);

        variableAssignmentsTextArea = new TextArea();
        variableAssignmentsTextArea.setWrapText(true);
        variableAssignmentsTextArea.setPrefRowCount(4);

        resultTextArea = new TextArea();
        resultTextArea.setWrapText(true);
        resultTextArea.setEditable(false);
        resultTextArea.setPrefRowCount(4);

        reversePolishNotationTextArea = new TextArea();
        reversePolishNotationTextArea.setWrapText(true);
        reversePolishNotationTextArea.setEditable(false);
        reversePolishNotationTextArea.setPrefRowCount(4);

        Label expressionLabel = new Label("Expression");
        Label variablesLabel = new Label("Variables (a = 4)");
        Label resultLabel = new Label("Result");
        Label reversePolishNotationLabel = new Label("Reverse polish notation");

        VBox expressionBox = new VBox(expressionLabel, expressionInputTextArea);
        expressionBox.getStyleClass().add("section-container");
        VBox variablesBox = new VBox(variablesLabel, variableAssignmentsTextArea);
        variablesBox.getStyleClass().add("section-container");
        VBox resultBox = new VBox(resultLabel, resultTextArea);
        resultBox.getStyleClass().add("section-container");
        VBox reversePolishNotationBox = new VBox(reversePolishNotationLabel, reversePolishNotationTextArea);
        reversePolishNotationBox.getStyleClass().add("section-container");

        gridPane.add(expressionBox, 0, 0);
        gridPane.add(variablesBox, 1, 0);
        gridPane.add(resultBox, 0, 1);
        gridPane.add(reversePolishNotationBox, 1, 1);

        calculateButton = new Button("CALCULATE");
        calculateButton.setMaxWidth(Double.MAX_VALUE);
        calculateButton.setOnAction(event -> onCalculate());

        HBox buttonBox = new HBox(calculateButton);
        buttonBox.getStyleClass().add("calculate-button-container");

        statusLabel = new Label("Enter expression and variables values");
        statusLabel.setId("statusLabel");

        root.getChildren().addAll(menuBar, gridPane, buttonBox, statusLabel);
        VBox.setVgrow(gridPane, Priority.ALWAYS);

        return root;
    }

    private void onCalculate() {
        String expression = expressionInputTextArea.getText();
        String assignments = variableAssignmentsTextArea.getText();
        try {
            CalculationResult calculationResult = expressionCalculationService.calculateExpression(expression, assignments);
            resultTextArea.setText(calculationResult.getNumericResultAsString());
            reversePolishNotationTextArea.setText(calculationResult.getReversePolishNotationAsString());
            statusLabel.setText("Calculations were successful");
        }
        catch (IllegalArgumentException e) {
            resultTextArea.setText("ERROR: " + e.getMessage());
            reversePolishNotationTextArea.clear();
            statusLabel.setText("Calculation error");
        }
    }

    private void onOpenFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open file with expression");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text files", "*.txt"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) loadExpressionFromFile(file);
    }

    private void onClearAll() {
        expressionInputTextArea.clear();
        variableAssignmentsTextArea.clear();
        resultTextArea.clear();
        reversePolishNotationTextArea.clear();
        statusLabel.setText("All fields were cleared");
    }

    private void loadExpressionFromFile(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            if (lines.isEmpty()) {
                resultTextArea.setText("File is empty");
                reversePolishNotationTextArea.clear();
                statusLabel.setText("File is empty");
                return;
            }
            String expressionLine = lines.get(0);
            StringBuilder variablesBuilder = new StringBuilder();
            for (int i = 1; i < lines.size(); i++) {
                variablesBuilder.append(lines.get(i));
                if (i < lines.size() - 1) variablesBuilder.append(System.lineSeparator());
            }
            expressionInputTextArea.setText(expressionLine);
            variableAssignmentsTextArea.setText(variablesBuilder.toString());
            resultTextArea.clear();
            reversePolishNotationTextArea.clear();
            statusLabel.setText("Loaded from file: " + file.getName());
        }
        catch (IOException e) {
            resultTextArea.setText("Failed read file: " + e.getMessage());
            reversePolishNotationTextArea.clear();
            statusLabel.setText("Failed read file");
        }
    }
}
