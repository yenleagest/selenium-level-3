package data.models.vj;

import io.qameta.allure.Step;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Passenger {

    private int adults;
    private int children;
    private int infants;

    @Step("Get passenger info")
    public static Passenger fromString(String text) {
        String[] passengerInfo = text.split("\\|")[1].trim().split(",");
        int adults;
        int children = 0;
        int infants = 0;
        switch (passengerInfo.length) {
            case 3 -> {
                adults = Integer.parseInt(passengerInfo[0].split(" ")[0]);
                children = Integer.parseInt(passengerInfo[1].split(" ")[0]);
                infants = Integer.parseInt(passengerInfo[2].split(" ")[0]);
            }
            case 2 -> {
                adults = Integer.parseInt(passengerInfo[0].split(" ")[0]);
                children = Integer.parseInt(passengerInfo[1].split(" ")[0]);
            }
            case 1 -> adults = Integer.parseInt(passengerInfo[0].split(" ")[0]);
            default -> throw new IllegalStateException("Unexpected passenger info format: " + String.join(",", passengerInfo));
        }

        return new Passenger(adults, children, infants);
    }
}
