package org.awards;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GreeterRepository {
    private final List<Greeter> items;

    private GreeterRepository(List<Greeter> items) {
        this.items = List.copyOf(items);
    }

    public static GreeterRepository fromResource(String resourcePath, char delimiter) {
        List<String[]> rows = TextTableReader.readFromResource(resourcePath, delimiter, true);
        return new GreeterRepository(parse(rows));
    }

    public static GreeterRepository fromFile(Path path, char delimiter) {
        List<String[]> rows = TextTableReader.readFromFile(path, delimiter, true);
        return new GreeterRepository(parse(rows));
    }

    private static List<Greeter> parse(List<String[]> rows) {
        List<Greeter> list = new ArrayList<>();
        for (String[] r : rows) {
            String id = at(r, 0);
            String name = at(r, 1);
            String role = at(r, 2);
            int fee = parseIntSafe(at(r, 3));
            list.add(new Greeter(id, name, role, fee));
        }
        return list;
    }

    private static String at(String[] arr, int idx) { return idx < arr.length ? arr[idx] : ""; }

    private static int parseIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

    public List<Greeter> findAll() { return Collections.unmodifiableList(items); }
}
