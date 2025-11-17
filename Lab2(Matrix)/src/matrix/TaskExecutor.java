package matrix;

public class TaskExecutor {
    public static void task4() {
        System.out.println("Задача 4");
        int[][] matrix = MatrixConfigurator.configureMatrix();

        int bestRow = -1;
        int bestSum = Integer.MIN_VALUE;

        for (int i = 0; i < matrix.length; i++) {
            boolean allOdd = true;
            int sumAbs = 0;

            for (int j = 0; j < matrix[i].length; j++) {
                int v = matrix[i][j];
                if (Math.abs(v) % 2 == 0) {
                    allOdd = false;
                }
                sumAbs += Math.abs(v);
            }

            if (allOdd) {
                if (sumAbs > bestSum) {
                    bestSum = sumAbs;
                    bestRow = i;
                }
            }
        }

        if (bestRow == -1) System.out.println("Нет строк, где все элементы — нечётные.");
        else {
            System.out.println("Строка с наибольшей суммой модулей эелементов среди строк с нечетными элементами: " + (bestRow + 1));
            System.out.println("Сумма модулей в этой строке: " + bestSum);
        }
    }

    public static void task24() {
        System.out.println("Задача 24");
        int[][] matrix = MatrixConfigurator.configureMatrix();

        int minAmongLocalMax = Integer.MAX_VALUE;
        int rowCoord = 0;
        int columnCoord = 0;
        boolean found = false;

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (isLocalMax(matrix, i, j)) {
                    int val = matrix[i][j];
                    if (val < minAmongLocalMax) {
                        minAmongLocalMax = val;
                        rowCoord = i;
                        columnCoord = j;
                    }
                    found = true;
                }
            }
        }

        if (!found) System.out.println("Локальные максимумы отсутствуют.");
        else {
            System.out.println("Минимум среди всех локальных максимумов: " + minAmongLocalMax);
            System.out.println("Позиция этого элемента: [" + (rowCoord + 1) + "][" + (columnCoord + 1) + "]");
        }
    }

    public static void task38() {
        System.out.println("Задача 38");
        double[][] a = MatrixConfigurator.configureSquareMatrixDouble();
        int n = a.length;

        double maxAbs = Math.abs(a[0][0]);
        int maxI = 0;
        int maxJ = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double curAbs = Math.abs(a[i][j]);
                if (curAbs > maxAbs) {
                    maxAbs = curAbs;
                    maxI = i;
                    maxJ = j;
                }
            }
        }

        System.out.println("Максимальный по модулю элемент: " + a[maxI][maxJ]);
        System.out.println("Позиция этого элемента: [" + (maxI + 1) + "][" + (maxJ + 1) + "]");

        double[][] b = new double[n - 1][n - 1];
        int r2 = 0;
        for (int i = 0; i < n; i++) {
            if (i == maxI) continue;
            int c2 = 0;
            for (int j = 0; j < n; j++) {
                if (j == maxJ) continue;
                b[r2][c2] = a[i][j];
                c2++;
            }
            r2++;
        }

        System.out.println("Полученная матрица порядка " + (n - 1) + ":");
        MatrixConfigurator.printMatrix(b);
    }

    private static boolean isLocalMax(int[][] a, int i, int j) {
        int rows = a.length;
        int cols = a[0].length;
        int val = a[i][j];

        for (int r = i - 1; r <= i + 1; r++) {
            for (int c = j - 1; c <= j + 1; c++) {
                if (r < 0 || r >= rows || c < 0 || c >= cols) continue;
                if (r == i && c == j) continue;
                if (val <= a[r][c]) return false;
            }
        }
        return true;
    }
}
