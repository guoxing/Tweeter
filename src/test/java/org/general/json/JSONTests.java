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
	
	@Test
	public void testJSON() {
		JSONablePerson person = new JSONablePerson("Ma\\rcel", 22, "Philippines", "Tiger", "Ginger");
		JSONObject correctResult = createCorrectResult();
		assertTrue(person.toJSON().equals(correctResult));
	}
	
	private JSONObject createCorrectResult() {
	    JSONMap correctResult = new JSONMap();
	    correctResult.put("name", "Ma\\rcel");
	    correctResult.put("age", 22);
	    correctResult.put("address", "Philippines");
	    JSONList array = new JSONList();
	    array.add("Tiger");
	    array.add("Ginger");
	    correctResult.put("petNames", array);
        return correctResult;
	}
	
}
