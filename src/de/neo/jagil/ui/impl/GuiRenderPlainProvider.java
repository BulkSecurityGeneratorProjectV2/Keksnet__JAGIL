package de.neo.jagil.ui.impl;

import de.neo.jagil.gui.GuiTypes;
import de.neo.jagil.ui.UIRenderPlainProvider;

public class GuiRenderPlainProvider implements UIRenderPlainProvider<GuiTypes.DataGui> {

    private final GuiTypes.DataGui data = new GuiTypes.DataGui();

    @Override
    public GuiTypes.DataGui getRenderPlain() {
        return this.data;
    }
}
