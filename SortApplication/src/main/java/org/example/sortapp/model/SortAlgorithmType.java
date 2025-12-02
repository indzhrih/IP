package org.example.sortapp.model;

public enum SortAlgorithmType {

    BUBBLE_SORT("BubbleSort"),
    MERGE_SORT("MergeSort"),
    QUICK_SORT("QuickSort");

    private final String displayName;

    SortAlgorithmType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
