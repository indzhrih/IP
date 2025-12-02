package org.example.sortapp.service;

@FunctionalInterface
public interface SortingStepListener {
    void onStep(int[] currentArray);
}
