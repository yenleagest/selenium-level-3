package data.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum Environment {
    AGODA("https://www.agoda.com"),
    BOOKS("https://books-pwakit.appspot.com"),
    LEAPFROG("https://store.leapfrog.com"),
    SIA("https://siassistance.com/en"),
    VJ_VI("https://www.vietjetair.com/vi"),
    VJ_EN("https://www.vietjetair.com/en");

    private final String baseUrl;

    public static Environment getEnvironment(String url) {
        return Arrays.stream(Environment.values())
                     .filter(env -> url.contains(env.getBaseUrl()))
                     .findFirst()
                     .orElseThrow(() -> new IllegalStateException("Unknown environment: " + url));
    }
}
