package de.neo.jagil.gui;

import java.util.UUID;
import java.util.logging.Logger;

import de.neo.jagil.JAGIL;
import de.neo.jagil.annotation.Internal;
import de.neo.jagil.annotation.NoCompatibilityMode;
import de.neo.jagil.annotation.OptionalImplementation;
import de.neo.jagil.annotation.UnstableFeature;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import de.neo.jagil.manager.GUIManager;

/**
 * Represents a {@link GUI}.
 * Extend this class to create your own GUI with {@link de.neo.jagil.JAGIL}.
 *
 * @version 2.0
 * @author Neo8
 */
public abstract class GUI {
	
	private String name;
	private int size;
	private InventoryType type;
	private OfflinePlayer p;
	private Inventory inv;

	private long cooldown;
	private long lastHandle;

	{
		cooldown = 10_000_000;
		lastHandle = 0;
	}

	/**
	 * Creates a new instance of the {@link GUI} class.
	 * Use this constructor when you like to create a universal {@link GUI} with a specific size.
	 *
	 * @param name name of the {@link Inventory}
	 * @param size size of the {@link Inventory}
	 */
	public GUI(String name, int size) {
		this(name, size, null);
	}

	/**
	 * Creates a new instance of the {@link GUI} class.
	 * Use this constructor when you like to create a universal {@link GUI} with a specific {@link InventoryType}.
	 *
	 * @param name name of the {@link Inventory}
	 * @param type {@link InventoryType} of the {@link Inventory}
	 */
	@UnstableFeature
	public GUI(String name, InventoryType type) {
		this(name, type, null);
	}

	/**
	 * Creates a new instance of the {@link GUI} class.
	 * Use this constructor when you like to create a non-universal {@link GUI} with a specific size.
	 *
	 * @param name name of the {@link Inventory}
	 * @param size size of the {@link Inventory}
	 * @param p the {@link org.bukkit.entity.Player} that should see this {@link Inventory}.
	 */
	public GUI(String name, int size, OfflinePlayer p) {
		this.name = name;
		this.size = size;
		this.p = p;
		if(this.p != null) {
			GUIManager.getInstance().register(this);
		}
	}

	/**
	 * Creates a new instance of the {@link GUI} class.
	 * Use this constructor when you like to create a non-universal {@link GUI} with a specific size.
	 *
	 * @param name name of the {@link Inventory}
	 * @param type {@link InventoryType} of the {@link Inventory}
	 * @param p the {@link org.bukkit.entity.Player} that should see this {@link Inventory}.
	 */
	@UnstableFeature
	public GUI(String name, InventoryType type, OfflinePlayer p) {
		this.name = name;
		this.type = type;
		this.p = p;
		if(this.p != null) {
			GUIManager.getInstance().register(this);
		}
	}

	public String getName() {
		return this.name;
	}
	
	public int getSize() {
		return this.size;
	}

	@Internal(forVisibilityChange = false)
	public String getIdentifier() {
		return this.name + "-" + (this.p != null ? this.p.getUniqueId() : "universal");
	}
	
	public UUID getPlayerUUID() {
		return this.p.getUniqueId();
	}

	public Player getPlayer() {
		return this.p.getPlayer();
	}
	
	public Inventory getInventory() {
		return this.inv;
	}

	public void setCooldown(long cooldown) {
		this.cooldown = cooldown;
	}

	public long getCooldown() {
		return this.cooldown;
	}

	/**
	 * Closes the {@link Inventory} of this {@link GUI} save.
	 */
	@NoCompatibilityMode
	public void closeInventory() {
		Bukkit.getScheduler().runTask(JAGIL.getPlugin(this.getClass().getName()), () -> {
			getPlayer().closeInventory();
		});
	}

	/**
	 * This method is called to create an Inventory.
	 * This is called by {@link GUI#show()} automatically.
	 *
	 * @return instance for chaining
	 */
	@Internal(forVisibilityChange = true)
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

	/**
	 * Call this method to open the inventory of a non-universal {@link GUI}.
	 *
	 * @throws RuntimeException if the internal {@link Player} is null or this is used on a universal {@link GUI}
	 * @return instance for chaining
	 */
	public GUI show() {
		this.update();
		if(this.p != null) {
			if(JAGIL.getPlugin(this.getClass().getName()) != null) {
				if(getPlayer().getInventory() != null) {
					Bukkit.getScheduler().runTask(JAGIL.getPlugin(this.getClass().getName()), () -> this.p.getPlayer().openInventory(this.inv));
				}else {
					this.p.getPlayer().openInventory(this.inv);
				}
			}else {
				this.p.getPlayer().openInventory(this.inv);
			}
			this.p.getPlayer().updateInventory();
			return this;
		}
		throw new RuntimeException("Please use show(OfflinePlayer) for universal GUIs");
	}

	/**
	 * Call this method to open the {@link Inventory} of a universal {@link GUI} for a specific {@link Player}.
	 *
	 * @param p player that should see the {@link Inventory}
	 * @return instance for chaining
	 */
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

	/**
	 * Fills this {@link GUI}
	 *
	 * @return instance for chaining
	 */
	public abstract GUI fill();

	@Internal
	public boolean handleInternal(InventoryClickEvent e) {
		if(System.nanoTime() - this.lastHandle <= this.cooldown) return true;
		this.lastHandle = System.nanoTime();
		return handle(e);
	}

	/**
	 * Called on an {@link InventoryClickEvent} in this {@link Inventory}.
	 *
	 * @param e the fired {@link InventoryClickEvent}
	 * @return if the event should be cancelled or not.
	 */
	public abstract boolean handle(InventoryClickEvent e);

	/**
	 * Like {@link GUI#handle(InventoryClickEvent)} but optional and one tick later.
	 *
	 * @param e the fired {@link InventoryClickEvent}
	 * @return instance for chaining
	 */
	@OptionalImplementation
	public GUI handleLast(InventoryClickEvent e) { return this; }

	/**
	 * Called on an {@link InventoryDragEvent} in this {@link Inventory}.
	 *
	 * @param e the fired {@link InventoryDragEvent}
	 * @return if the event should be cancelled or not.
	 */
	@OptionalImplementation
	public boolean handleDrag(InventoryDragEvent e) { return true; }

	/**
	 * Like {@link GUI#handleDrag(InventoryDragEvent)} but optional and one tick later.
	 *
	 * @param e the fired {@link InventoryClickEvent}
	 * @return instance for chaining
	 */
	@OptionalImplementation
	public GUI handleDragLast(InventoryDragEvent e) { return this; }

	/**
	 * Like {@link GUI#handleDrag(InventoryDragEvent)} but optional and one tick later.
	 *
	 * @param e the fired {@link InventoryClickEvent}
	 * @return instance for chaining
	 */
	@OptionalImplementation
	public GUI handleClose(InventoryCloseEvent e) { return this; }

	/**
	 * Returns if the event should cancelled by default.
	 *
	 * @return the default cancel-value
	 */
	@OptionalImplementation
	public boolean isCancelledByDefault() { return true; }
}