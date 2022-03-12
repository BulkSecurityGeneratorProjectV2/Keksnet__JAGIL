package de.neo.jagil;

import de.neo.jagil.annotation.DeprecatedDefaults;
import de.neo.jagil.annotation.NoCompatibilityMode;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.neo.jagil.listener.GUIListener;
import de.neo.jagil.manager.GUIManager;

import java.util.HashMap;

/**
 * JAGIL
 */
public class JAGIL {

	private static HashMap<String, JavaPlugin> plugins;
	public static JavaPlugin loaderPlugin;

	static {
		plugins = new HashMap<>();
	}

	/**
	 * Initializes JAGIL.
	 * Calls {@link JAGIL#init(JavaPlugin, boolean)} with plugin and true.
	 *
	 * @param plugin your {@link JavaPlugin} instance.
	 */
	@DeprecatedDefaults(forChange = true)
	public static void init(JavaPlugin plugin) {
		init(plugin, true);
	}

	/**
	 * Initializes JAGIL.
	 *
	 * @param plugin your {@link JavaPlugin} instance.
	 * @param compatibilityMode enables compatibilityMode. This disables methods with the {@link de.neo.jagil.annotation.NoCompatibilityMode} annotation.
	 */
	public static void init(JavaPlugin plugin, boolean compatibilityMode) {
		plugin.getLogger().info("Registered JAGIL from " + plugin.getDescription().getName() + " " + plugin.getDescription().getVersion());
		new GUIManager();
		Bukkit.getPluginManager().registerEvents(new GUIListener(plugin), plugin);
		if(!compatibilityMode) {
			String pluginName = plugin.getClass().getName();
			String[] packages = pluginName.split("[.]");
			String savedName = "";
			if(packages.length >= 3) {
				for(int i = 0; i < 3; i++) {
					savedName += packages[i];
					if(i != 2) {
						savedName += ".";
					}
				}
			}
			plugins.put(savedName, plugin);
		}
	}

	/**
	 * Return the plugin instance for an {@link de.neo.jagil.gui.GUI}.
	 *
	 * @param clazz the {@link Class#getName()} of the {@link de.neo.jagil.gui.GUI}
	 * @return the instance of the {@link JavaPlugin}
	 */
	@NoCompatibilityMode
	public static JavaPlugin getPlugin(String clazz) {
		String pluginName = clazz;
		String[] packages = pluginName.split("[.]");
		String savedName = "";
		if(packages.length >= 3) {
			for(int i = 0; i < 3; i++) {
				savedName += packages[i];
				if(i != 2) {
					savedName += ".";
				}
			}
		}
		return plugins.get(savedName);
	}
}
