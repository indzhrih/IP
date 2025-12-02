package org.example.sortapp.view;

import org.example.sortapp.model.SortAlgorithmType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SortingView {

    private final BorderPane root;

    private final SortingPane bubbleSortPane;
    private final SortingPane mergeSortPane;
    private final SortingPane quickSortPane;

    private final MenuItem openFileMenuItem;
    private final MenuItem exitMenuItem;

    private final Button startButton;
    private final Button generateRandomArrayButton;

    private final Slider delaySlider;
    private final Label delayValueLabel;
    private final TextField delayTextField;

    private final TextField arraySizeTextField;
    private final Label arrayInfoLabel;

    public SortingView() {
        this.root = new BorderPane();
        this.bubbleSortPane = new SortingPane(SortAlgorithmType.BUBBLE_SORT.getDisplayName());
        this.mergeSortPane = new SortingPane(SortAlgorithmType.MERGE_SORT.getDisplayName());
        this.quickSortPane = new SortingPane(SortAlgorithmType.QUICK_SORT.getDisplayName());
        Menu fileMenu = new Menu("File");
        this.openFileMenuItem = new MenuItem("Open...");
        this.exitMenuItem = new MenuItem("Exit");
        fileMenu.getItems().addAll(openFileMenuItem, new SeparatorMenuItem(), exitMenuItem);
        MenuBar menuBar = new MenuBar(fileMenu);
        VBox topContainer = new VBox(menuBar);
        root.setTop(topContainer);
        HBox panesContainer = new HBox(10);
        panesContainer.setPadding(new Insets(10));
        panesContainer.setAlignment(Pos.TOP_CENTER);
        panesContainer.getChildren().addAll(bubbleSortPane, mergeSortPane, quickSortPane);
        root.setCenter(panesContainer);
        this.startButton = new Button("Старт");
        this.generateRandomArrayButton = new Button("Сгенерировать массив");
        this.delaySlider = new Slider(1, 500, 5);
        this.delaySlider.setShowTickLabels(true);
        this.delaySlider.setShowTickMarks(true);
        this.delaySlider.setMajorTickUnit(100);
        this.delaySlider.setMinorTickCount(4);
        this.delaySlider.setBlockIncrement(5);
        this.delayValueLabel = new Label(Integer.toString((int) delaySlider.getValue()));
        this.delayTextField = new TextField(Integer.toString((int) delaySlider.getValue()));
        this.delayTextField.setPrefWidth(70);
        this.arraySizeTextField = new TextField("30");
        this.arraySizeTextField.setPrefWidth(60);
        this.arrayInfoLabel = new Label("Массив не загружен");
        configureBottomPanel();
    }

    private void configureBottomPanel() {
        HBox bottomPanel = new HBox(10);
        bottomPanel.setPadding(new Insets(10));
        bottomPanel.setAlignment(Pos.CENTER_LEFT);
        Label delayLabel = new Label("Задержка (мс):");
        Label arraySizeLabel = new Label("Размер массива:");
        bottomPanel.getChildren().addAll(
                delayLabel,
                delaySlider,
                delayValueLabel,
                delayTextField,
                new Separator(),
                arraySizeLabel,
                arraySizeTextField,
                generateRandomArrayButton,
                new Separator(),
                startButton,
                new Separator(),
                arrayInfoLabel
        );
        HBox.setHgrow(arrayInfoLabel, Priority.ALWAYS);
        arrayInfoLabel.setMaxWidth(Double.MAX_VALUE);
        root.setBottom(bottomPanel);
        delaySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int value = newValue.intValue();
            delayValueLabel.setText(Integer.toString(value));
            String currentText = delayTextField.getText();
            String newText = Integer.toString(value);
            if (!newText.equals(currentText)) {
                delayTextField.setText(newText);
            }
        });
        delayTextField.textProperty().addListener((observable, oldText, newText) -> {
            if (newText == null || newText.isBlank()) {
                return;
            }
            try {
                int value = Integer.parseInt(newText.trim());
                double min = delaySlider.getMin();
                double max = delaySlider.getMax();
                if (value < min) {
                    value = (int) min;
                }
                if (value > max) {
                    value = (int) max;
                }
                if (delaySlider.getValue() != value) {
                    delaySlider.setValue(value);
                }
            } catch (NumberFormatException ignored) {
            }
        });
    }

    public BorderPane getRoot() {
        return root;
    }

    public SortingPane getBubbleSortPane() {
        return bubbleSortPane;
    }

    public SortingPane getMergeSortPane() {
        return mergeSortPane;
    }

    public SortingPane getQuickSortPane() {
        return quickSortPane;
    }

    public MenuItem getOpenFileMenuItem() {
        return openFileMenuItem;
    }

    public MenuItem getExitMenuItem() {
        return exitMenuItem;
    }

    public Button getStartButton() {
        return startButton;
    }

    public Button getGenerateRandomArrayButton() {
        return generateRandomArrayButton;
    }

    public Slider getDelaySlider() {
        return delaySlider;
    }

    public Label getDelayValueLabel() {
        return delayValueLabel;
    }

    public TextField getDelayTextField() {
        return delayTextField;
    }

    public TextField getArraySizeTextField() {
        return arraySizeTextField;
    }

    public Label getArrayInfoLabel() {
        return arrayInfoLabel;
    }
}