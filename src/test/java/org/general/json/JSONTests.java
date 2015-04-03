package org.general.json;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class JSONTests {
	
	private class JSONablePerson implements JSONable {
		
		private String name;
		private int age;
		private String address;
		private String[] petNames;
		
		public JSONablePerson(String name, int age, String address, String... petNames) {
			this.age = age;
			this.name = name;
			this.address = address;
			this.petNames = petNames;
		}
		
		@Override
		public JSONObject toJSON() {
			JSONMap jsonMap = new JSONMap();
			jsonMap.put("name", name);
			jsonMap.put("age", age);
			jsonMap.put("address", address);
			
			JSONList petNamesList = new JSONList();
			for (String petName : petNames) {
				petNamesList.add(petName);
			}
			
			jsonMap.put("petNames", petNamesList);
			return jsonMap;
		}

	}
	
	private static final String TEST_NAME = "Ma\\rcel";
	private static final int TEST_AGE = 22;
	private static final String TEST_ADDRESS = "Philippines";
	private static final String TEST_PETNAME_ONE = "Tiger";
	private static final String TEST_PETNAME_TWO = "Ginger";
	
	@Test
	public void testJSON() {
		JSONablePerson person = new JSONablePerson(TEST_NAME, TEST_AGE, TEST_ADDRESS, TEST_PETNAME_ONE, TEST_PETNAME_TWO);
		JSONObject correctResult = createCorrectResult();
		assertTrue(person.toJSON().equals(correctResult));
	}
	
	private JSONObject createCorrectResult() {
	    JSONMap correctResult = new JSONMap();
	    correctResult.put("name", TEST_NAME);
	    correctResult.put("age", TEST_AGE);
	    correctResult.put("address", TEST_ADDRESS);
	    JSONList array = new JSONList();
	    array.add(TEST_PETNAME_ONE);
	    array.add(TEST_PETNAME_TWO);
	    correctResult.put("petNames", array);
        return correctResult;
	}
	
}
