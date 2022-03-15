package de.neo.jagil.util;

import com.google.gson.JsonObject;
import de.neo.jagil.annotation.Internal;

public class ParseUtil {

    @Internal
    public static String getJsonString(JsonObject json, String key) {
        if(!json.has(key)) return "";
        return json.get(key).getAsString();
    }

    @Internal
    public static int getJsonInt(JsonObject json, String key) {
        if(!json.has(key)) return 0;
        return json.get(key).getAsInt();
    }

    @Internal
    public static String normalizeString(String unfiltered) {
        StringBuilder r = new StringBuilder();
        for(char c : unfiltered.toCharArray()) {
            if(Character.isDigit(c) || c == '-') {
                r.append(c);
            }
        }
        return r.toString().trim();
    }

}
