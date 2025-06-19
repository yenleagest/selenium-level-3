package data.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
@AllArgsConstructor
@Slf4j
public class CardContainer {

    String destination;
    int price;
    int rating;

    /**
     * Filters and optionally sorts a list of hotels based on destination, price range, and rating.
     *
     * @param hotels        the list of hotels to filter
     * @param destination   the destination string to filter by; if null, destination is ignored
     * @param sortedByPrice if true, the list is sorted by price in ascending order;
     *                      if false, price filtering is applied only when {@code priceRange} is provided
     * @param rating        the star rating to filter by; if 0, rating is ignored
     * @param priceRange    optional varargs specifying minimum and maximum price. Must contain exactly two values to apply filtering:
     *                      - if {@code sortedByPrice} is true, the list is sorted regardless of {@code priceRange}
     *                      - if {@code sortedByPrice} is false and {@code priceRange} has two values, price filtering is applied
     *                      - if {@code sortedByPrice} is false and {@code priceRange} is not provided, no price filtering is applied
     * @return the filtered list of hotels
     */
    public static List<CardContainer> filteredHotels(List<CardContainer> hotels,
                                                     @Nullable String destination,
                                                     boolean sortedByPrice,
                                                     int rating,
                                                     int... priceRange) {

        if (priceRange.length != 0 && priceRange.length != 2) {
            throw new IllegalArgumentException("Either provide both minPrice and maxPrice, or omit them entirely.");
        }

        if (priceRange.length == 2)
            log.info("Filtering hotels with destination: {}, price range: {}-{}, rating: {}, sortedByPrice: {}",
                    destination, priceRange[0], priceRange[1], rating, sortedByPrice);
        else
            log.info("Filtering hotels with destination: {}, rating: {}, sortedByPrice: {}",
                    destination, rating, sortedByPrice);

        List<CardContainer> filtered = hotels.stream()
                .filter(hotel -> destination == null || hotel.getDestination().contains(destination))
                .filter(hotel -> (sortedByPrice || priceRange.length != 2) || (hotel.getPrice() >= priceRange[0] && hotel.getPrice() <= priceRange[1]))
                .filter(hotel -> rating == 0 || hotel.getRating() == rating)
                .toList();

        if (sortedByPrice) {
            filtered = new ArrayList<>(filtered);
            filtered.sort(Comparator.comparingInt(CardContainer::getPrice));
        }

        return filtered;
    }
}
