package de.neo.jagil.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import de.neo.jagil.JAGIL;
import de.neo.jagil.annotation.Internal;
import de.neo.jagil.debug.Hook;
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
		unregister(gui);
		this.inventories.put(gui.getIdentifier(), gui);
		if(JAGIL.debugMode) {
			JAGIL.debugger.executeHook(Hook.REGISTER_GUI, gui, this);
		}
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
