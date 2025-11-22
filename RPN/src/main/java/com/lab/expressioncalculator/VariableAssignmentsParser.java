package main.java.com.lab.expressioncalculator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class VariableAssignmentsParser {

    public Map<String, BigDecimal> parse(String assignmentsText) {
        Map<String, BigDecimal> result = new HashMap<>();
        if (assignmentsText == null || assignmentsText.isBlank()) return result;
        String[] lines = assignmentsText.split("\\R");
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()) continue;

            String[] parts = trimmedLine.split("=");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Incorrect line with variable: " + line);
            }
            String name = parts[0].trim();
            String valueText = parts[1].trim().replace(',', '.');
            if (!name.matches("[a-zA-Z][a-zA-Z0-9_]*")) {
                throw new IllegalArgumentException("Incorrect variable name: " + name);
            }
            if (valueText.isEmpty()) {
                throw new IllegalArgumentException("Don't have a value for this variable: " + name);
            }
            BigDecimal value;
            try {
                value = new BigDecimal(valueText);
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Incorrect variable value " + name + ": " + valueText);
            }
            result.put(name, value);
        }
        return result;
    }
}
