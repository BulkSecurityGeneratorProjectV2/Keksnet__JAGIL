package de.neo.jagil.ui.components;

import java.awt.*;

public interface Resizeable extends Dynamic {

    void resize(int width, int height);

    void resize(Dimension size);

}
