package de.neo.jagil.ui;

import de.neo.jagil.ui.components.Clickable;
import de.neo.jagil.ui.components.UIComponent;

import java.awt.*;

public interface UISystem {

    int getSize();

    boolean hasComponent(String id);

    void addComponent(UIComponent component) throws IllegalArgumentException;

    UIComponent getComponent(String id);

    <T extends UIComponent & Clickable> T getClickedComponent(Point click);

    void removeComponent(UIComponent component);

    UIRenderPlainProvider<?> getRenderProvider();

    void render();

    void render(UIRenderPlainProvider<?> renderPlain);

}
