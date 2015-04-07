package org.general.http;

/**
 * URLDecoder.
 *
 * @author Guoxing Li
 *
 */
public class URLDecoder {
    public static String decode(String in) {
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        int pos = -1;
        for (char c : in.toCharArray()) {
            if (c == '%') {
                pos = 0;
            }
            if (pos >= 0) {
                temp.append(c);
                pos++;
                if (pos == 3) {
                    char decoded = (char) Integer.parseInt(temp.substring(1),
                            16);
                    if (decoded >= 32) {
                        sb.append(decoded);
                    } else {
                        sb.append(temp.toString());
                    }
                    pos = -1;
                    temp = new StringBuilder();
                }
            } else {
                sb.append(c);
            }
        }
        sb.append(temp.toString());
        return sb.toString();
    }
}
