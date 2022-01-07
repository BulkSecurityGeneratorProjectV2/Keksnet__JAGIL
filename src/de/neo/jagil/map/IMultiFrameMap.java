package de.neo.jagil.map;

import de.neo.jagil.util.Pair;

import java.awt.image.BufferedImage;

public interface IMultiFrameMap extends IMap {

    void calculateMap();

    void addFrame(Pair<String, BufferedImage> frame);

    void addFrames(Pair<String, BufferedImage>... frames);

    int getFrameCount();

    void nextFrame();

    BufferedImage getFrame(int index, int width, int height);

    void removeFrame(String name);

    void removeFrames(String... names);

}
