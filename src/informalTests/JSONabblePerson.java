package informalTests;

import json.JSONList;
import json.JSONMap;
import json.JSONObject;
import json.JSONabble;

public class JSONabblePerson implements JSONabble {
	
	private String name;
	private int age;
	private String address;
	private String[] petNames;
	
	public JSONabblePerson(String name, int age, String address, String... petNames) {
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
