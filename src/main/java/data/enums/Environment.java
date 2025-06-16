package data.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Environment {
    AGODA("https://www.agoda.com/"),
    VJ_VI("https://www.vietjetair.com/vi/"),
    VJ_EN("https://www.vietjetair.com/en/"),
    VJ_KO("https://www.vietjetair.com/ko/"),
    ;

    private final String baseUrl;

    public static Environment getEnvironment(String name) {
        try {
            return Environment.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No environment found with name: %s".formatted(name));
        }
    }
}

