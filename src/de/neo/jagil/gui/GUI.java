package de.neo.jagil.gui;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

import de.neo.jagil.manager.GUIManager;

public abstract class GUI {
	
	private String name;
	private Integer size;
	private OfflinePlayer p;
	private Inventory inv;
	
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
		if(this.inv == null) {
			this.inv = Bukkit.createInventory(null, this.size, this.name);
			this.fill();
		}else {
			this.fill();
			this.p.getPlayer().updateInventory();
		}
		return this;
	}
	
	public GUI show() {
		this.p.getPlayer().openInventory(this.inv);
		this.p.getPlayer().updateInventory();
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