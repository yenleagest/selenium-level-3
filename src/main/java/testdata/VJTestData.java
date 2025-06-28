package testdata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VJTestData {

    private String testMethod;
    private String departureAirport;
    private String destinationAirport;
}
