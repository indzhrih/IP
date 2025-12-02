package org.example.sortapp.view;

import org.example.sortapp.model.SortStatistics;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class SortingPane extends VBox {

    private final String algorithmDisplayName;
    private final Label titleLabel;
    private final Canvas canvas;
    private final Label startedAtLabel;
    private final Label finishedAtLabel;
    private final Label durationLabel;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public SortingPane(String algorithmDisplayName) {
        this.algorithmDisplayName = algorithmDisplayName;
        this.titleLabel = new Label(algorithmDisplayName);
        this.canvas = new Canvas(350, 250);
        this.startedAtLabel = new Label();
        this.finishedAtLabel = new Label();
        this.durationLabel = new Label();
        configureLayout();
        resetStatistics();
    }

    private void configureLayout() {
        setSpacing(5);
        setPadding(new Insets(10));
        setAlignment(Pos.TOP_CENTER);
        setPrefWidth(380);
        titleLabel.setFont(Font.font(16));
        titleLabel.setStyle("-fx-font-weight: bold;");
        getChildren().addAll(
                titleLabel,
                canvas,
                startedAtLabel,
                finishedAtLabel,
                durationLabel
        );
    }

    public void renderArray(int[] values) {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.fillRect(0, 0, width, height);
        graphicsContext.setStroke(Color.GRAY);
        graphicsContext.strokeRect(0, 0, width, height);
        if (values == null || values.length == 0) {
            return;
        }

        int minValue = Integer.MAX_VALUE;
        int maxValue = Integer.MIN_VALUE;
        for (int value : values) {
            if (value < minValue) {
                minValue = value;
            }
            if (value > maxValue) {
                maxValue = value;
            }
        }

        if (minValue == Integer.MAX_VALUE) {
            return;
        }

        double barWidth = width / values.length;
        double padding = 10;
        double minBarHeight = 3;

        graphicsContext.setFill(Color.LIMEGREEN);

        if (minValue == maxValue) {
            double barHeight = height - padding;
            double y = height - barHeight;
            for (int i = 0; i < values.length; i++) {
                double x = i * barWidth;
                graphicsContext.fillRect(x, y, barWidth - 1, barHeight);
            }
            return;
        }

        double range = maxValue - minValue;

        for (int i = 0; i < values.length; i++) {
            double normalized = (values[i] - minValue) / range;
            double barHeight = minBarHeight + normalized * (height - padding - minBarHeight);
            double x = i * barWidth;
            double y = height - barHeight;
            graphicsContext.fillRect(x, y, barWidth - 1, barHeight);
        }
    }

    public void resetStatistics() {
        startedAtLabel.setText("Начал работу: -");
        finishedAtLabel.setText("Окончил работу: -");
        durationLabel.setText("Длительность: -");
    }

    public void updateStatistics(SortStatistics statistics) {
        if (statistics.getStartedAt() != null) {
            startedAtLabel.setText(
                    "Начал работу: " + timeFormatter.format(statistics.getStartedAt())
            );
        }
        if (statistics.getFinishedAt() != null) {
            finishedAtLabel.setText(
                    "Окончил работу: " + timeFormatter.format(statistics.getFinishedAt())
            );
            Duration duration = statistics.getDuration();
            durationLabel.setText("Длительность: " + duration.toMillis() + " мс");
        }
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
