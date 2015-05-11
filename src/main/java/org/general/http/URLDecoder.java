package org.general.http;

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
                    try {
                        int code = Integer.parseInt(
                                in.substring(pos + 1, pos + 3), 16);
                        if (code >= 32 && code <= 255) {
                            result.append((char) code);
                            pos += 3;
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        // invalid, ignore and move on
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
