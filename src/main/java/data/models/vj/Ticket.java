package data.models.vj;

import data.enums.vj.FareOption;
import data.enums.vj.FlightType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Ticket {

    private FlightType flightType;
    private FlightInfo departureFlight;
    private FlightInfo returnFlight;
    private FareOption fareOption;
}
