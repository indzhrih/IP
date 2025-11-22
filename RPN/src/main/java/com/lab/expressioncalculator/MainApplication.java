package main.java.com.lab.expressioncalculator;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) {
        ExpressionTokenizer expressionTokenizer = new ExpressionTokenizer();
        ReversePolishNotationConverter reversePolishNotationConverter = new ReversePolishNotationConverter();
        ReversePolishNotationEvaluator reversePolishNotationEvaluator = new ReversePolishNotationEvaluator();
        VariableAssignmentsParser variableAssignmentsParser = new VariableAssignmentsParser();
        ExpressionCalculationService expressionCalculationService = new ExpressionCalculationService(
                expressionTokenizer,
                reversePolishNotationConverter,
                reversePolishNotationEvaluator,
                variableAssignmentsParser
        );
        CalculatorController calculatorController = new CalculatorController(expressionCalculationService, stage);
        Parent root = calculatorController.createRootPane();
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(
                MainApplication.class.getResource("/styles.css").toExternalForm()
        );
        scene.getStylesheets().add(MainApplication.class.getResource("/styles.css").toExternalForm());
        stage.setTitle("RPN");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
