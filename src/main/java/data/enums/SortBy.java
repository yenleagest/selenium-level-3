package data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortBy {
    LOWEST_PRICE_FIRST("search-sort-price"),
    DISTANCE("search-sort-distance-landmark"),
    TOP_REVIEWED("search-sort-guest-rating"),
    HOT_DEALS("search-sort-secret-deals");

    private final String dataElementName;

    public static SortBy fromString(String name) {
        try {
            return SortBy.valueOf(name.replace(" ", "_").toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No sort by option found with name: %s".formatted(name));
        }
    }
}
