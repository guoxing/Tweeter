package org.general.json;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.general.json.JSONObject.JSONable;
import org.junit.Test;

public class JSONTests {

    private class JSONSerializablePerson implements JSONable {

        private String name;
        private int age;
        private String address;
        private List<String> petNames;

        public JSONSerializablePerson(String name, int age, String address,
                String... petNames) {
            this.age = age;
            this.name = name;
            this.address = address;
            this.petNames = Arrays.asList(petNames);
        }

        @Override
        public JSONObject toJsonObject() {
            Map<String, JSONObject> map = new HashMap<>();
            map.put("name", new JSONObject(name));
            map.put("age", new JSONObject(age));
            map.put("address", new JSONObject(address));
            map.put("petNames", JSONObject.fromStrings(petNames));
            
            return new JSONObject(map);
        }

    }

    private static final String TEST_NAME = "Ma\\rcel";
    private static final int TEST_AGE = 22;
    private static final String TEST_ADDRESS = "Philippines";
    private static final String TEST_PETNAME_ONE = "Tiger";
    private static final String TEST_PETNAME_TWO = "Ginger";

    @Test
    public void testJSON() {
        JSONSerializablePerson person = new JSONSerializablePerson(TEST_NAME,
                TEST_AGE, TEST_ADDRESS, TEST_PETNAME_ONE, TEST_PETNAME_TWO);
        JSONObject correctResult = createCorrectResult();
        HashMap<String, String> str = new HashMap<>();
        str.put("Marcel", "LOL");
        HashMap<String, String> str2 = new HashMap<>();
        str2.put("Marcel", "LOL");
        assertTrue(person.toJsonObject().equals(correctResult));
    }

    private JSONObject createCorrectResult() {
        Map<String, JSONObject> correctResult = new HashMap<>();
        correctResult.put("name", new JSONObject(TEST_NAME));
        correctResult.put("age", new JSONObject(TEST_AGE));
        correctResult.put("address", new JSONObject(TEST_ADDRESS));
        correctResult.put("petNames", JSONObject.fromStrings(Arrays.asList(new String[]{TEST_PETNAME_ONE, TEST_PETNAME_TWO})));
        return new JSONObject(correctResult);
    }
}
