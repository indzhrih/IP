package org.awards;

import java.util.Objects;

public class Greeter {
    private final String id;
    private final String name;
    private final String role;
    private final int fee;

    public Greeter(String id, String name, String role, int fee) {
        this.id = notBlank(id, "id");
        this.name = notBlank(name, "name");
        this.role = role == null ? "" : role.trim();
        this.fee = Math.max(0, fee);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public int getFee() { return fee; }

    @Override public String toString() { return name; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Greeter)) return false;
        Greeter greeter = (Greeter) o;
        return id.equals(greeter.id);
    }

    @Override public int hashCode() { return Objects.hash(id); }

    private static String notBlank(String s, String field) {
        if (s == null || s.trim().isEmpty()) {
            throw new IllegalArgumentException("Greeter: " + field + " is blank");
        }
        return s.trim();
    }
}
