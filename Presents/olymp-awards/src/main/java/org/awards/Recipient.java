package org.awards;

import java.util.Objects;

public class Recipient {
    private final String id;
    private final String fullName;
    private final String category;

    public Recipient(String id, String fullName, String category) {
        this.id = notBlank(id, "id");
        this.fullName = notBlank(fullName, "fullName");
        this.category = category == null ? "" : category.trim();
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getCategory() { return category; }

    @Override public String toString() {
        return category.isEmpty() ? fullName : fullName + " (" + category + ")";
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recipient)) return false;
        Recipient that = (Recipient) o;
        return id.equals(that.id);
    }

    @Override public int hashCode() { return Objects.hash(id); }

    private static String notBlank(String s, String field) {
        if (s == null || s.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipient: " + field + " is blank");
        }
        return s.trim();
    }
}
