package org.general.http;

import org.general.util.NumberParser;

/**
 * URLDecoder used to decode URL according to
 * https://tools.ietf.org/html/rfc3986.
 *
 * @author Guoxing Li
 *
 */
public class URLDecoder {
    public static String decode(String in) {
        StringBuilder result = new StringBuilder();
        int pos = 0;
        while (pos < in.length()) {
            if (in.charAt(pos) == '%') {
                if (pos + 2 < in.length()) {
                    String str = in.substring(pos + 1, pos + 3);
                    if (NumberParser.isNumber(str, 16)) {
                        int code = Integer.parseInt(in.substring(pos + 1, pos + 3), 16);
                        if (code >= 32 && code <= 255) {
                            result.append((char) code);
                            pos += 3;
                            continue;
                        }
                    }
                }
            }
            // normal character or failed on parsing, move on
            result.append(in.charAt(pos));
            pos++;
        }
        return result.toString();
    }
}
