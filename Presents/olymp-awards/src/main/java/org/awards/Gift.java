package org.awards;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public class Gift {
    private final String name;
    private final int price;

    public Gift(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String name() { return name; }
    public int price() { return price; }

    @Override
    public String toString() {
        NumberFormat ru = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));
        return name + " — " + ru.format(price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gift gift = (Gift) o;
        return price == gift.price && Objects.equals(name, gift.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }
}
