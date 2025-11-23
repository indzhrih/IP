package org.awards;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipientRepository {
    private final List<Recipient> items;

    private RecipientRepository(List<Recipient> items) {
        this.items = List.copyOf(items);
    }

    public static RecipientRepository fromResource(String resourcePath, char delimiter) {
        List<String[]> rows = TextTableReader.readFromResource(resourcePath, delimiter, true);
        return new RecipientRepository(parse(rows));
    }

    public static RecipientRepository fromFile(Path path, char delimiter) {
        List<String[]> rows = TextTableReader.readFromFile(path, delimiter, true);
        return new RecipientRepository(parse(rows));
    }

    private static List<Recipient> parse(List<String[]> rows) {
        List<Recipient> list = new ArrayList<>();
        for (String[] r : rows) {
            String id = at(r, 0);
            String fullName = at(r, 1);
            String category = at(r, 2);
            list.add(new Recipient(id, fullName, category));
        }
        return list;
    }

    private static String at(String[] arr, int idx) { return idx < arr.length ? arr[idx] : ""; }

    public List<Recipient> findAll() { return Collections.unmodifiableList(items); }
}
