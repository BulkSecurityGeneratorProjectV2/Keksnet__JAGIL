package de.neo.jagil.util;

import com.google.gson.JsonObject;
import de.neo.jagil.annotation.Internal;
import de.neo.jagil.gui.GUI;
import de.neo.jagil.gui.GuiTypes;

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

    @Internal
    public static int getAutoSlotId(GuiTypes.DataGui gui) {
        for(int i = -1; i > -999; i--) {
            if(gui.items.containsKey(i)) continue;
            return i;
        }
        throw new RuntimeException("No free slot id found!");
    }

    @Internal
    public static InventoryPosition getJsonPosition(JsonObject frame, String key) {
        if(!frame.has(key)) return InventoryPosition.DEFAULT;
        JsonObject json = frame.get(key).getAsJsonObject();
        if(!json.has("x")) return InventoryPosition.DEFAULT;
        if(!json.has("y")) return InventoryPosition.DEFAULT;
        return new InventoryPosition(json.get("x").getAsInt(), json.get("y").getAsInt());
    }

}
