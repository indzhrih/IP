package org.awards;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class TextTableReader {
    private TextTableReader() {}

    public static List<String[]> readFromResource(String resourcePath, char delimiter, boolean headerOptional) {
        try (InputStream is = TextTableReader.class.getResourceAsStream(resourcePath)) {
            if (is == null) throw new IllegalStateException("Resource not found: " + resourcePath);
            return read(new InputStreamReader(is, StandardCharsets.UTF_8), delimiter, headerOptional);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read resource: " + resourcePath, e);
        }
    }

    public static List<String[]> readFromFile(Path path, char delimiter, boolean headerOptional) {
        try (Reader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return read(r, delimiter, headerOptional);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read file: " + path, e);
        }
    }

    private static List<String[]> read(Reader reader, char delimiter, boolean headerOptional) throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            boolean headerConsumed = false;
            while ((line = br.readLine()) != null) {
                line = stripBom(line).trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                if (!headerConsumed && headerOptional && looksLikeHeader(line, delimiter)) {
                    headerConsumed = true;
                    continue;
                }
                String[] parts = split(line, delimiter);
                rows.add(parts);
            }
        }
        return rows;
    }

    private static String stripBom(String s) {
        if (!s.isEmpty() && s.charAt(0) == '\uFEFF') return s.substring(1);
        return s;
    }

    private static boolean looksLikeHeader(String line, char delimiter) {
        String[] parts = split(line, delimiter);
        if (parts.length == 0) return false;
        String first = parts[0].trim().toLowerCase(Locale.ROOT);
        return first.matches("[a-zа-я_]+");
    }

    private static String[] split(String line, char delimiter) {
        String[] raw = line.split("\\s*\\" + delimiter + "\\s*");
        for (int i = 0; i < raw.length; i++) raw[i] = raw[i] == null ? "" : raw[i].trim();
        return raw;
    }
}
