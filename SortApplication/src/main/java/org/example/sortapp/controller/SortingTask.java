package org.example.sortapp.controller;

import org.example.sortapp.model.SortStatistics;
import org.example.sortapp.service.DelayProvider;
import org.example.sortapp.service.SortingAlgorithm;
import org.example.sortapp.service.SortingStepListener;
import org.example.sortapp.view.SortingPane;
import javafx.application.Platform;

import java.time.LocalDateTime;
import java.util.Arrays;

public class SortingTask implements Runnable {

    private final int[] originalArray;
    private final SortingAlgorithm sortingAlgorithm;
    private final SortingPane sortingPane;
    private final DelayProvider delayProvider;
    private final Runnable finishedCallback;

    public SortingTask(int[] originalArray,
                       SortingAlgorithm sortingAlgorithm,
                       SortingPane sortingPane,
                       DelayProvider delayProvider,
                       Runnable finishedCallback) {
        this.originalArray = originalArray;
        this.sortingAlgorithm = sortingAlgorithm;
        this.sortingPane = sortingPane;
        this.delayProvider = delayProvider;
        this.finishedCallback = finishedCallback;
    }

    @Override
    public void run() {
        int[] workingCopy = Arrays.copyOf(originalArray, originalArray.length);

        LocalDateTime startedAt = LocalDateTime.now();

        Platform.runLater(() -> {
            sortingPane.resetStatistics();
            sortingPane.renderArray(workingCopy.clone());
            sortingPane.updateStatistics(new SortStatistics(startedAt, null));
        });

        SortingStepListener stepListener = currentArray -> {
            int[] snapshot = Arrays.copyOf(currentArray, currentArray.length);
            Platform.runLater(() -> sortingPane.renderArray(snapshot));
        };

        try {
            sortingAlgorithm.sort(workingCopy, stepListener, delayProvider);
        }
        catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
        finally {
            LocalDateTime finishedAt = LocalDateTime.now();
            SortStatistics statistics = new SortStatistics(startedAt, finishedAt);
            Platform.runLater(() -> sortingPane.updateStatistics(statistics));

            if (finishedCallback != null) {
                finishedCallback.run();
            }
        }
    }
}
