package utils;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.stream.StreamSupport;

import static common.Constants.RESOURCE_TEST_DATA_PATH;

@Slf4j
public class YmlParser {

    public static <T> Object[][] getTestDataByMethod(String testMethod, Class<T> testDataClass) {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(RESOURCE_TEST_DATA_PATH);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Test data file not found at: ".concat(RESOURCE_TEST_DATA_PATH), e);
        }

        Yaml yaml = new Yaml(new Constructor(testDataClass, new LoaderOptions()));
        Iterable<Object> loaded = yaml.loadAll(inputStream);

        return StreamSupport.stream(loaded.spliterator(), false)
                .map(testDataClass::cast)
                .filter(testData -> {
                    try {
                        return testMethod.equals(testDataClass.getMethod("getTestMethod").invoke(testData));
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to invoke getTestMethod() on test data", e);
                    }
                })
                .map(testData -> new Object[]{testData})
                .toArray(Object[][]::new);
    }

    public static Map<String, Map<String, String>> loadLocales() {
        String fileName = "locales/" + extractLocaleFromBaseUrl() + ".yml";
        Yaml yaml = new Yaml();
        try (InputStream in = YmlParser.class.getClassLoader().getResourceAsStream(fileName)) {
            if (in == null) {
                throw new RuntimeException("YAML file not found: " + fileName);
            }
            return yaml.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load YAML: " + fileName, e);
        }
    }

    private static String extractLocaleFromBaseUrl() {
        URI uri = URI.create(System.getProperty("selenide.baseUrl"));
        String[] segments = uri.getPath().split("/");
        return segments.length > 1 ? segments[1] : "en";
    }
}
