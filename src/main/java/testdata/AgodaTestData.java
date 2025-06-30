package testdata;

import data.enums.agoda.Facilities;
import data.enums.agoda.SortBy;
import data.models.agoda.Occupancy;
import data.models.agoda.PriceFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.List;

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
    private Facilities facility;
    private List<String> benefits;
    private List<String> reviewDetails;
}
