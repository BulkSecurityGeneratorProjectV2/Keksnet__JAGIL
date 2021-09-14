package de.neo.jagil.listener;

import de.neo.jagil.annotation.Internal;
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
	
	private JavaPlugin plugin;

	@Internal
	public GUIListener(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@Internal
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getClickedInventory() != null && e.getInventory() != null) {
			if(GUIManager.getInstance().isGUIByJAGIL(e.getView().getTitle() + "-" + e.getWhoClicked().getUniqueId().toString())) {
				GUI gui = GUIManager.getInstance().getGUI(e.getView().getTitle() + "-" + e.getWhoClicked().getUniqueId().toString());
				Boolean cancel = gui.isCancelledByDefault();
				if(gui != null) {
					if(e.getCurrentItem() != null && e.getClickedInventory() != e.getWhoClicked().getInventory()) {
						cancel = gui.handleInternal(e);
					}
				}
				e.setCancelled(cancel);
				if(cancel) {
					((Player)e.getWhoClicked()).updateInventory();
				}
				Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable() {
					
					@Override
					public void run() {
						gui.handleLast(e);
					}
				}, 1l);
			}
		}
	}

	@Internal
	@EventHandler
	public void onDrag(InventoryDragEvent e) {
		if(GUIManager.getInstance().isGUIByJAGIL(e.getView().getTitle() + "-" + e.getWhoClicked().getUniqueId().toString())) {
			GUI gui = GUIManager.getInstance().getGUI(e.getView().getTitle() + "-" + e.getWhoClicked().getUniqueId().toString());
			Boolean cancel = true;
			if(gui != null) {
				cancel = gui.handleDrag(e);
			}
			e.setCancelled(cancel);
			Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable() {
				
				@Override
				public void run() {
					gui.handleDragLast(e);
				}
			}, 1l);
			((Player)e.getWhoClicked()).updateInventory();
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