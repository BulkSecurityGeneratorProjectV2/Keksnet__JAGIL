package de.neo.jagil.reader;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.neo.jagil.gui.GUI;
import de.neo.jagil.util.ParseUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class JsonGuiReader extends GuiReader {

    public JsonGuiReader() {
        super("json");
    }

    @Override
    public GUI.DataGui read(Path guiFile) throws IOException {
        GUI.DataGui gui = new GUI.DataGui();

        String jsonString = Files.readString(guiFile);
        JsonObject json = new Gson().fromJson(jsonString, JsonObject.class);

        gui.name = ParseUtil.getJsonString(json, "name");
        gui.size = json.get("size").getAsInt();

        if(!json.has("items")) {
            Bukkit.getLogger().warning("[JAGIL] Empty GUI " + guiFile + ": no items section!");
            return gui;
        }

        for(JsonElement elem : json.get("items").getAsJsonArray()) {
            JsonObject jsonItem = elem.getAsJsonObject();
            GUI.GuiItem item = new GUI.XmlHead();

            item.id = ParseUtil.getJsonString(jsonItem, "id");
            item.slot = jsonItem.get("slot").getAsInt();
            item.material = Material.getMaterial(ParseUtil.getJsonString(jsonItem, "material"));
            item.name = ParseUtil.getJsonString(jsonItem, "name");
            item.amount = ParseUtil.getJsonInt(jsonItem, "amount");
            if(jsonItem.has("lore")) {
                for(JsonElement strElem : jsonItem.get("lore").getAsJsonArray()) {
                    item.lore.add(strElem.getAsString());
                }
            }

            if(jsonItem.has("enchantments")) {
                for(JsonElement enchantElem : jsonItem.get("enchantments").getAsJsonArray()) {
                    JsonObject enchJson = enchantElem.getAsJsonObject();
                    GUI.GuiEnchantment enchantment = new GUI.GuiEnchantment();
                    enchantment.enchantment =
                            Arrays.stream(Enchantment.values())
                                    .filter(it -> enchJson.get("name").getAsString().equalsIgnoreCase(it.toString()))
                                    .findFirst().get();
                    enchantment.level = enchJson.get("level").getAsInt();
                    item.enchantments.add(enchantment);
                }
            }

            if(jsonItem.has("base64")) {
                ((GUI.XmlHead)item).texture = ParseUtil.getJsonString(jsonItem, "base64");
            }

            if(jsonItem.has("modelData")) {
                ((GUI.XmlHead)item).customModelData = ParseUtil.getJsonInt(jsonItem, "modelData");
            }

            gui.items.put(item.slot, item);

            if(jsonItem.has("fillTo")) {
                int fillMax = jsonItem.get("fillTo").getAsInt();
                for(int i = item.slot + 1; i < fillMax; i++) {
                    gui.items.put(i, new GUI.XmlHead((GUI.XmlHead)item));
                }
            }
        }

        return gui;
    }

    @Override
    public String getFileType() {
        return super.getFileType();
    }
}
