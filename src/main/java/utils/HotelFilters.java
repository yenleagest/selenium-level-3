package utils;

import data.models.Hotel;
import data.models.PriceFilter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class HotelFilters {
    private List<Hotel> hotels;

    public HotelFilters(List<Hotel> hotels) {
        this.hotels = new ArrayList<>(hotels);
    }

    public HotelFilters filterByDestination(String destination) {
        if (destination != null && !destination.isEmpty()) {
            hotels = hotels.stream()
                    .filter(hotel -> hotel.getDestination().contains(destination))
                    .collect(Collectors.toList());
        }
        return this;
    }

    public HotelFilters filterByRating(int rating) {
        if (rating > 0) {
            hotels = hotels.stream()
                    // include hotels with rating in [rating, rating + 1),
                    // e.g., for input 3, 3.5 is accepted sine it > 3.0 & < 4.0
                    .filter(hotel -> hotel.getRating() < rating + 1 && hotel.getRating() >= rating)
                    .collect(Collectors.toList());
        }
        return this;
    }

    public HotelFilters filterByPriceRange(PriceFilter priceFilter) {
        hotels = hotels.stream()
                .filter(hotel -> hotel.getPrice() >= priceFilter.getMin() && hotel.getPrice() <= priceFilter.getMax())
                .collect(Collectors.toList());
        return this;
    }

    public HotelFilters sortByPrice() {
        hotels.sort(Comparator.comparingInt(Hotel::getPrice));
        return this;
    }

    public List<Hotel> get() {
        log.info("Filtered hotels: {}", hotels);
        return hotels;
    }
}
