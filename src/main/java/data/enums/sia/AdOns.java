package data.enums.sia;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum AdOns {
    MULTIPLE("Smart Delay - Multiple Trip"),
    NONE(null),
    SINGLE("Smart Delay - Single Trip");

    private final String dataPlan;

    public static AdOns fromString(String name) {
        return Arrays.stream(AdOns.values())
                     .filter(adOns -> name.equalsIgnoreCase(adOns.name()))
                     .findFirst()
                     .orElseThrow(() -> new IllegalStateException("Unknown Add-ons: " + name));
    }
}
