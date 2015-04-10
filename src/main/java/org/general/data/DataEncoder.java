package org.general.data;

import java.util.Map.Entry;

/**
 * Encoder used to encode entries to file storage.
 *
 * @author Guoxing Li
 *
 */
class DataEncoder {

    /**
     * Not efficient... I know
     * 
     * @param in
     * @return A encoded String
     */
    static String encode(String in) {
        for (Entry<String, String> entry : AppData.ENCODE_MAP.entrySet()) {
            in = in.replace(entry.getKey(), entry.getValue());
        }
        return in;
    }
}
