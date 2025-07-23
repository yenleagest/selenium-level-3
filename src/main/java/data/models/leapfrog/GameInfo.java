package data.models.leapfrog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(exclude = "index")
public class GameInfo {

    private int index; // 1-based index for Excel row or page number
    private String title;
    private String ageRange;
    private String price;

    public String getMismatchDetails(GameInfo actual) {
        List<String> lines = new ArrayList<>();
        if (!this.getAgeRange().equals(actual.getAgeRange()))
            lines.add("    Age    : expected '%s' but got '%s'".formatted(this.getAgeRange(), actual.getAgeRange()));
        if (!this.getPrice().equals(actual.getPrice()))
            lines.add("    Price  : expected '%s' but got '%s'".formatted(this.getPrice(), actual.getPrice()));
        return String.join("\n", lines);
    }
}
