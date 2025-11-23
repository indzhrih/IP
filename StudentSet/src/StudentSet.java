import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;

public class StudentSet {
    public static HashSet<Student> union(HashSet<Student> studentsA, HashSet<Student> studentsB) {
        HashSet<Student> result = new HashSet<>(studentsA);
        result.addAll(studentsB);
        return result;
    }

    public static HashSet<Student> intersection(HashSet<Student> studentsA, HashSet<Student> studentsB) {
        HashSet<Student> result = new HashSet<>(studentsA);
        result.retainAll(studentsB);
        return result;
    }

    public static HashSet<Student> difference(HashSet<Student> studentsA, HashSet<Student> studentsB) {
        HashSet<Student> result = new HashSet<>(studentsA);
        result.removeAll(studentsB);
        return result;
    }

    public static void writeToFile(HashSet<Student> students, String outputFileName) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(outputFileName),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Student student : students) {
                writer.write(student.toFileString());
                writer.newLine();
            }
        }
    }
}
