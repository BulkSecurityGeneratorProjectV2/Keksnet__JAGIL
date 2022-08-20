package de.neo.jagil.ui.impl;

import org.bukkit.event.inventory.ClickType;

import java.awt.*;

public class UIAction<T> {

    private final Class<T> uiType;

    private final Point click;

    private final ClickType clickType;

    public UIAction(Class<T> uiType, Point click, ClickType clickType) {
        this.uiType = uiType;
        this.click = click;
        this.clickType = clickType;
    }

    public Class<T> getUiType() {
        return uiType;
    }

    public Point getClick() {
        return click;
    }

    public ClickType getClickType() {
        return clickType;
    }

}
