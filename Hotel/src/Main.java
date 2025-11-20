import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        HotelsCollection hotelsCollection = new HotelsCollection();

        try {
            Scanner scanner = new Scanner(System.in);
            hotelsCollection.readFile();

            System.out.println("Сортировка по городу и звездам: ");
            hotelsCollection.displayAllSortedByCityAndStars();

            String userGuess;
            System.out.println("Введите название города ");
            userGuess = scanner.nextLine();
            System.out.println("Вывод отелей в городе: ");
            hotelsCollection.displayHotelsByCity(userGuess);
//           hotelsCollection.displayHotelsByCity("Санкт-Петербург");
//           hotelsCollection.displayHotelsByCity("Париж");

            System.out.println("Введите название Отеля ");
            userGuess = scanner.nextLine();
            System.out.println("Вывод городов по названию отеля");
            hotelsCollection.displayCitiesByHotelName(userGuess);

        }
        catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

}