package org.example.sortapp.service;

public class QuickSortAlgorithm implements SortingAlgorithm {

    @Override
    public void sort(int[] array,
                     SortingStepListener stepListener,
                     DelayProvider delayProvider) throws InterruptedException {
        quickSort(array, 0, array.length - 1, stepListener, delayProvider);
        stepListener.onStep(array);
    }

    private void quickSort(int[] array,
                           int low,
                           int high,
                           SortingStepListener stepListener,
                           DelayProvider delayProvider) throws InterruptedException {
        if (low >= high) {
            return;
        }

        int pivotIndex = partition(array, low, high, stepListener, delayProvider);
        quickSort(array, low, pivotIndex - 1, stepListener, delayProvider);
        quickSort(array, pivotIndex + 1, high, stepListener, delayProvider);
    }

    private int partition(int[] array,
                          int low,
                          int high,
                          SortingStepListener stepListener,
                          DelayProvider delayProvider) throws InterruptedException {
        int mid = low + (high - low) / 2;
        int pivot = array[mid];

        swap(array, mid, high);
        stepListener.onStep(array);
        long initialDelay = Math.max(1L, delayProvider.getDelayMillis());
        Thread.sleep(initialDelay);

        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (array[j] <= pivot) {
                i++;
                if (i != j) {
                    swap(array, i, j);
                    stepListener.onStep(array);
                    long delay = Math.max(1L, delayProvider.getDelayMillis());
                    Thread.sleep(delay);
                }
            }
        }

        swap(array, i + 1, high);
        stepListener.onStep(array);
        long delay = Math.max(1L, delayProvider.getDelayMillis());
        Thread.sleep(delay);

        return i + 1;
    }

    private void swap(int[] array, int indexA, int indexB) {
        if (indexA == indexB) {
            return;
        }
        int temp = array[indexA];
        array[indexA] = array[indexB];
        array[indexB] = temp;
    }
}
