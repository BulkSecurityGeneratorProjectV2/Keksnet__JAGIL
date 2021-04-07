package de.neo.jagil;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.neo.jagil.listener.GUIListener;
import de.neo.jagil.manager.GUIManager;

public class JAGIL {
	
	public static void init(JavaPlugin plugin) {
		new GUIManager();
		Bukkit.getPluginManager().registerEvents(new GUIListener(plugin), plugin);
	}
}
