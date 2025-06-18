package utils;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static common.Constants.RESOURCE_DATA_PATH;

public class YmlParser {

    public static List<Map<String, String>> getTestData(String testCaseName) throws IOException {
        InputStream input = new FileInputStream(RESOURCE_DATA_PATH + "data.yml");
        LoaderOptions options = new LoaderOptions();
        Constructor constructor = new Constructor(Map.class, options);
        Yaml yaml = new Yaml(constructor);
        Map<String, List<Map<String, String>>> data = yaml.load(input);

        if (!data.containsKey(testCaseName)) {
            throw new RuntimeException("Test case not found: " + testCaseName);
        }
        return data.get(testCaseName);
    }
}

