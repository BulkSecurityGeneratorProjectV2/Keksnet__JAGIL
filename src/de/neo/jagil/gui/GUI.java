package de.neo.jagil.gui;

import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import de.neo.jagil.manager.GUIManager;

public abstract class GUI {
	
	private String name;
	private Integer size;
	private InventoryType type;
	private OfflinePlayer p;
	private Inventory inv;
	
	/* Calculates how many slots are needed for all players. Do not use, when more than 54 players are online */
	public GUI(String name) {
		this.name = name;
		this.size = Integer.valueOf(String.valueOf((Math.floor(Bukkit.getOnlinePlayers().size() / 9) * 9) + 9).replace(".0", ""));
	}
	
	public GUI(String name, Integer size) {
		this.name = name;
		this.size = size;
	}
	
	public GUI(String name, InventoryType type) {
		this.name = name;
		this.type = type;
	}
	
	/* Calculates how many slots are needed for all players. Do not use, when more than 54 players are online */
	public GUI(String name, OfflinePlayer p) {
		this.name = name;
		this.size = Integer.valueOf(String.valueOf((Math.floor(Bukkit.getOnlinePlayers().size() / 9) * 9) + 9).replace(".0", ""));
		this.p = p;
		GUIManager.getInstance().register(this);
	}
	
	public GUI(String name, Integer size, OfflinePlayer p) {
		this.name = name;
		this.size = size;
		this.p = p;
		GUIManager.getInstance().register(this);
	}
	
	public GUI(String name, InventoryType type, OfflinePlayer p) {
		this.name = name;
		this.type = type;
		this.p = p;
		GUIManager.getInstance().register(this);
	}
	
	public String getName() {
		return this.name;
	}
	
	public Integer getSize() {
		return this.size;
	}
	
	public String getIdentifier() {
		return this.name + "-" + this.p.getUniqueId().toString();
	}
	
	public UUID getPlayerUUID() {
		return this.p.getUniqueId();
	}
	
	public Inventory getInventory() {
		return this.inv;
	}
	
	/* This method updates or creates the Inventory it. Call this before call show(); */
	public GUI update() {
		if(this.p != null) {
			this.updateInternal();
			return this;
		}
		throw new RuntimeException("This method should not be called on universal GUIs");
	}
	
	private GUI updateInternal() {
		if(this.inv == null) {
			if(this.size != 0) {
				this.inv = Bukkit.createInventory(null, this.size, this.name);
			}else {
				this.inv = Bukkit.createInventory(null, this.type, this.name);
			}
			this.fill();
		}else {
			this.fill();
			this.p.getPlayer().updateInventory();
		}
		return this;
	}
	
	public GUI show() {
		if(this.p != null) {
			this.p.getPlayer().openInventory(this.inv);
			this.p.getPlayer().updateInventory();
			return this;
		}
		throw new RuntimeException("Please use show(OfflinePlayer) for universal GUIs");
	}
	
	public GUI show(OfflinePlayer p) {
		if(p != null) {
			Logger.getLogger("JAGIL").warning("Using show(OfflinePlayer) for non-universal GUIs is dangerous. Please try to avoid it.");
		}
		this.p = p;
		GUIManager.getInstance().register(this);
		this.updateInternal();
		this.show();
		this.p = null;
		return this;
	}
	
	public abstract GUI fill();
	
	public abstract Boolean handle(InventoryClickEvent e);
	
	public GUI handleLast(InventoryClickEvent e) { return this; }
	
	public Boolean handleDrag(InventoryDragEvent e) { return true; }
	
	public GUI handleDragLast(InventoryDragEvent e) { return this; }
	
	public GUI handleClose(InventoryCloseEvent e) { return this; }
	
	public Boolean isCancelledByDefault() { return true; }
}