package de.neo.jagil.ui.components;

import de.neo.jagil.ui.UIRenderPlainProvider;

import java.awt.*;

public interface UIComponent {

    Point getPosition();

    Dimension getSize();

    Rectangle getBounds();

    int getPriority();

    void render(UIRenderPlainProvider<?> renderPlain);

    String getId();

}
