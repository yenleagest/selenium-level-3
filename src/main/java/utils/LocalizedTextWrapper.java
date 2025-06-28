package utils;

import java.util.Map;

// wrapper for localized text loaded from YAML, allowing enum-based access.
// allows textMap.get(PASSENGER_ADULTS) instead of using textMap.get(PASSENGER_ADULTS.name())
public class LocalizedTextWrapper<E extends Enum<E>> {

    private final Map<String, String> internal;

    public LocalizedTextWrapper(String className) {
        this.internal = YmlParser.loadLocales().get(className);
    }

    public String get(E key) {
        return internal.get(key.name());
    }
}
