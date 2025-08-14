package data.enums.sia;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Plans {
    GLOBAL("global cov"),
    GOLD("gold cov"),
    SILVER("silver cov"),;

    private final String dataPlan;
}
