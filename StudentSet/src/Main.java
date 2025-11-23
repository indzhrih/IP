import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;

public class Main {
    public void inputFromFile(String inputFileName, HashSet<Student> students) throws IOException {
        String line;

        BufferedReader br = Files.newBufferedReader(Path.of(inputFileName));
        while ((line = br.readLine()) != null) {
            if (line.isBlank()) continue;

            String[] studentString = Arrays.stream(line.split(";", -1)).map(String::trim).toArray(String[]::new);
            Student stud = new Student(Long.parseLong(studentString[0]),
                    studentString[1],
                    Integer.parseInt(studentString[2]),
                    Double.parseDouble(studentString[3]));

            students.add(stud);
        }
        br.close();
    }

    public static void main(String[] args) {
        HashSet<Student> studentsA = new HashSet<Student>();
        HashSet<Student> studentsB = new HashSet<Student>();

        Main main = new Main();

        try {
            main.inputFromFile("studentsA.txt", studentsA);
            main.inputFromFile("studentsB.txt", studentsB);

            System.out.println("Множество A:");
            studentsA.forEach(System.out::println);

            System.out.println("\nМножество B:");
            studentsB.forEach(System.out::println);

            HashSet<Student> union = StudentSet.union(studentsA, studentsB);
            System.out.println("\nОбъединение A и B:");
            union.forEach(System.out::println);
            StudentSet.writeToFile(union, "union.txt");

            HashSet<Student> intersection = StudentSet.intersection(studentsA, studentsB);
            System.out.println("\nПересечение A и B:");
            intersection.forEach(System.out::println);
            StudentSet.writeToFile(intersection, "intersection.txt");

            HashSet<Student> differenceAB = StudentSet.difference(studentsA, studentsB);
            System.out.println("\nРазность A - B:");
            differenceAB.forEach(System.out::println);
            StudentSet.writeToFile(differenceAB, "differenceAB.txt");

            HashSet<Student> differenceBA = StudentSet.difference(studentsB, studentsA);
            System.out.println("\nРазность B - A:");
            differenceBA.forEach(System.out::println);
            StudentSet.writeToFile(differenceBA, "differenceBA.txt");

            System.out.println("\nРезультаты сохранены в файлы:");
            System.out.println("- union.txt (объединение)");
            System.out.println("- intersection.txt (пересечение)");
            System.out.println("- differenceAB.txt (разность A-B)");
            System.out.println("- differenceBA.txt (разность B-A)");

        }
        catch (IOException e) {
            System.err.println("Ошибка при работе с файлами: " + e.getMessage());
        }
        catch (Exception e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
        }
    }
}
