package utils;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import testdata.AgodaTestData;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.stream.StreamSupport;

import static common.Constants.RESOURCE_TEST_DATA_PATH;

@Slf4j
public class YmlParser {

    public static Object[][] getTestDataByMethod(String testMethod) {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(RESOURCE_TEST_DATA_PATH);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Test data file not found at: ".concat(RESOURCE_TEST_DATA_PATH), e);
        }

        Yaml yaml = new Yaml(new Constructor(AgodaTestData.class, new LoaderOptions()));
        Iterable<Object> loaded = yaml.loadAll(inputStream);

        return StreamSupport.stream(loaded.spliterator(), false)
                .map(data -> (AgodaTestData) data)
                .filter(testData -> testMethod.equals(testData.getTestMethod()))
                .map(testData -> new Object[]{testData})
                .toArray(Object[][]::new);
    }
}
