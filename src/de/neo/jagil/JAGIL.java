package de.neo.jagil;

import de.neo.jagil.listener.GUIListener;
import de.neo.jagil.manager.GuiReaderManager;
import de.neo.jagil.reader.JsonGuiReader;
import de.neo.jagil.reader.XmlGuiReader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.neo.jagil.manager.GUIManager;

import java.util.HashMap;

/**
 * JAGIL
 */
public class JAGIL {

	public static JavaPlugin loaderPlugin;

	static {
		GuiReaderManager.getInstance().register(new JsonGuiReader());
		GuiReaderManager.getInstance().register(new XmlGuiReader());
	}

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
