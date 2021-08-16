package de.neo.jagil;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.neo.jagil.listener.GUIListener;
import de.neo.jagil.manager.GUIManager;

/**
 * JAGIL
 */
public class JAGIL {

	/**
	 * Initializes JAGIL.
	 *
	 * @param plugin your {@link JavaPlugin} instance.
	 */
	public static void init(JavaPlugin plugin) {
		plugin.getLogger().info("Registered JAGIL from " + plugin.getDescription().getName() + " " + plugin.getDescription().getVersion());
		new GUIManager();
		Bukkit.getPluginManager().registerEvents(new GUIListener(plugin), plugin);
	}
}
