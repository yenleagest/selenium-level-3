package testdata;

import data.enums.sia.AdOns;
import data.enums.sia.Plans;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SIATestData {

    private int rowNum;
    private String departureLocation;
    private String destinationLocation;
    private int tripLong;
    private int travellers;
    private int[] ages;
    private Plans plan;
    private AdOns addOns;
    private String price;
}
