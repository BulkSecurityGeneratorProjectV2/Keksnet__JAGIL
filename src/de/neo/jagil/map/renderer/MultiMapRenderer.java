package de.neo.jagil.map.renderer;

import de.neo.jagil.map.IMultiFrameMap;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.util.concurrent.atomic.AtomicInteger;

public class MultiMapRenderer extends MapRenderer implements IJAGILRenderer {

    private AtomicInteger imageId;
    private final int width;
    private final int height;
    private IMultiFrameMap map;

    public MultiMapRenderer(IMultiFrameMap map, int width, int height) {
        super();
        this.imageId = new AtomicInteger(0);
        this.map = map;
        this.width = width;
        this.height = height;
    }

    public MultiMapRenderer(IMultiFrameMap map, int width, int height, boolean context) {
        super(context);
        this.imageId = new AtomicInteger(0);
        this.map = map;
        this.width = width;
        this.height = height;
    }

    public void next() {
        imageId.incrementAndGet();
        if(imageId.get() >= map.getFrameCount()) {
            imageId.set(0);
        }
    }

    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
        mapCanvas.drawImage(0, 0, map.getFrame(imageId.get(), width, height));
    }
}
