package de.neo.jagil.map.renderer;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;

public class SingleMapRenderer extends MapRenderer {

    private BufferedImage image;

    public SingleMapRenderer(BufferedImage image) {
        super();
        this.image = image;
    }

    public SingleMapRenderer(BufferedImage image, boolean contextual) {
        super(contextual);
        this.image = image;
    }

    @Override
    public void initialize(MapView map) {
        super.initialize(map);
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        mapCanvas.drawImage(0, 0, image);
    }
}
