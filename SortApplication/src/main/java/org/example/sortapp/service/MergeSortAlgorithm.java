package org.example.sortapp.service;

public class MergeSortAlgorithm implements SortingAlgorithm {

    @Override
    public void sort(int[] array,
                     SortingStepListener stepListener,
                     DelayProvider delayProvider) throws InterruptedException {

        if (array.length <= 1) {
            stepListener.onStep(array);
            return;
        }

        int[] temp = new int[array.length];
        mergeSort(array, temp, 0, array.length - 1, stepListener, delayProvider);
        stepListener.onStep(array);
    }

    private void mergeSort(int[] array,
                           int[] temp,
                           int left,
                           int right,
                           SortingStepListener stepListener,
                           DelayProvider delayProvider) throws InterruptedException {

        if (left >= right) {
            return;
        }

        int middle = (left + right) / 2;
        mergeSort(array, temp, left, middle, stepListener, delayProvider);
        mergeSort(array, temp, middle + 1, right, stepListener, delayProvider);
        merge(array, temp, left, middle, right, stepListener, delayProvider);
    }

    private void merge(int[] array,
                       int[] temp,
                       int left,
                       int middle,
                       int right,
                       SortingStepListener stepListener,
                       DelayProvider delayProvider) throws InterruptedException {

        System.arraycopy(array, left, temp, left, right - left + 1);

        int i = left;
        int j = middle + 1;
        int k = left;

        while (i <= middle && j <= right) {
            if (temp[i] <= temp[j]) {
                array[k] = temp[i];
                i++;
            } else {
                array[k] = temp[j];
                j++;
            }
            stepListener.onStep(array);
            long delay = Math.max(1L, delayProvider.getDelayMillis());
            Thread.sleep(delay);
            k++;
        }

        while (i <= middle) {
            array[k] = temp[i];
            i++;
            k++;
            stepListener.onStep(array);
            long delay = Math.max(1L, delayProvider.getDelayMillis());
            Thread.sleep(delay);
        }

        while (j <= right) {
            array[k] = temp[j];
            j++;
            k++;
            stepListener.onStep(array);
            long delay = Math.max(1L, delayProvider.getDelayMillis());
            Thread.sleep(delay);
        }
    }
}
