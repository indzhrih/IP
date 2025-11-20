import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class HotelsCollection {
    private List<Hotel> hotels = new ArrayList<>();

    public void readFile() throws IOException {
        try (BufferedReader br = Files.newBufferedReader(Paths.get("hotel.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] info = Arrays.stream(line.split(";", -1)).map(String::trim).toArray(String[]::new);
                if (info.length == 3) {
                    Hotel newHotel = new Hotel(info[0], info[1], Integer.parseInt(info[2]));
                    hotels.add(newHotel);
                }
            }
        }
    }

    public void displayAllSortedByCityAndStars() {
        List<Hotel> sortedHotels = new ArrayList<>(hotels);
        sortedHotels.sort(new Hotel.CityThenStarsComparator());

        sortedHotels.forEach(Hotel::printHotel);
        System.out.println();
    }

    public void displayHotelsByCity(String cityName) {
        List<Hotel> cityHotels = new ArrayList<>();

        for (Hotel hotel : hotels) {
            if (hotel.getCity().equalsIgnoreCase(cityName)) cityHotels.add(hotel);
        }

        cityHotels.sort(new Hotel.StarsComparator());

        if (cityHotels.isEmpty()) System.out.println("Отели в городе '" + cityName + "' не найдены.");
        else {
            System.out.println("Отели в городе '" + cityName + "':");
            for (Hotel hotel : cityHotels) {
                System.out.println(hotel.getCity() + " - " + hotel.getHotelName() + " - " + hotel.getStars());
            }
        }
        System.out.println();
    }

    public void displayCitiesByHotelName(String hotelName) {
        Set<String> cities = new TreeSet<>();

        for (Hotel hotel : hotels) {
            if (hotel.getHotelName().equalsIgnoreCase(hotelName)) cities.add(hotel.getCity());
        }

        if (cities.isEmpty()) System.out.println("Отели с названием '" + hotelName + "' не найдены.");
        else {
            System.out.println("Города с отелем '" + hotelName + "':");
            for (String city : cities) System.out.println(city);
        }
        System.out.println();
    }
}
