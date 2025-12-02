package main.java.com.example.studentmanagement.repository;

import main.java.com.example.studentmanagement.model.Student;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class StudentFileRepository {

    private static final String TEXT_DELIMITER = ";";

    public List<Student> loadStudentsFromFile(Path filePath) throws IOException {
        List<Student> students = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            long lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1 && line.toLowerCase().contains("номер")) {
                    continue;
                }
                if (line.isBlank()) {
                    continue;
                }
                Student student = parseStudentLine(line, lineNumber);
                students.add(student);
            }
        }

        return students;
    }

    public void saveStudentsToFile(Path filePath, List<Student> students) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write("Номер;НомерЗачетки;ФИО;Курс;Группа;Специальность;СреднийБал;Задолженностей");
            writer.newLine();

            for (Student student : students) {
                String line = String.join(TEXT_DELIMITER,
                        String.valueOf(student.getSequenceNumber()),
                        escapeTextValue(student.getGradeBookNumber()),
                        escapeTextValue(student.getFullName()),
                        String.valueOf(student.getCourse()),
                        escapeTextValue(student.getGroupName()),
                        escapeTextValue(student.getSpecialization()),
                        String.valueOf(student.getAverageGrade()),
                        String.valueOf(student.getDebtCount())
                );
                writer.write(line);
                writer.newLine();
            }
        }
    }

    private Student parseStudentLine(String line, long lineNumber) {
        String[] tokens = line.split(TEXT_DELIMITER, -1);

        if (tokens.length < 8) {
            throw new IllegalArgumentException(
                    "Неверное количество столбцов в строке " + lineNumber + ": " + line
            );
        }

        long sequenceNumber = parseLong(tokens[0], "Номер", lineNumber);
        String gradeBookNumber = tokens[1].trim();
        String fullName = tokens[2].trim();
        int course = (int) parseLong(tokens[3], "Курс", lineNumber);
        String groupName = tokens[4].trim();
        String specialization = tokens[5].trim();
        double averageGrade = parseDouble(tokens[6], "СреднийБал", lineNumber);
        int debtCount = (int) parseLong(tokens[7], "Задолженностей", lineNumber);

        return new Student(
                sequenceNumber,
                gradeBookNumber,
                fullName,
                course,
                groupName,
                specialization,
                averageGrade,
                debtCount
        );
    }

    private long parseLong(String value, String columnName, long lineNumber) {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Не удалось разобрать целое число в столбце '" + columnName +
                            "' в строке " + lineNumber + ": '" + value + "'", exception
            );
        }
    }

    private double parseDouble(String value, String columnName, long lineNumber) {
        String normalizedValue = value.trim().replace(',', '.');
        try {
            return Double.parseDouble(normalizedValue);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Не удалось разобрать число с плавающей точкой в столбце '" + columnName +
                            "' в строке " + lineNumber + ": '" + value + "'", exception
            );
        }
    }

    private String escapeTextValue(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(TEXT_DELIMITER) || value.contains("\"")) {
            String escaped = value.replace("\"", "\"\"");
            return "\"" + escaped + "\"";
        }
        return value;
    }
}
