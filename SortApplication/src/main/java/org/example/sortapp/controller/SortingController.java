package org.example.sortapp.controller;

import org.example.sortapp.service.BubbleSortAlgorithm;
import org.example.sortapp.service.DelayProvider;
import org.example.sortapp.service.MergeSortAlgorithm;
import org.example.sortapp.service.QuickSortAlgorithm;
import org.example.sortapp.view.SortingView;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SortingController {

    private final Stage primaryStage;
    private final SortingView view;

    private final AtomicInteger delayMillis = new AtomicInteger(5);
    private final AtomicBoolean sortingInProgress = new AtomicBoolean(false);
    private final AtomicInteger runningTasks = new AtomicInteger(0);

    private int[] currentArray;

    public SortingController(Stage primaryStage, SortingView view) {
        this.primaryStage = primaryStage;
        this.view = view;
    }

    public void initialize() {
        configureDelayBinding();
        configureMenuHandlers();
        configureButtonHandlers();
        currentArray = null;
        updateArrayInView();
        updateStartButtonState();
    }

    private void configureDelayBinding() {
        delayMillis.set((int) view.getDelaySlider().getValue());
        view.getDelaySlider().valueProperty().addListener((observable, oldValue, newValue) ->
                delayMillis.set(newValue.intValue()));
    }

    private void configureMenuHandlers() {
        view.getOpenFileMenuItem().setOnAction(event -> handleOpenFile());
        view.getExitMenuItem().setOnAction(event -> primaryStage.close());
    }

    private void configureButtonHandlers() {
        view.getGenerateRandomArrayButton().setOnAction(event -> handleGenerateRandomArray());
        view.getStartButton().setOnAction(event -> handleStartSorting());
    }

    private void handleOpenFile() {
        if (sortingInProgress.get()) {
            showInformation("Сортировка выполняется",
                    "Дождитесь окончания сортировки перед загрузкой нового массива.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите текстовый файл с массивом");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt")
        );
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file == null) {
            return;
        }
        try {
            int[] loadedArray = readArrayFromFile(file.toPath());
            if (loadedArray.length == 0) {
                showInformation("Файл пустой", "В файле не найдено ни одного числа.");
                return;
            }
            this.currentArray = loadedArray;
            updateArrayInView();
            view.getArrayInfoLabel().setText(
                    "Массив из файла: " + file.getName() + " (" + currentArray.length + " элементов)"
            );
        }
        catch (IOException exception) {
            showError("Ошибка чтения файла",
                    "Произошла ошибка при чтении файла: " + exception.getMessage(),
                    exception);
        }
        catch (NumberFormatException exception) {
            showError("Ошибка формата файла",
                    "Не удалось разобрать некоторые числа в файле: " + exception.getMessage(),
                    exception);
        }
    }

    private void handleGenerateRandomArray() {
        if (sortingInProgress.get()) {
            showInformation("Сортировка выполняется",
                    "Дождитесь окончания сортировки перед генерацией нового массива.");
            return;
        }
        String sizeText = view.getArraySizeTextField().getText().trim();
        int size;
        try {
            size = Integer.parseInt(sizeText);
        } catch (NumberFormatException exception) {
            showInformation("Некорректный размер массива",
                    "Размер массива должен быть целым числом (например, 20 или 50).");
            return;
        }
        if (size <= 0) {
            showInformation("Некорректный размер массива",
                    "Размер массива должен быть положительным.");
            return;
        }
        if (size > 300) {
            showInformation("Слишком большой массив",
                    "Для наглядной визуализации лучше использовать не более 300 элементов.");
            return;
        }
        Random random = new Random();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(201) - 100;
        }
        this.currentArray = array;
        updateArrayInView();
        view.getArrayInfoLabel().setText("Случайный массив (" + size + " элементов)");
    }

    private void handleStartSorting() {
        if (currentArray == null || currentArray.length == 0) {
            showInformation("Нет данных",
                    "Сначала загрузите массив из файла или сгенерируйте его.");
            return;
        }
        if (sortingInProgress.get()) {
            return;
        }
        sortingInProgress.set(true);
        runningTasks.set(3);
        updateStartButtonState();
        view.getGenerateRandomArrayButton().setDisable(true);
        view.getOpenFileMenuItem().setDisable(true);
        view.getArraySizeTextField().setDisable(true);
        view.getBubbleSortPane().resetStatistics();
        view.getMergeSortPane().resetStatistics();
        view.getQuickSortPane().resetStatistics();
        updateArrayInView();
        DelayProvider delayProvider = () -> Math.max(1L, delayMillis.get());
        Runnable taskFinishedCallback = () -> {
            int left = runningTasks.decrementAndGet();
            if (left == 0) {
                Platform.runLater(this::onAllSortingFinished);
            }
        };
        SortingTask bubbleTask = new SortingTask(
                currentArray,
                new BubbleSortAlgorithm(),
                view.getBubbleSortPane(),
                delayProvider,
                taskFinishedCallback
        );
        SortingTask mergeTask = new SortingTask(
                currentArray,
                new MergeSortAlgorithm(),
                view.getMergeSortPane(),
                delayProvider,
                taskFinishedCallback
        );
        SortingTask quickTask = new SortingTask(
                currentArray,
                new QuickSortAlgorithm(),
                view.getQuickSortPane(),
                delayProvider,
                taskFinishedCallback
        );
        Thread bubbleThread = new Thread(bubbleTask, "BubbleSortThread");
        Thread mergeThread = new Thread(mergeTask, "MergeSortThread");
        Thread quickThread = new Thread(quickTask, "QuickSortThread");
        bubbleThread.setDaemon(true);
        mergeThread.setDaemon(true);
        quickThread.setDaemon(true);
        bubbleThread.start();
        mergeThread.start();
        quickThread.start();
    }

    private void onAllSortingFinished() {
        sortingInProgress.set(false);
        view.getGenerateRandomArrayButton().setDisable(false);
        view.getOpenFileMenuItem().setDisable(false);
        view.getArraySizeTextField().setDisable(false);
        updateStartButtonState();
    }

    private void updateArrayInView() {
        int[] snapshot = (currentArray != null) ? currentArray.clone() : new int[0];
        view.getBubbleSortPane().renderArray(snapshot);
        view.getMergeSortPane().renderArray(snapshot);
        view.getQuickSortPane().renderArray(snapshot);
        view.getBubbleSortPane().resetStatistics();
        view.getMergeSortPane().resetStatistics();
        view.getQuickSortPane().resetStatistics();
        updateStartButtonState();
    }

    private void updateStartButtonState() {
        boolean disabled = sortingInProgress.get()
                || currentArray == null
                || currentArray.length == 0;
        view.getStartButton().setDisable(disabled);
    }

    private int[] readArrayFromFile(Path filePath) throws IOException {
        List<Integer> numbers = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                String[] tokens = trimmed.split("[,;\\s]+");
                for (String token : tokens) {
                    if (token.isBlank()) {
                        continue;
                    }
                    int value = Integer.parseInt(token);
                    numbers.add(value);
                }
            }
        }
        int[] result = new int[numbers.size()];
        for (int i = 0; i < numbers.size(); i++) {
            result[i] = numbers.get(i);
        }
        return result;
    }

    private void showError(String title, String message, Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(primaryStage);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message + "\n\n" + exception.getClass().getSimpleName());
        alert.showAndWait();
    }

    private void showInformation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(primaryStage);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
