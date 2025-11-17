package matrix;

import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;

public class MatrixConfigurator {
    public static int[][] configureMatrix() {
        try {
            Scanner in = new Scanner(new File("input.txt"));
            int[][] matrix;
            int rows;
            int columns;

            rows = in.nextInt();
            columns = in.nextInt();
            matrix = new int[rows][columns];

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (in.hasNextInt()) {
                        matrix[i][j] = in.nextInt();
                    }
                }
            }

            System.out.println("\nМатрица: ");
            printMatrix(matrix);

            return matrix;
        }
        catch (FileNotFoundException ex) {
            System.out.println("Ошибка " + ex + "\nФайла input.txt не найден!");
            return null;
        }
    }

    public static double[][] configureSquareMatrixDouble() {
        try {
            Scanner in = new Scanner(new File("input.txt"));
            System.out.print("Введите порядок n квадратной матрицы: ");
            int n = in.nextInt();
            double[][] m = new double[n][n];

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    m[i][j] = in.nextDouble();
                }
            }

            System.out.println("\nМатрица: ");
            printMatrix(m);

            return m;
        }
        catch (FileNotFoundException ex) {
            System.out.println("Ошибка " + ex + "\nФайла input.txt не найден!");
            return null;
        }
    }

    public static void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.printf("%4d", matrix[i][j]);
            }
            System.out.println();
        }
    }

    public static void printMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.printf("%8.2f", matrix[i][j]);
            }
            System.out.println();
        }
    }
}
