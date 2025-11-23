package org.awards;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

public class PrizeService {
    public static int CONCERT_COST = 200;
    public static double LOYAL_DISCOUNT = 0.10;

    private final Map<String, List<Gift>> catalog = new LinkedHashMap<>();
    private final NumberFormat money;

    public PrizeService(Locale locale) {
        this.money = NumberFormat.getCurrencyInstance(locale);
        loadConfig();
        loadGifts();
    }

    public List<Gift> getGiftsForGreeter(String greeterName) {
        return catalog.getOrDefault(greeterName, List.of());
    }

    public int calcSubtotal(Gift gift, boolean concert) {
        int base = gift != null ? gift.price() : 0;
        if (concert) base += CONCERT_COST;
        return base;
    }

    public int applyLoyalDiscount(int subtotal, boolean loyal) {
        return loyal ? (int) Math.round(subtotal * (1.0 - LOYAL_DISCOUNT)) : subtotal;
    }

    public String money(int amount) {
        return money.format(amount);
    }

    private void loadGifts() {
        List<String[]> rows = TextTableReader.readFromResource("/data/gifts.txt", '|', true);
        Map<String, List<Gift>> byGreeter = new LinkedHashMap<>();
        for (String[] r : rows) {
            String greeterName = at(r, 0);
            String giftName = at(r, 1);
            int price = parseIntSafe(at(r, 2));
            byGreeter.computeIfAbsent(greeterName, k -> new ArrayList<>()).add(new Gift(giftName, price));
        }
        Map<String, List<Gift>> sorted = byGreeter.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream().sorted(Comparator.comparing(Gift::name)).collect(Collectors.toList()),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
        catalog.clear();
        catalog.putAll(sorted);
    }

    private void loadConfig() {
        try {
            List<String[]> rows = TextTableReader.readFromResource("/data/config.txt", '|', true);
            for (String[] r : rows) {
                String key = at(r, 0).toLowerCase(Locale.ROOT);
                String val = at(r, 1);
                if (key.equals("concertcost")) CONCERT_COST = parseIntSafe(val);
                if (key.equals("loyaldiscount")) LOYAL_DISCOUNT = parseDoubleSafe(val);
            }
        } catch (Exception ignored) {}
    }

    private static String at(String[] arr, int idx) { return idx < arr.length ? arr[idx] : ""; }
    private static int parseIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }
    private static double parseDoubleSafe(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0.0; }
    }
}
