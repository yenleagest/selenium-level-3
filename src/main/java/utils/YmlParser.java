package utils;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import testdata.AgodaTestData;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static common.Constants.RESOURCE_DATA_PATH;

@Slf4j
public class YmlParser {

    public static Object[][] getAgodaTestData(String testCaseName) throws FileNotFoundException {
        String path = RESOURCE_DATA_PATH + "agoda/" + testCaseName + ".yml";
        InputStream inputStream = new FileInputStream(path);

        Yaml yaml = new Yaml(new Constructor(AgodaTestData.class, new LoaderOptions()));
        Iterable<Object> loaded = yaml.loadAll(inputStream);

        return java.util.stream.StreamSupport.stream(loaded.spliterator(), false)
                .map(data -> new Object[]{data})
                .toArray(Object[][]::new);
    }
}
