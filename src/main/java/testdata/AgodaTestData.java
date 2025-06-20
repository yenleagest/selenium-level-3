package testdata;

import data.enums.SortBy;
import data.models.Occupancy;
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
    private DayOfWeek weekday;
    private Integer duration;
    private Integer resultCount;
    private Occupancy occupancy;
    private Integer minPrice;
    private Integer maxPrice;
    private Integer rating;
    private SortBy sortBy;
}
