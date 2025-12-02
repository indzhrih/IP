package main.java.com.example.studentmanagement.model;

public enum StudentSortOption {

    BY_SEQUENCE_NUMBER("По номеру по порядку"),
    BY_FULL_NAME("По ФИО (по алфавиту)"),
    BY_GRADE_BOOK_NUMBER("По номеру зачётки"),
    BY_AVERAGE_GRADE("По среднему баллу");

    private final String displayName;

    StudentSortOption(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
