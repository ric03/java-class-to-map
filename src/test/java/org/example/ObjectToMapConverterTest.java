package org.example;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ObjectToMapConverterTest {


    static class SampleClass {
        private Integer x = 123;
        private String y = "test";
        private Boolean z = true;
        // private boolean zz = true;
        private String n = null;
        private Map<String, String> w = Map.of("key1", "value1", "key2", "value2");
    }

    private static Map<String, String> convertToMap(Object obj) {
        Map<String, String> result = new HashMap<>();

        if (obj == null) {
            return result; // Return an empty map for null objects
        }

        Class<?> objClass = obj.getClass();

        // Iterate over all declared fields of the class
        for (Field field : objClass.getDeclaredFields()) {
            field.setAccessible(true); // Allow access to private fields

            try {
                String fieldName = field.getName();
                Object value = field.get(obj); // Get the field value

                // Check if the field is of type Map<String, String>
                if (value instanceof Map<?, ?> mapValue) {
                    for (Map.Entry<?, ?> entry : mapValue.entrySet()) {
                        String mapKey = entry.getKey().toString();
                        String mapValueStr = entry.getValue() != null ? entry.getValue().toString() : "null";
                        result.put(mapKey, mapValueStr);
                    }
                } else {
                    // Add the field to the result map
                    System.out.println(field.getAnnotatedType()); // TODO handle primitive types?
                    result.put(fieldName, value != null ? value.toString() : "null");
                }
            } catch (IllegalAccessException e) {
                // Handle potential exceptions
                result.put(field.getName(), "error");
            }
        }

        return result;
    }


    @Test
    void testMapping() {
        // Arrange
        var obj = new SampleClass();
        // Act
        var result = convertToMap(obj);
        // Assert
        Map<String, String> expected = Map.of(
                "x", "123",
                "y", "test",
                "z", "true",
                "n", "null",
                "key1", "value1",
                "key2", "value2"
        );
        assertThat(result).containsExactlyInAnyOrderEntriesOf(expected);

    }

}