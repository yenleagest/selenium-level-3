package data.models.vj;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class FlightInfo {

    private String currency;
    private String departureAirport;
    private String destinationAirport;
    private LocalDate takeOffDate;
    private Passenger passenger;
}
