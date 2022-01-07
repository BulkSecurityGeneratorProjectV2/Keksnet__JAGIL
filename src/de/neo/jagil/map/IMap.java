package de.neo.jagil.map;

import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public interface IMap {

    void setName(String name);

    String getName();

    void calculateMap(BufferedImage image);

    void calculateMap(BufferedImage image, World world);

    HashMap<Point, ItemStack> build();
}
