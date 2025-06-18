package utils;

import data.models.CardContainer;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CollectionUtils {

    public static <T extends Comparable<T>> List<T> sortAscending(Collection<T> collection) {
        return collection.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public static <T> List<T> filterByKeyword(Collection<T> collection, String keyword) {
        return collection.stream()
                .filter(item -> {
                    if (item instanceof String str) {
                        return str.contains(keyword);
                    } else if (item instanceof Integer i) {
                        try {
                            return i.equals(Integer.parseInt(keyword));
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    public static List<Integer> getPriceFromText(List<String> texts) {
        return texts.stream()
                .map(text -> text.replaceAll("[^\\d]", "")) // Remove ₫, commas, spaces
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    public static List<Integer> filterByRange(List<Integer> list, int min, int max) {
        return list.stream()
                .filter(num -> num >= min && num <= max)
                .collect(Collectors.toList());
    }

    public static List<String> getDestinations(List<CardContainer> hotels) {
        List<String> destinations = hotels.stream()
                .map(CardContainer::getDestination)
                .toList();
        log.info("List of destinations: {}", destinations);
        return destinations;
    }

    public static List<Integer> getPrices(List<CardContainer> hotels) {
        List<Integer> prices = hotels.stream()
                .map(CardContainer::getPrice)
                .toList();
        log.info("List of prices: {}", prices);
        return prices;
    }

    public static List<Integer> getRatings(List<CardContainer> hotels) {
        List<Integer> ratings = hotels.stream()
                .map(CardContainer::getRating)
                .toList();
        log.info("List of ratings: {}", ratings);
        return ratings;
    }
}
