package de.neo.jagil.util;

import com.google.gson.JsonObject;

public class ParseUtil {

    public static String getJsonString(JsonObject json, String key) {
        if(!json.has(key)) return "";
        return json.get(key).getAsString();
    }

    public static int getJsonInt(JsonObject json, String key) {
        if(!json.has(key)) return 0;
        return json.get(key).getAsInt();
    }

}
