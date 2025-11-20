package main.java.com.famcs.gradebook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class GradeBook {
    private String lastName;
    private String firstName;
    private String middleName;
    private String group;
    private int course;
    private final Map<Integer, Session> sessions = new LinkedHashMap<>();

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    static {
        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static List<GradeBook> readFromJson(Path file) throws IOException {
        try (InputStream input = Files.newInputStream(file)) {
            List<Map<String, Object>> jsonList = jsonMapper.readValue(input, List.class);
            List<GradeBook> result = new ArrayList<>();

            for (Map<String, Object> jsonData : jsonList) {
                GradeBook student = new GradeBook(
                        (String) jsonData.get("lastName"),
                        (String) jsonData.get("firstName"),
                        (String) jsonData.get("middleName"),
                        ((Number) jsonData.get("course")).intValue(),
                        (String) jsonData.get("group")
                );

                List<Map<String, Object>> sessionsData = (List<Map<String, Object>>) jsonData.get("sessions");
                if (sessionsData != null) {
                    for (Map<String, Object> sessionData : sessionsData) {
                        Session session = student.session(((Number) sessionData.get("number")).intValue());

                        Map<String, Object> exams = (Map<String, Object>) sessionData.get("exams");
                        if (exams != null) {
                            for (Map.Entry<String, Object> exam : exams.entrySet()) {
                                session.addExam(exam.getKey(), ((Number) exam.getValue()).intValue());
                            }
                        }

                        Map<String, Object> credits = (Map<String, Object>) sessionData.get("credits");
                        if (credits != null) {
                            for (Map.Entry<String, Object> credit : credits.entrySet()) {
                                session.addCredit(credit.getKey(), (Boolean) credit.getValue());
                            }
                        }
                    }
                }
                result.add(student);
            }
            return result;
        }
    }

    public static List<GradeBook> readFromTxt(Path file) throws IOException {
        List<GradeBook> students = new ArrayList<>();
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);

        GradeBook currentStudent = null;
        Session currentSession = null;
        boolean inExams = false;
        boolean inCredits = false;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.equals("=".repeat(50))) {
                if (currentStudent != null) {
                    students.add(currentStudent);
                    currentStudent = null;
                }
                continue;
            }

            if (line.startsWith("Student: ")) {
                String namePart = line.substring(9);
                String[] names = namePart.split(" ");
                if (names.length >= 3) {
                    currentStudent = new GradeBook(names[0], names[1], names[2], 0, "");
                }
            } else if (line.startsWith("Course: ") && currentStudent != null) {
                String[] parts = line.substring(8).split(", Group: ");
                if (parts.length == 2) {
                    currentStudent.course = Integer.parseInt(parts[0].trim());
                    currentStudent.group = parts[1].trim();
                }
            } else if (line.startsWith("Session ") && currentStudent != null) {
                String sessionNum = line.substring(8).replace(":", "").trim();
                currentSession = currentStudent.session(Integer.parseInt(sessionNum));
                inExams = false;
                inCredits = false;
            } else if (line.equals("Exams:") && currentSession != null) {
                inExams = true;
                inCredits = false;
            } else if (line.equals("Credits:") && currentSession != null) {
                inExams = false;
                inCredits = true;
            } else if (inExams && currentSession != null && line.contains(":")) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String subject = parts[0].trim();
                    int grade = Integer.parseInt(parts[1].trim());
                    currentSession.addExam(subject, grade);
                }
            } else if (inCredits && currentSession != null && line.contains(":")) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String subject = parts[0].trim();
                    String result = parts[1].trim();
                    boolean pass = result.equals("PASS");
                    currentSession.addCredit(subject, pass);
                }
            }
        }

        if (currentStudent != null) {
            students.add(currentStudent);
        }

        return students;
    }

    public static void writeToJson(List<GradeBook> students, Path file) throws IOException {
        List<Map<String, Object>> jsonList = new ArrayList<>();

        for (GradeBook student : students) {
            Map<String, Object> studentData = new LinkedHashMap<>();
            studentData.put("lastName", student.lastName);
            studentData.put("firstName", student.firstName);
            studentData.put("middleName", student.middleName);
            studentData.put("course", student.course);
            studentData.put("group", student.group);

            List<Map<String, Object>> sessionsData = new ArrayList<>();
            for (Session session : student.sessions.values()) {
                Map<String, Object> sessionData = new LinkedHashMap<>();
                sessionData.put("number", session.number);
                sessionData.put("exams", new LinkedHashMap<>(session.exams));
                sessionData.put("credits", new LinkedHashMap<>(session.credits));
                sessionsData.add(sessionData);
            }
            studentData.put("sessions", sessionsData);

            jsonList.add(studentData);
        }

        try (OutputStream output = Files.newOutputStream(file)) {
            jsonMapper.writeValue(output, jsonList);
        }
    }

    public static void writeToTxt(List<GradeBook> students, Path file) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (GradeBook student : students) {
                writer.write("Student: " + student.lastName + " " + student.firstName + " " + student.middleName);
                writer.newLine();
                writer.write("Course: " + student.course + ", Group: " + student.group);
                writer.newLine();
                writer.write("Sessions:");
                writer.newLine();

                for (Session session : student.sessions.values()) {
                    writer.write("Session " + session.number + ":");
                    writer.newLine();

                    writer.write("Exams:");
                    writer.newLine();
                    for (Map.Entry<String, Integer> exam : session.exams.entrySet()) {
                        writer.write("" + exam.getKey() + ": " + exam.getValue());
                        writer.newLine();
                    }

                    writer.write("Credits:");
                    writer.newLine();
                    for (Map.Entry<String, Boolean> credit : session.credits.entrySet()) {
                        String result = credit.getValue() ? "PASS" : "NO PASS";
                        writer.write("" + credit.getKey() + ": " + result);
                        writer.newLine();
                    }
                }
                writer.newLine();
                writer.write("=".repeat(50));
                writer.newLine();
            }
        }
    }

    public String toFormattedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Student: ").append(lastName).append(" ").append(firstName).append(" ").append(middleName).append("\n");
        sb.append("Course: ").append(course).append(", Group: ").append(group).append("\n");
        sb.append("Sessions:\n");

        for (Session session : sessions.values()) {
            sb.append("Session ").append(session.number).append(":\n");

            sb.append("Exams:\n");
            for (Map.Entry<String, Integer> exam : session.exams.entrySet()) {
                sb.append("").append(exam.getKey()).append(": ").append(exam.getValue()).append("\n");
            }

            sb.append("Credits:\n");
            for (Map.Entry<String, Boolean> credit : session.credits.entrySet()) {
                String result = credit.getValue() ? "PASS" : "NO PASS";
                sb.append("").append(credit.getKey()).append(": ").append(result).append("\n");
            }
        }

        return sb.toString();
    }

    public String toJsonString() throws IOException {
        Map<String, Object> studentData = new LinkedHashMap<>();
        studentData.put("lastName", lastName);
        studentData.put("firstName", firstName);
        studentData.put("middleName", middleName);
        studentData.put("course", course);
        studentData.put("group", group);

        List<Map<String, Object>> sessionsData = new ArrayList<>();
        for (Session session : sessions.values()) {
            Map<String, Object> sessionData = new LinkedHashMap<>();
            sessionData.put("number", session.number);
            sessionData.put("exams", new LinkedHashMap<>(session.exams));
            sessionData.put("credits", new LinkedHashMap<>(session.credits));
            sessionsData.add(sessionData);
        }
        studentData.put("sessions", sessionsData);

        return jsonMapper.writeValueAsString(studentData);
    }

    public GradeBook() {
        this.lastName = "";
        this.firstName = "";
        this.middleName = "";
        this.course = 0;
        this.group = "";
    }

    public GradeBook(String lastName, String firstName, String middleName, int course, String group) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.course = course;
        this.group = group;
    }

    private Session session(int n) { return sessions.computeIfAbsent(n, Session::new); }

    public class Session {
        private final int number;
        private final Map<String,Integer> exams = new LinkedHashMap<>();
        private final Map<String,Boolean> credits = new LinkedHashMap<>();

        private Session(int n) { this.number = n; }

        void addExam(String subject, int grade) { exams.put(subject, grade); }
        void addCredit(String subject, boolean pass) { credits.put(subject, pass); }

        boolean isExcellent() {
            if (exams.isEmpty()) return false;

            for (int grade : exams.values()) {
                if (grade < 9) return false;
            }

            for (boolean isPassed : credits.values()) {
                if (!isPassed) return false;
            }

            return true;
        }

        boolean isHasDebt() {
            for (int grade : exams.values()) {
                if (grade < 4) return true;
            }

            for (boolean isPassed : credits.values()) {
                if (!isPassed) return true;
            }

            return false;
        }
    }
}
