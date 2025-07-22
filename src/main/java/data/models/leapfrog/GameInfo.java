package data.models.leapfrog;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameInfo {

    private int index; // 1-based index for Excel row or page number
    private String title;
    private String ageRange;
    private String price;
}
