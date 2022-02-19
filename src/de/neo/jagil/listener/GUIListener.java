package de.neo.jagil.listener;

import de.neo.jagil.annotation.Internal;
import de.neo.jagil.exception.JAGILException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.plugin.java.JavaPlugin;

import de.neo.jagil.gui.GUI;
import de.neo.jagil.manager.GUIManager;

public class GUIListener implements Listener {
	
	private final JavaPlugin plugin;

	@Internal
	public GUIListener(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@Internal
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getClickedInventory() != null) {
			if (GUIManager.getInstance().isGUIByJAGIL(e.getView().getTitle() + "-" + e.getWhoClicked().getUniqueId())) {
				GUI gui = GUIManager.getInstance().getGUI(e.getView().getTitle() + "-" + e.getWhoClicked().getUniqueId());
				if (gui == null) {
					e.setCancelled(true);
					throw new JAGILException("Internal error: GUI is null but registered in GUIManager!");
				}
				boolean cancel = gui.isCancelledByDefault();
				if (e.getCurrentItem() != null && e.getClickedInventory() != e.getWhoClicked().getInventory()) {
					try {
						cancel = gui.handleInternal(e);
					}catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				e.setCancelled(cancel);
				if (cancel) {
					((Player) e.getWhoClicked()).updateInventory();
				}
				Bukkit.getScheduler().runTaskLater(this.plugin, () -> gui.handleLast(e), 1L);
			}
		}
	}

	@Internal
	@EventHandler
	public void onDrag(InventoryDragEvent e) {
		if(GUIManager.getInstance().isGUIByJAGIL(e.getView().getTitle() + "-" + e.getWhoClicked().getUniqueId())) {
			GUI gui = GUIManager.getInstance().getGUI(e.getView().getTitle() + "-" + e.getWhoClicked().getUniqueId());
			if(gui == null) {
				e.setCancelled(true);
				throw new JAGILException("Internal error: GUI is null but registered in GUIManager!");
			}
			boolean cancel = gui.isCancelledByDefault();
			try {
				cancel = gui.handleDrag(e);
			}catch (Exception ex) {
				ex.printStackTrace();
			}
			e.setCancelled(cancel);
			if(cancel) {
				((Player)e.getWhoClicked()).updateInventory();
			}
			Bukkit.getScheduler().runTaskLater(this.plugin, () -> gui.handleDragLast(e), 1L);
		}
	}

	@Internal
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		if(GUIManager.getInstance().isGUIByJAGIL(e.getView().getTitle() + "-" + e.getPlayer().getUniqueId())) {
			GUI gui = GUIManager.getInstance().getGUI(e.getView().getTitle() + "-" + e.getPlayer().getUniqueId());
			if(gui != null) {
				if(e.getInventory() != e.getPlayer().getInventory()) {
					gui.handleClose(e);
				}
			}
			GUIManager.getInstance().unregister(e.getView().getTitle() + "-" + e.getPlayer().getUniqueId());
			((Player)e.getPlayer()).updateInventory();
		}
	}
}