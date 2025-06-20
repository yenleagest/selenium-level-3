package testdata;

import data.enums.SortBy;
import data.models.Occupancy;
import data.models.PriceFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgodaTestData {

    private String testMethod;
    private String location;
    private DayOfWeek checkIn;
    private Integer checkOut;
    private Integer resultCount;
    private Occupancy occupancy;
    private PriceFilter priceFilter;
    private Integer rating;
    private SortBy sortBy;
}
