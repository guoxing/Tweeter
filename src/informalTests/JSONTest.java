package informalTests;

public class JSONTest {
	public static void main(String[] args) {
		JSONabblePerson person = new JSONabblePerson("Ma\"rcel", 22, "Philippines", "Tiger", "Ginger");
		System.out.println(person.toJSON());
	}
}
