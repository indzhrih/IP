package main.java.com.famcs.gradebook;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nGradeBook System");
            System.out.println("1. Read from JSON and display");
            System.out.println("2. Export to JSON");
            System.out.println("3. Export to TXT");
            System.out.println("4. Import from TXT to JSON");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");

            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        readAndDisplayJson(scanner);
                        break;
                    case "2":
                        exportToJson(scanner);
                        break;
                    case "3":
                        exportToTxt(scanner);
                        break;
                    case "4":
                        importFromTxtToJson(scanner);
                        break;
                    case "5":
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid option!");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void readAndDisplayJson(Scanner scanner) throws Exception {
        System.out.print("Enter JSON file path (default: students.json): ");
        String path = scanner.nextLine();
        if (path.trim().isEmpty()) {
            path = "students.json";
        }

        List<GradeBook> students = GradeBook.readFromJson(Path.of(path));
        System.out.println("Loaded " + students.size() + " students:");

        for (GradeBook student : students) {
            System.out.println(student.toFormattedString());
        }
    }

    private static void exportToJson(Scanner scanner) throws Exception {
        System.out.print("Enter input JSON file path (default: students.json): ");
        String inputPath = scanner.nextLine();
        if (inputPath.trim().isEmpty()) {
            inputPath = "students.json";
        }

        System.out.print("Enter output JSON file path (default: output.json): ");
        String outputPath = scanner.nextLine();
        if (outputPath.trim().isEmpty()) {
            outputPath = "output.json";
        }

        List<GradeBook> students = GradeBook.readFromJson(Path.of(inputPath));
        GradeBook.writeToJson(students, Path.of(outputPath));
        System.out.println("Data exported to JSON: " + outputPath);
    }

    private static void exportToTxt(Scanner scanner) throws Exception {
        System.out.print("Enter input JSON file path (default: students.json): ");
        String inputPath = scanner.nextLine();
        if (inputPath.trim().isEmpty()) {
            inputPath = "students.json";
        }

        System.out.print("Enter output TXT file path (default: output.txt): ");
        String outputPath = scanner.nextLine();
        if (outputPath.trim().isEmpty()) {
            outputPath = "output.txt";
        }

        List<GradeBook> students = GradeBook.readFromJson(Path.of(inputPath));
        GradeBook.writeToTxt(students, Path.of(outputPath));
        System.out.println("Data exported to TXT: " + outputPath);
    }

    private static void importFromTxtToJson(Scanner scanner) throws Exception {
        System.out.print("Enter input TXT file path (default: input.txt): ");
        String inputPath = scanner.nextLine();
        if (inputPath.trim().isEmpty()) {
            inputPath = "input.txt";
        }

        System.out.print("Enter output JSON file path (default: imported.json): ");
        String outputPath = scanner.nextLine();
        if (outputPath.trim().isEmpty()) {
            outputPath = "imported.json";
        }

        List<GradeBook> students = GradeBook.readFromTxt(Path.of(inputPath));
        GradeBook.writeToJson(students, Path.of(outputPath));
        System.out.println("Data imported from TXT to JSON: " + outputPath);
    }
}
