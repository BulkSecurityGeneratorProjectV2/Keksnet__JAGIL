package de.neo.jagil.reader;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.neo.jagil.gui.GUI;
import de.neo.jagil.gui.GuiTypes;
import de.neo.jagil.util.InventoryPosition;
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
    public GuiTypes.DataGui read(Path guiFile) throws IOException {
        GuiTypes.DataGui gui = new GuiTypes.DataGui();

        String jsonString = Files.readString(guiFile);
        JsonObject json = new Gson().fromJson(jsonString, JsonObject.class);

        gui.name = ParseUtil.getJsonString(json, "name");
        gui.size = json.get("size").getAsInt();
        gui.animationMod = json.get("animationTick").getAsInt();

        if(!json.has("items")) {
            Bukkit.getLogger().warning("[JAGIL] Empty GUI " + guiFile + ": no items section!");
            return gui;
        }

        for(JsonElement elem : json.get("items").getAsJsonArray()) {
            JsonObject jsonItem = elem.getAsJsonObject();
            GuiTypes.GuiItem item = new GuiTypes.GuiItem();

            item.id = ParseUtil.getJsonString(jsonItem, "id");
            if(jsonItem.has("pos")) {
                item.slot = ParseUtil.getJsonPosition(jsonItem, "pos").toSlot();
            }else {
                if(!item.id.isEmpty() && !jsonItem.has("slot")) {
                    item.slot = ParseUtil.getAutoSlotId(gui);
                } else {
                    item.slot = jsonItem.get("slot").getAsInt();
                }
            }
            item.material = Material.getMaterial(ParseUtil.getJsonString(jsonItem, "material"));
            item.name = ParseUtil.getJsonString(jsonItem, "name");
            item.amount = ParseUtil.getJsonInt(jsonItem, "amount");
            item.amount = item.amount == 0 ? 1 : item.amount;
            if(jsonItem.has("lore")) {
                for(JsonElement strElem : jsonItem.get("lore").getAsJsonArray()) {
                    item.lore.add(strElem.getAsString());
                }
            }

            if(jsonItem.has("enchantments")) {
                for(JsonElement enchantElem : jsonItem.get("enchantments").getAsJsonArray()) {
                    JsonObject enchJson = enchantElem.getAsJsonObject();
                    GuiTypes.GuiEnchantment enchantment = new GuiTypes.GuiEnchantment();
                    enchantment.enchantment =
                            Arrays.stream(Enchantment.values())
                                    .filter(it -> enchJson.get("name").getAsString().equalsIgnoreCase(it.toString()))
                                    .findFirst().get();
                    enchantment.level = enchJson.get("level").getAsInt();
                    item.enchantments.add(enchantment);
                }
            }

            if(jsonItem.has("base64")) {
                item.texture = ParseUtil.getJsonString(jsonItem, "base64");
            }

            if(jsonItem.has("modelData")) {
                item.customModelData = jsonItem.get("modelData").getAsInt();
            }

            if(jsonItem.has("animation")) {
                for(JsonElement animElem : jsonItem.get("animation").getAsJsonArray()) {
                    JsonObject animFrame = animElem.getAsJsonObject();
                    GuiTypes.GuiAnimationFrame frame = new GuiTypes.GuiAnimationFrame();
                    frame.itemId = animFrame.has("itemId") ? animFrame.get("itemId").getAsString() : item.id;
                    frame.position = animFrame.has("pos") ?
                            ParseUtil.getJsonPosition(animFrame, "pos") : InventoryPosition.fromSlot(item.slot);
                    frame.shouldCleanUp = !animFrame.has("cleanUp") || animFrame.get("cleanUp").getAsBoolean();
                    if(item.animationFrames.size() != 0)
                        frame.previousFrame = item.animationFrames.get(item.animationFrames.size() - 1);
                    item.animationFrames.add(frame);
                }
                item.animationFrames.get(0).previousFrame = item.animationFrames.get(item.animationFrames.size() - 1);
            }

            if(jsonItem.has("attributes")) {
                for(JsonElement attrElem : jsonItem.get("attributes").getAsJsonArray()) {
                    JsonObject attrJson = attrElem.getAsJsonObject();
                    item.attributes.put(attrJson.get("name").getAsString(), attrJson.get("value").getAsJsonObject());
                }
            }

            gui.items.put(item.slot, item);

            if(jsonItem.has("fillTo")) {
                int fillMax = jsonItem.get("fillTo").getAsInt();
                for(int i = item.slot + 1; i <= fillMax; i++) {
                    GuiTypes.GuiItem item2 = new GuiTypes.GuiItem(item);
                    item2.slot = i;
                    gui.items.put(i, item2);
                }
            }

            if(jsonItem.has("fill")) {
                for(JsonElement fillElem : jsonItem.get("fill").getAsJsonArray()) {
                    if(fillElem.isJsonObject()) {
                        JsonObject fillJson = fillElem.getAsJsonObject();
                        int from = fillJson.get("from").getAsInt();
                        int to = fillJson.get("to").getAsInt();
                        for(int i = from; i <= to; i++) {
                            GuiTypes.GuiItem item2 = new GuiTypes.GuiItem(item);
                            item2.slot = i;
                            gui.items.put(i, item2);
                        }
                    }else if(fillElem.isJsonPrimitive()) {
                        int slot = fillElem.getAsInt();
                        GuiTypes.GuiItem item2 = new GuiTypes.GuiItem(item);
                        item2.slot = slot;
                        gui.items.put(slot, item2);
                    }else {
                        Bukkit.getLogger().warning("[JAGIL] Invalid fill property item in GUI " + guiFile + ": " + fillElem);
                    }
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
