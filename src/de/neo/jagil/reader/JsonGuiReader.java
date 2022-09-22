package de.neo.jagil.reader;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.neo.jagil.gui.GuiTypes;
import de.neo.jagil.ui.components.UIComponent;
import de.neo.jagil.util.ComponentUtil;
import de.neo.jagil.util.InventoryPosition;
import de.neo.jagil.util.ParseUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class JsonGuiReader extends GuiReader<JsonObject> {

    public JsonGuiReader() {
        super("json");
    }

    @Override
    public GuiTypes.DataGui read(String content) throws RuntimeException {
        GuiTypes.DataGui gui = new GuiTypes.DataGui();
        JsonObject json = new Gson().fromJson(content, JsonObject.class);

        gui.name = ParseUtil.getJsonString(json, "name");
        gui.size = json.get("size").getAsInt();
        gui.animationMod = ParseUtil.getJsonInt(json, "animationTick");

        if (json.has("items")) {
            parseItems(gui, json);
        }else if (json.has("ui")) {
            try {
                parseUI(gui, json);
            }catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else {
            Bukkit.getLogger().warning("[JAGIL] Empty GUI " + gui.name + ": no items section!");
            return gui;
        }

        return gui;
    }

    @Override
    public void parseItem(GuiTypes.DataGui gui, JsonObject json) {
        JsonObject jsonItem = json.getAsJsonObject();
        GuiTypes.GuiItem item = new GuiTypes.GuiItem();

        item.id = ParseUtil.getJsonString(jsonItem, "id");
        if(jsonItem.has("pos")) {
            item.slot = ParseUtil.getJsonPosition(jsonItem, "pos").toSlot();
        }else {
            if (jsonItem.has("slot")) {
                if (jsonItem.isJsonPrimitive()) {
                    item.slot = jsonItem.get("slot").getAsInt();
                }else {
                    JsonElement slotElem = jsonItem.get("slot").getAsJsonArray().get(0);
                    if (slotElem.isJsonObject()) {
                        item.slot = slotElem.getAsJsonObject().get("from").getAsInt();
                    }else {
                        item.slot = slotElem.getAsJsonPrimitive().getAsInt();
                    }
                }
            }else if (!item.id.isEmpty()) {
                item.slot = ParseUtil.getAutoSlotId(gui);
            }else throw new IllegalStateException("slot is not json");
        }
        item.material = Material.getMaterial(ParseUtil.getJsonString(jsonItem, "material"));
        item.name = ParseUtil.getJsonString(jsonItem, "name");
        item.amount = ParseUtil.getJsonInt(jsonItem, "amount");
        item.amount = item.amount == 0 ? 1 : item.amount;
        if(jsonItem.has("lore")) {
            for(JsonElement strElem : jsonItem.get("lore").getAsJsonArray()) {
                String str = strElem.getAsString();
                if (!str.matches("§[0-9a-fkl-or]")) {
                    str = ComponentUtil.convertToLegacy(MiniMessage.miniMessage().deserialize(str));
                }
                item.lore.add(str);
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
                item.attributes.put(attrJson.get("name").getAsString(), attrJson.get("value"));
            }
        }

        if(!jsonItem.has("slot") || !json.isJsonPrimitive()) {
            gui.items.put(item.slot, item);
        }

        if(jsonItem.has("fillTo")) {
            int fillMax = jsonItem.get("fillTo").getAsInt();
            for(int i = item.slot + 1; i <= fillMax; i++) {
                GuiTypes.GuiItem item2 = new GuiTypes.GuiItem(item);
                item2.slot = i;
                gui.items.put(i, item2);
            }
        }

        if(jsonItem.has("slot") && jsonItem.get("slot").isJsonArray()) {
            for(JsonElement fillElem : jsonItem.get("slot").getAsJsonArray()) {
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
                    Bukkit.getLogger().warning("[JAGIL] Invalid fill property item in GUI:" + fillElem);
                }
            }
        }
    }

    @Override
    public void parseUIComponent(GuiTypes.DataGui gui, JsonObject jsonUi) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        UIComponent component = ParseUtil.getUIComponent(jsonUi.get("type").getAsString(), jsonUi);
        gui.ui.put(component.getId(), component);
    }

    public void parseUI(GuiTypes.DataGui gui, JsonObject json) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for (JsonElement elem : json.get("ui").getAsJsonArray()) {
            parseUIComponent(gui, elem.getAsJsonObject());
        }
    }

    public void parseItems(GuiTypes.DataGui gui, JsonObject json) {
        for(JsonElement elem : json.get("items").getAsJsonArray()) {
            parseItem(gui, elem.getAsJsonObject());
        }
    }

    @Override
    public String getFileType() {
        return super.getFileType();
    }
}
