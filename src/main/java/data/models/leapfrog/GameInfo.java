package data.models.leapfrog;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameInfo {

    private String title;
    private String ageRange;
    private String price;
}
