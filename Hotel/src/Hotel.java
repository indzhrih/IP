import java.util.Comparator;

public class Hotel {
    private String city;
    private String hotelName;
    private int stars;

    public Hotel() {
        this.city = "";
        this.hotelName = "";
        this.stars = 2;
    }

    public Hotel(String city, String hotelName, int stars) {
        this.city = city;
        this.hotelName = hotelName;
        this.stars = stars;
    }

    public void printHotel() {
        System.out.println(city + " " + hotelName + " " + stars);
    }

    public String getCity() { return city; }
    public String getHotelName() { return hotelName; }
    public int getStars() { return stars; }

    public static class CityNameComparator implements Comparator<Hotel> {
        @Override
        public int compare(Hotel hotel1, Hotel hotel2) {
            return hotel1.city.compareToIgnoreCase(hotel2.city);
        }
    }

    public static class StarsComparator implements Comparator<Hotel> {
        @Override
        public int compare(Hotel hotel1, Hotel hotel2) {
            return Integer.compare(hotel2.stars, hotel1.stars);
        }
    }

    public static class CityThenStarsComparator implements Comparator<Hotel> {
        @Override
        public int compare(Hotel hotel1, Hotel hotel2) {
            int cityCompare = hotel1.city.compareToIgnoreCase(hotel2.city);

            if (cityCompare != 0) return cityCompare;

            return Integer.compare(hotel2.stars, hotel1.stars);
        }
    }

    public static class HotelNameComparator implements Comparator<Hotel> {
        @Override
        public int compare(Hotel hotel1, Hotel hotel2) {
            return hotel1.hotelName.compareToIgnoreCase(hotel2.hotelName);
        }
    }
}
