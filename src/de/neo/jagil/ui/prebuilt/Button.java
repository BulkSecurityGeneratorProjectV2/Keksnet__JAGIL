package de.neo.jagil.ui.prebuilt;

import com.google.gson.JsonObject;
import de.neo.jagil.gui.GuiTypes;
import de.neo.jagil.ui.UIRenderPlainProvider;
import de.neo.jagil.ui.components.Clickable;
import de.neo.jagil.ui.components.JsonParsable;
import de.neo.jagil.ui.components.UIComponent;
import de.neo.jagil.ui.impl.UIAction;
import de.neo.jagil.util.InventoryPosition;
import de.neo.jagil.util.ParseUtil;
import org.bukkit.Material;

import java.awt.*;

public class Button implements UIComponent, Clickable, JsonParsable {

    private final String id;
    private final Point position;
    private final Dimension size;
    private final int priority;
    private final Rectangle bounds;
    private final Material material;
    private final boolean border;
    private final Material borderMaterial;

    public Button(JsonObject json) {
        this(json.get("id").getAsString(),
                Material.getMaterial(ParseUtil.getJsonStringOrNull(json, "material")),
                Material.getMaterial(ParseUtil.getJsonStringOrNull(json, "borderMaterial")),
                new Rectangle(ParseUtil.getPosition(json, "pos"), ParseUtil.getSize(json, "size")));
    }

    public Button(String id, Material material, Rectangle bounds) {
        this(id, material, null, bounds);
    }

    public Button(String id, Material material, Material borderMaterial, Rectangle bounds) {
        this.id = id;
        this.position = bounds.getLocation();
        this.size = bounds.getSize();
        this.priority = 0;
        this.bounds = bounds;
        this.material = material;
        this.border = borderMaterial != null;
        this.borderMaterial = borderMaterial;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public Dimension getSize() {
        return size;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void render(UIRenderPlainProvider<?> renderPlainProvider) {
        Object renderPlain = renderPlainProvider.getRenderPlain();
        if (renderPlain instanceof GuiTypes.DataGui) {
            GuiTypes.DataGui gui = (GuiTypes.DataGui) renderPlain;
            InventoryPosition slot = new InventoryPosition(position.x, position.y);
            // TODO: Render button
            if (size.width >= 3 && size.height >= 3 && border) {
                // TODO: Render border
                GuiTypes.GuiItem borderItem = new GuiTypes.GuiItem();
                borderItem.material = borderMaterial;
                for (int i = 0; i < size.width; i++) {
                    GuiTypes.GuiItem copy = new GuiTypes.GuiItem(borderItem);
                    copy.slot = slot.toSlot();
                    gui.items.put(slot.toSlot(), copy);
                    slot.move(1, 0);
                }
                slot.move(-1, 0);
                for (int i = 0; i < size.height; i++) {
                    GuiTypes.GuiItem copy = new GuiTypes.GuiItem(borderItem);
                    copy.slot = slot.toSlot();
                    gui.items.put(slot.toSlot(), copy);
                    slot.move(0, 1);
                }
                slot.move(0, -1);
                for (int i = 0; i < size.width; i++) {
                    GuiTypes.GuiItem copy = new GuiTypes.GuiItem(borderItem);
                    copy.slot = slot.toSlot();
                    gui.items.put(slot.toSlot(), copy);
                    slot.move(-1, 0);
                }
                slot.move(1, 0);
                for (int i = 0; i < size.height; i++) {
                    GuiTypes.GuiItem copy = new GuiTypes.GuiItem(borderItem);
                    copy.slot = slot.toSlot();
                    gui.items.put(slot.toSlot(), copy);
                    slot.move(0, -1);
                }
            }
            slot.set(position.x, position.y);
        }
    }

    @Override
    public void click(UIAction<?> click) {
    }
}
