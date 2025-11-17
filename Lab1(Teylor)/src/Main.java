import teylorpackage.Teylor;

import java.text.NumberFormat;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMaximumFractionDigits(7);

        Scanner in = new Scanner(System.in);
        double finalValue;
        int k;
        double x;

        System.out.println("Введите k ");
        k = in.nextInt();
        System.out.println("Введите x ");
        x = in.nextDouble();

        System.out.println("Ряд Тейлора для экспоненты: ");
        finalValue = Teylor.countTeylorExp(k, x);

        System.out.println("\nВаше значение экспоненты с округлением до трех знаков: "
                + formatter.format(finalValue));
        System.out.println("Значение экспоненты из библиотеки Math "
                + formatter.format(Math.exp(x)));
    }
}
