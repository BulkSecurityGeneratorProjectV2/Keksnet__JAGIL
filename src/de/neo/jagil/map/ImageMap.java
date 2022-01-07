package de.neo.jagil.map;

import de.neo.jagil.map.renderer.SingleMapRenderer;
import de.neo.jagil.util.ItemTool;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class ImageMap implements IMap {

    private String name;
    private int width;
    private int height;
    protected boolean calculated;
    protected final HashMap<Point, MapView> view;
    protected final HashMap<Point, BufferedImage> maps;
    protected final boolean isDynamic;

    public ImageMap(String name, BufferedImage image) {
        this(name, image, false);
    }

    public ImageMap(String name, BufferedImage image, boolean isDynamic) {
        this.view = new HashMap<>();
        this.maps = new HashMap<>();
        this.isDynamic = true;
        calculateMap(image);
    }

    public void calculateMap(BufferedImage image) {
        calculateMap(image, Bukkit.getWorlds().get(0));
    }

    public void calculateMap(BufferedImage image, World world) {
        this.width = (int) Math.round((image.getWidth(null) / 128) + 0.5);
        this.height = (int) Math.round((image.getHeight(null) / 128) + 0.5);
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                Point p = new Point(x, y);
                if(x * 128 + 128 < w && y * 128 + 128 < h) {
                    MapView view = Bukkit.createMap(world);
                    BufferedImage subImage = image.getSubimage(x * 128, y * 128, 128, 128);
                    setCustomRenderer(view, x * 128, y * 128, subImage);
                    this.view.put(p, view);
                    this.maps.put(p, subImage);
                }else if(x * 128 < w && y * 128 < h) {
                    int clipWidth = w - x * 128;
                    int clipHeight = h - y * 128;
                    MapView view = Bukkit.createMap(world);
                    BufferedImage subImage = image.getSubimage(x * 128, y * 128, clipWidth, clipHeight);
                    setCustomRenderer(view, x * 128, y * 128, subImage);
                    this.view.put(p, view);
                    this.maps.put(p, subImage);
                }
            }
        }
        this.calculated = true;
    }

    protected void setCustomRenderer(MapView view, int width, int height, BufferedImage image) {
        view.getRenderers().forEach(view::removeRenderer);
        view.addRenderer(new SingleMapRenderer(image, this.isDynamic));
        view.setLocked(false);
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public HashMap<Point, ItemStack> build() {
        if(!calculated) {
            throw new IllegalStateException("Map is not calculated yet!");
        }
        HashMap<Point, ItemStack> items = new HashMap<>();
        for(int w = 0; w < width; w++) {
            for(int h = 0; h < height; h++) {
                Point point = new Point(w, h);
                BufferedImage image = this.maps.get(point);
                ItemStack map = ItemTool.createItem(this.name.replace("$i", String.valueOf(h * height + w)),
                        Material.FILLED_MAP);
                MapMeta mapMeta = (MapMeta) map.getItemMeta();
                mapMeta.setMapView(this.view.get(point));
                map.setItemMeta(mapMeta);
            }
        }
        return items;
    }
}
