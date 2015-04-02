package json;

import java.util.HashMap;

/**
 * Class comments here... TODO: describe JSON formatting
 * @author marcelpuyat
 *
 */
public abstract class JSONObject {

	/**
	 * Mapping from JSON characters that should be escaped to their appropriate
	 * replacements. Note that backslashes must be escaped twice when used in
	 * regex. Static initializer sets up the hashmap for use in the jsonEscape method.
	 */
	private static HashMap<String, String> escapeChars;
	static {
		escapeChars = new HashMap<String, String>();
		escapeChars.put("\b", "\\\\b");
		escapeChars.put("\n", "\\\\n");
		escapeChars.put("\f", "\\\\f");
		escapeChars.put("\r", "\\\\r");
		escapeChars.put("\t", "\\\\t");
		escapeChars.put("\"", "\\\\\"");
		escapeChars.put("\\\\", "\\\\\\\\");
		escapeChars.put("/", "\\\\/");
		// TODO: Unicode?
	}
	
	/**
	 * Will return a valid JSON string
	 */
	public abstract String toString();
	
	/**
	 * If passed in object is a string, will escape characters as necessary and
	 * wrap string in quotation marks.
	 * @param val JSON value
	 * @return escaped string wrapped in quotations
	 */
	public static String jsonEscape(Object val) {
		if (val instanceof String) {
			return "\"" + replaceSpecialChars(val.toString()) + "\"";
		} else {
			return val.toString();
		}
	}
	
	/**
	 * Uses statically initialized hashmap of characters to be escaped (and their
	 * replacements) to return a string with these characters replaced appropriately.
	 * @param str String to be escaped
	 * @return escaped string
	 */
	private static String replaceSpecialChars(String str) {
		for (String charToEscape : escapeChars.keySet()) {
			str = str.replaceAll(charToEscape, escapeChars.get(charToEscape));
		}
		return str;
	}
}