package de.neo.jagil.manager;

import java.util.HashMap;

import de.neo.jagil.annotation.Internal;
import de.neo.jagil.gui.GUI;
import de.neo.jagil.map.renderer.IJAGILRenderer;
import org.bukkit.map.MapRenderer;

public class RendererManager {

    private static RendererManager INSTANCE;

    private HashMap<String, Class<? extends IJAGILRenderer>> renderers;

    @Internal
    public RendererManager() {
        INSTANCE = this;
        this.renderers = new HashMap<>();
    }

    @Internal
    public void register(String identifier, Class<? extends IJAGILRenderer> renderer) {
        this.renderers.put(identifier, renderer);
    }

    public Boolean hasRenderer(String identifier) {
        return this.renderers.containsKey(identifier);
    }

    public <T extends IJAGILRenderer> Class<T> getRenderer(String identifier, Class<T> clazz) {
        return (Class<T>) this.renderers.get(identifier);
    }

    @Internal
    public void unregister(String identifier) {
        this.renderers.remove(identifier);
    }

    @Internal
    public static RendererManager getInstance() {
        return INSTANCE;
    }
}
