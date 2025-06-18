package data.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

@Data
@With
@AllArgsConstructor
public class Occupancy {

    private final int rooms;
    private final int adults;
    private final int children;
}
