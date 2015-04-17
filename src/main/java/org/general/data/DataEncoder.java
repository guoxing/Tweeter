package org.general.data;


/**
 * Encoder used to encode entries to file storage.
 *
 * @author Guoxing Li
 *
 */
class DataEncoder {

    /**
     * Encode the input String using AppData.ENCODE_MAP
     * 
     * @param in
     * @return An encoded String
     */
    static String encode(String in) {
        StringBuilder sb = new StringBuilder();
        for (char ch : in.toCharArray()) {
            String encoded = AppData.ENCODE_MAP.get(String.valueOf(ch));
            if (encoded != null) {
                sb.append(encoded);
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}
