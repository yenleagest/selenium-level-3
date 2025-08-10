package data.enums.sia;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdOns {
    MULTIPLE("Smart Delay - Multiple Trip"),
    NONE(null),
    SINGLE("Smart Delay - Single Trip");

    private final String dataPlan;
}
