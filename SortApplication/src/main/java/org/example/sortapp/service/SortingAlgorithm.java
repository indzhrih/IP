package org.example.sortapp.service;

public interface SortingAlgorithm {

    void sort(int[] array,
              SortingStepListener stepListener,
              DelayProvider delayProvider) throws InterruptedException;
}
