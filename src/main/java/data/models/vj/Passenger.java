package data.models.vj;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Passenger {

    private int adults;
    private int children;
    private int infants;
}
