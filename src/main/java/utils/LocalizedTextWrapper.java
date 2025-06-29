package utils;

import java.util.Map;

// wrapper for localized text loaded from YAML, with enum-based access for type safety.
// allows cleaner usage: textMap.get(LocalizedText.PASSENGER_ADULTS)
// instead of: textMap.get(LocalizedText.PASSENGER_ADULTS.name())
public class LocalizedTextWrapper<E extends Enum<E>> {

    private final Map<String, String> internal;

    public LocalizedTextWrapper(String className) {
        this.internal = YmlParser.loadLocales().get(className);
    }

    public String get(E key) {
        return internal.get(key.name());
    }
}
