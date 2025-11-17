package teylorpackage;

import java.text.NumberFormat;
import java.util.Scanner;

public class Teylor {
    public static double countTeylorExp(int k, double x) {
        double epsilon = Math.pow(10, -k);
        double currentTerm = 1.0;
        double finalValue = currentTerm;

        int i = 0;
        System.out.print(currentTerm + " ");
        i++;

        while (currentTerm > epsilon) {
            currentTerm = currentTerm * x / i;
            finalValue += currentTerm;
            System.out.print(currentTerm + " ");
            i++;
        }

        return finalValue;
    }
}
