package de.neo.jagil.map;

import java.awt.image.BufferedImage;

public class AnimatedMap extends MultiFrameMap {

    public AnimatedMap(String name, BufferedImage frame, long cooldown) {
        super(name, frame);
    }

    public AnimatedMap(String name, BufferedImage image, long cooldown, boolean isDynamic) {
        super(name, image, isDynamic);
    }
}