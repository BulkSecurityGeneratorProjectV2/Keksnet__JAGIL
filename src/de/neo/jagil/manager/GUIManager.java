package de.neo.jagil.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import de.neo.jagil.annotation.Internal;
import de.neo.jagil.gui.GUI;

public class GUIManager {
	
	private static GUIManager INSTANCE;
	
	private HashMap<String, GUI> inventories;

	@Internal
	public GUIManager() {
		INSTANCE = this;
		this.inventories = new HashMap<>();
	}

	@Internal
	public void register(GUI gui) {
		this.inventories.remove(gui.getIdentifier());
		this.inventories.put(gui.getIdentifier(), gui);
	}

	public Boolean isGUIByJAGIL(String identifier) {
		return this.inventories.containsKey(identifier);
	}

	public GUI getGUI(String identifier) {
		return this.inventories.get(identifier);
	}

	@Internal
	public void unregister(String identifier) {
		this.inventories.remove(identifier);
	}

	@Internal
	public void unregister(GUI gui) {
		this.inventories.remove(gui.getIdentifier());
	}

	@Internal
	public static GUIManager getInstance() {
		return INSTANCE;
	}
}
