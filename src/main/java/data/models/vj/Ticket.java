package data.models.vj;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Ticket {

    boolean isReturnFlight;
    private LocalDate returnDate;
    private FlightInfo flightInfo;
}
