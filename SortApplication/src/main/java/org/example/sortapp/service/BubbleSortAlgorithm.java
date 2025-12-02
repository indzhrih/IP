package org.example.sortapp.service;

public class BubbleSortAlgorithm implements SortingAlgorithm {

    @Override
    public void sort(int[] array,
                     SortingStepListener stepListener,
                     DelayProvider delayProvider) throws InterruptedException {

        int n = array.length;
        boolean swapped;

        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    swapped = true;
                }
                stepListener.onStep(array);
                long delay = Math.max(1L, delayProvider.getDelayMillis());
                Thread.sleep(delay);
            }
            if (!swapped) {
                break;
            }
        }

        stepListener.onStep(array);
    }
}
