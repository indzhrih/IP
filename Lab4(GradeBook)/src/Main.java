import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("""
                    Использование:
                    java Main <input.txt> <output.txt> <mode>
                    где <mode> = excellent | debtors | all""");
            return;
        }

        Path input = Paths.get(args[0]);
        Path output = Paths.get(args[1]);

        try {
            GradeBook.ReportType mode = GradeBook.ReportType.from(args[2]);
            GradeBook.process(input, output, mode);
            System.out.println("Готово - смотрите " + output);
        }
        catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}
