import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class GradeBook {
    private final String lastName;
    private final String firstName;
    private final String middleName;
    private final String group;
    private final int course;
    private final Map<Integer, Session> sessions = new LinkedHashMap<>();

    public enum ReportType {
        EXCELLENT, DEBTORS, ALL;

        static ReportType from(String v) {
            return switch (v.trim().toLowerCase(Locale.ROOT)) {
                case "excellent", "best" -> EXCELLENT;
                case "debtors", "debt", "bad" -> DEBTORS;
                case "all" -> ALL;
                default -> throw new IllegalArgumentException("Неизвестный режим: " + v);
            };
        }
    }

    public static void process(Path input, Path output, ReportType mode) throws IOException {
        Collection<GradeBook> books = readBooks(input).values();
        writeReport(books, output, mode);
    }

    private GradeBook(String lastName, String firstName, String middleName, int course, String group) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.course = course;
        this.group = group;
    }

    private Session session(int n) { return sessions.computeIfAbsent(n, Session::new); }

    private static Map<String, GradeBook> readBooks(Path file) throws IOException {
        Map<String, GradeBook> books = new LinkedHashMap<>();

        try (BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] p = Arrays.stream(line.split(";", -1)).map(String::trim).toArray(String[]::new);
                if (p.length < 9) continue;

                String lastName = p[0],
                        firstName = p[1],
                        middleName = p[2],
                        group = p[4],
                        subject = p[7];

                Integer course = Integer.parseInt((p[3]).trim()),
                        sessNo = Integer.parseInt(p[5].trim());

                String kind = p[6].trim().toUpperCase(Locale.ROOT),
                        val = p[8];

                if (invalid(lastName, firstName, middleName, group, subject, course, sessNo)) continue;

                GradeBook b = books.computeIfAbsent(
                        (lastName + '|' + firstName + '|' + middleName +'|' + course + '|' + group).toLowerCase(Locale.ROOT),
                        k -> new GradeBook(lastName, firstName, middleName, course, group)
                );
                Session session = b.session(sessNo);

                switch (kind) {
                    case "EXAM" -> { Integer grade = Integer.parseInt(val);
                        if (grade != null && grade <= 10) session.addExam(subject,grade);
                    }
                    case "CREDIT" -> session.addCredit(subject, isPassed(val));
                }
            }
        }
        return books;
    }

    private static void writeReport(Collection<GradeBook> books, Path out, ReportType mode) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(out, StandardCharsets.UTF_8)) {
            bw.write("Фамилия;Имя;Отчество;Курс;Группа;Сессия;Тип;Предмет;Результат");
            bw.newLine();

            List<String[]> perStudentAvg = new ArrayList<>();

            for (GradeBook b : books) {
                boolean wroteForStudent = false;

                long studSum = 0;
                long studCount = 0;

                for (Session s : b.sessions.values()) {
                    boolean takeSession =
                            mode == ReportType.ALL ||
                                    (mode == ReportType.EXCELLENT && s.isExcellent()) ||
                                    (mode == ReportType.DEBTORS && s.isHasDebt());
                    if (!takeSession) continue;

                    for (int g : s.exams.values()) { studSum += g; studCount++; }

                    for (var ex : s.exams.entrySet()) {
                        int grade = ex.getValue();
                        if (mode == ReportType.DEBTORS && grade >= 4) continue;

                        bw.write(String.join(";",
                                b.lastName, b.firstName, b.middleName,
                                String.valueOf(b.course), b.group,
                                String.valueOf(s.number),
                                "Экзамен", ex.getKey(), String.valueOf(grade)
                        ));
                        bw.newLine();
                        wroteForStudent = true;
                    }

                    for (var cr : s.credits.entrySet()) {
                        boolean pass = cr.getValue();
                        if (mode == ReportType.DEBTORS && pass) continue;

                        bw.write(String.join(";",
                                b.lastName, b.firstName, b.middleName,
                                String.valueOf(b.course), b.group,
                                String.valueOf(s.number),
                                "Зачёт", cr.getKey(), pass ? "Зачёт" : "Незачёт"
                        ));
                        bw.newLine();
                        wroteForStudent = true;
                    }
                }

                if (wroteForStudent) {
                    bw.newLine();


                    String avgStr = (studCount > 0) ? String.format(Locale.ROOT, "%.2f", (double) studSum / (double) studCount) : "н/д";
                    perStudentAvg.add(new String[]{ b.lastName, b.firstName, b.middleName, avgStr });
                }
            }

            if (!perStudentAvg.isEmpty()) {
                bw.newLine();
                bw.write("Фамилия;Имя;Отчество;Средний балл");
                bw.newLine();
                for (String[] row : perStudentAvg) {
                    bw.write(String.join(";", row[0], row[1], row[2], row[3]));
                    bw.newLine();
                }
            }
        }
    }

    private static boolean isPassed(String s){
        return Set.of("PASS", "PASSED", "OK", "ЗАЧЕТ", "ЗАЧЁТ", "ДА", "YES", "Y", "ПРОЙДЕН").contains(s.trim().toUpperCase(Locale.ROOT));
    }

    private static boolean invalid(String lastName, String firstName, String middleName, String group,
                                   String subject, Integer course, Integer n) {
        return lastName.isEmpty() ||
                firstName.isEmpty() ||
                middleName.isEmpty() ||
                group.isEmpty() ||
                subject.isEmpty() ||
                course == null || course<1 ||
                n == null || n<1;
    }

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
