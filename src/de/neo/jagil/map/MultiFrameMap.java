package de.neo.jagil.map;

import de.neo.jagil.JAGIL;
import de.neo.jagil.manager.RendererManager;
import de.neo.jagil.map.renderer.MultiMapRenderer;
import de.neo.jagil.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class MultiFrameMap extends ImageMap implements IMultiFrameMap {

    private final HashMap<String, HashMap<Point, BufferedImage>> images;
    private final HashMap<String, BufferedImage> frames;
    private final HashMap<Point, MapView> views;

    public MultiFrameMap(String name, BufferedImage frame) {
        super(name, frame);
        this.images = new HashMap<>();
        this.frames = new HashMap<>();
        this.views = new HashMap<>();
    }

    public MultiFrameMap(String name, BufferedImage image, boolean isDynamic) {
        super(name, image, isDynamic);
        this.images = new HashMap<>();
        this.frames = new HashMap<>();
        this.views = new HashMap<>();
    }

    @Override
    public void calculateMap() {
        calculateMap(null, Bukkit.getWorlds().get(0));
    }

    @Override
    public void calculateMap(BufferedImage image) {
        super.calculateMap(image, Bukkit.getWorlds().get(0));
    }

    @Override
    public void calculateMap(BufferedImage image, World world) {
        for(Map.Entry<String, BufferedImage> entry : this.frames.entrySet()) {
            super.calculateMap(entry.getValue());
            images.put(entry.getKey(), this.maps);
            maps.clear();
        }
        frames.clear();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    protected void setCustomRenderer(MapView view, int width, int height, BufferedImage image) {
        view.getRenderers().forEach(view::removeRenderer);
        Class<MultiMapRenderer> rendererClass = RendererManager.getInstance().getRenderer("MultiMapRenderer", MultiMapRenderer.class);
        MultiMapRenderer renderer;
        try {
            renderer = rendererClass.getConstructor(MultiFrameMap.class, int.class, int.class, boolean.class)
                    .newInstance(this, width, height, this.isDynamic);
        }catch (NoSuchMethodException e) {
            JAGIL.getLoader().getLogger().warning("Could not find constructor for MultiMapRenderer");
            return;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
        view.addRenderer(renderer);
        view.setLocked(false);
    }

    @Override
    public void addFrame(Pair<String, BufferedImage> frame) {
        this.frames.put(frame.getKey(), frame.getValue());
    }

    @Override
    public void addFrames(Pair<String, BufferedImage>... frames) {
        for(Pair<String, BufferedImage> frame : frames) {
            this.frames.put(frame.getKey(), frame.getValue());
        }
    }

    @Override
    public int getFrameCount() {
        return images.size();
    }

    @Override
    public void nextFrame() {
        this.views.values().forEach(view -> ((MultiMapRenderer) view.getRenderers().get(0)).next());
    }

    @Override
    public BufferedImage getFrame(int index, int width, int height) {
        return this.images.get(this.images.keySet().toArray(new String[0])[width]).get(new Point(width, height));
    }

    @Override
    public void removeFrame(String name) {
        this.frames.remove(name);
    }

    @Override
    public void removeFrames(String... names) {
        for(String name : names) {
            this.frames.remove(name);
        }
    }

    @Override
    public HashMap<Point, ItemStack> build() {
        return super.build();
    }
}
