package org.general.data;

/**
 * Decoder used to decode entries from file storage.
 *
 * @author Guoxing Li
 *
 */
class DataDecoder {

    /**
     * Decode the input String using AppData.DECODE_MAP
     * 
     * @param in
     * @return A decoded String
     */
    static String decode(String in) {
        StringBuilder sb = new StringBuilder();
        boolean matchMode = false;
        for (int i = 0; i < in.length(); ++i) {
            if (matchMode) {
                matchMode = false;
                String original = in.substring(i - 1, i + 1);
                String decoded = AppData.DECODE_MAP.get(original);
                if (decoded == null) {
                    sb.append(original);
                    if (in.charAt(i) == AppData.RESERVED_HEADER) {
                        matchMode = true;
                    }
                } else {
                    sb.append(decoded);
                }
            } else {
                if (in.charAt(i) == AppData.RESERVED_HEADER) {
                    matchMode = true;
                } else {
                    sb.append(in.charAt(i));
                }
            }
        }
        if (matchMode) {
            sb.append(in.charAt(in.length() - 1));
        }
        return sb.toString();
    }

}
