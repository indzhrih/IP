import java.util.Scanner;

public class Parser {
    private static String text = "";

    public static String correctText() {
        inputText();

        if (text == null || text.isEmpty()) {
            return text;
        }

        int i = 0;
        while (i < text.length() - 1) {
            char currentChar = text.charAt(i);
            char nextChar = text.charAt(i + 1);

            if ((currentChar == 'Р' || currentChar == 'р') &&
                    (nextChar == 'А' || nextChar == 'а')) {

                char replacement = (nextChar == 'А') ? 'О' : 'о';
                text = text.substring(0, i + 1) + replacement + text.substring(i + 2);
            }
            i++;
        }

        return text;
    }

    private static void inputText() {
        System.out.println("Введите текст: ");

        Scanner in = new Scanner(System.in);

        String line;
        line = in.nextLine();
        while(!line.isEmpty()) {
            text = text.concat(line);
            text = text.concat(" ");
            line = in.nextLine();
        }
    }
}
