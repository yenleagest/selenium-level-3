package data.enums.sia;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Plans {
    GLOBAL("global cov"),
    GOLD("gold cov"),
    SILVER("silver cov"),;

    private final String dataPlan;

    public static Plans fromString(String name) {
        return Arrays.stream(Plans.values())
                     .filter(plan -> name.equalsIgnoreCase(plan.name()))
                     .findFirst()
                     .orElseThrow(() -> new IllegalStateException("Unknown plan: " + name));
    }
}
