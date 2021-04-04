package de.neo.jagil.manager;

import java.util.HashMap;

import de.neo.jagil.gui.GUI;

public class GUIManager {
	
	private static GUIManager INSTANCE;
	
	private HashMap<String, GUI> invs;
	
	public GUIManager() {
		INSTANCE = this;
		this.invs = new HashMap<>();
	}
	
	public void register(GUI gui) {
		this.invs.put(gui.getIdentifier(), gui);
	}
	
	public Boolean isGUIByJAGIL(String identifier) {
		return this.invs.containsKey(identifier);
	}
	
	public GUI getGUI(String identifier) {
		return this.invs.get(identifier);
	}
	
	public void unregister(String identifier) {
		this.invs.remove(identifier);
	}
	
	public void unregister(GUI gui) {
		this.invs.remove(gui.getIdentifier());
	}
	
	public static GUIManager getInstance() {
		return INSTANCE;
	}
}
