package org.general.data;

import java.util.Map.Entry;

/**
 * Decoder used to decode entries from file storage.
 *
 * @author Guoxing Li
 *
 */
class DataDecoder {

    /**
     * Not efficient... I know
     * 
     * @param in
     * @return A decoded String
     */
    static String decode(String in) {
        for (Entry<String, String> entry : AppData.DECODE_MAP.entrySet()) {
            in = in.replace(entry.getKey(), entry.getValue());
        }
        return in;
    }

}
