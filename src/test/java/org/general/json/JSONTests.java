package org.general.json;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class JSONTests {
	
	private class JSONSerializablePerson implements JSONSerializable {
		
		private String name;
		private int age;
		private String address;
		private String[] petNames;
		private boolean isMale;
		
		public JSONSerializablePerson(String name, int age, String address, 
		        Boolean isMale, String... petNames) {
			this.age = age;
			this.name = name;
			this.address = address;
			this.petNames = petNames;
			this.isMale = isMale;
		}
		
		@Override
		public JSONObject toJSON() {
			JSONMap jsonMap = new JSONMap();
			jsonMap.put("name", name);
			jsonMap.put("age", age);
			jsonMap.put("address", address);
			jsonMap.put("isMale", isMale);
			
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
	private static final Boolean TEST_IS_MALE = true;
	
	@Test
	public void testJSON() {
		JSONSerializablePerson person = new JSONSerializablePerson(TEST_NAME, TEST_AGE, TEST_ADDRESS, 
		        TEST_IS_MALE, TEST_PETNAME_ONE, TEST_PETNAME_TWO);
		JSONObject correctResult = createCorrectResult();
		assertTrue(person.toJSON().equals(correctResult));
	}
	
	private JSONObject createCorrectResult() {
	    JSONMap correctResult = new JSONMap();
	    correctResult.put("name", TEST_NAME);
	    correctResult.put("age", TEST_AGE);
	    correctResult.put("address", TEST_ADDRESS);
	    correctResult.put("isMale", TEST_IS_MALE);
	    JSONList array = new JSONList();
	    array.add(TEST_PETNAME_ONE);
	    array.add(TEST_PETNAME_TWO);
	    correctResult.put("petNames", array);
        return correctResult;
	}
}
