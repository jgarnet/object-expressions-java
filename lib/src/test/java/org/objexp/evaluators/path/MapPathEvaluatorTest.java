package org.objexp.evaluators.path;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class MapPathEvaluatorTest {
    @Test
    public void test() throws Exception {
        MapPathEvaluator e = new MapPathEvaluator();
        Map<String, Object> map = this.loadMap("evaluators/path/test.json");
        Assertions.assertEquals("test2", e.evaluate(map, "$test.nested.[test collection].2"));
        Assertions.assertEquals(map.get("collection"), e.evaluate(map, "$collection"));
        Assertions.assertEquals(1, (Integer) e.evaluate(map, "$collection.0.a.b"));
        Assertions.assertEquals(2, (Integer) e.evaluate(map, "$collection.1.c.1"));
        Assertions.assertEquals("test", e.evaluate(map, "$0"));
    }

    public Map<String, Object> loadMap(String fileName) throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream inputStream= classLoader.getResourceAsStream(fileName);
        return new ObjectMapper().readValue(inputStream, new TypeReference<Map<String, Object>>(){});
    }
}
