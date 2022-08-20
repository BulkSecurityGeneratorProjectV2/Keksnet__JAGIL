package de.neo.jagil.ui.prebuilt;

import com.google.gson.JsonObject;
import de.neo.jagil.gui.GuiTypes;
import de.neo.jagil.ui.UIRenderPlainProvider;
import de.neo.jagil.ui.components.Clickable;
import de.neo.jagil.ui.components.JsonParsable;
import de.neo.jagil.ui.components.UIComponent;
import de.neo.jagil.ui.impl.UIAction;
import de.neo.jagil.util.ParseUtil;

import java.awt.*;

public class Button implements UIComponent, Clickable, JsonParsable {

    private final String id;
    private Point position;
    private Dimension size;
    private int priority;
    private Rectangle bounds;

    public Button(JsonObject json) {
        this.id = json.get("id").getAsString();
        this.position = ParseUtil.getPosition(json, "pos");
        this.size = ParseUtil.getSize(json, "size");
        this.priority = ParseUtil.getJsonInt(json, "priority");
        this.bounds = new Rectangle(position, size);
    }

    public Button(String id, Rectangle bounds) {
        this.id = id;
        this.position = bounds.getLocation();
        this.size = bounds.getSize();
        this.priority = 0;
        this.bounds = bounds;
    }

    public Button(String id, Point position, Dimension size) {
        this.id = id;
        this.position = position;
        this.size = size;
        this.priority = 0;
        this.bounds = new Rectangle(position, size);
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
            // TODO: Render button
        }
    }

    @Override
    public void click(UIAction<?> click) {

    }
}
