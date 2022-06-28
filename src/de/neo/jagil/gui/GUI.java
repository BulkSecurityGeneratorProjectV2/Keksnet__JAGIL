package de.neo.jagil.gui;

import de.neo.jagil.JAGIL;
import de.neo.jagil.annotation.Internal;
import de.neo.jagil.annotation.OptionalImplementation;
import de.neo.jagil.annotation.UnstableFeature;
import de.neo.jagil.manager.GUIManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Represents a {@link GUI}.
 * Extend this class to create your own GUI with {@link de.neo.jagil.JAGIL}.
 *
 * @version 4.0
 * @author Neo8
 */
public abstract class GUI {
	
	private String name;
	private int size;
	private InventoryType type;
	private OfflinePlayer p;
	private Inventory inv;
	private GuiTypes.DataGui guiData;
	protected HashMap<String, Integer> itemIds;
	public int animationTaskId;

	private long cooldown;
	private long lastHandle;

	{
		cooldown = 50;
	}

	/**
	 * Creates a new instance of the {@link GUI} class.
	 * Use this constructor when you like to load a GUI from the {@link GuiTypes.DataGui} class.
	 * Use this constructor when you like to create a universal {@link GUI}.
	 *
	 * @param gui the {@link GuiTypes.DataGui} class to load the GUI from.
	 */
	public GUI(GuiTypes.DataGui gui) {
		this.guiData = gui;
		this.name = gui.name;
		this.size = gui.size;
		this.itemIds = new HashMap<>();
		for(GuiTypes.GuiItem item : gui.items.values()) {
			this.itemIds.put(item.id, item.slot);
		}
	}

	/**
	 * Creates a new instance of the {@link GUI} class.
	 * Use this constructor when you like to load a GUI from the {@link GuiTypes.DataGui} class.
	 * Use this constructor when you like to create a non-universal {@link GUI}.
	 *
	 * @param gui the {@link GuiTypes.DataGui} class to load the GUI from.
	 */
	public GUI(GuiTypes.DataGui gui, OfflinePlayer p) {
		this.guiData = gui;
		this.name = gui.name;
		this.size = gui.size;
		this.p = p;
		this.itemIds = new HashMap<>();
		for(GuiTypes.GuiItem item : gui.items.values()) {
			this.itemIds.put(item.id, item.slot);
		}
		register();
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
		register();
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
	 * @param type {@link InventoryType} of the {@link Inventory}
	 * @param p the {@link org.bukkit.entity.Player} that should see this {@link Inventory}.
	 */
	@UnstableFeature
	public GUI(String name, InventoryType type, OfflinePlayer p) {
		this.name = name;
		this.type = type;
		this.p = p;
		register();
	}

	private void register() {
		if(this.p == null) return;
		Bukkit.getScheduler().runTaskLater(JAGIL.loaderPlugin, () -> {
			GUIManager.getInstance().register(this);
		}, 1L);
	}

	public final GUI setName(String name) {
		this.name = name;
		register();
		return this;
	}

	public final String getName() {
		return this.name;
	}
	
	public final int getSize() {
		return this.size;
	}
	
	public final UUID getPlayerUUID() {
		return this.p.getUniqueId();
	}

	public final Player getPlayer() {
		return this.p.getPlayer();
	}
	
	public final Inventory getInventory() {
		return this.inv;
	}

	public final void setCooldown(long cooldown) {
		this.cooldown = cooldown;
	}

	public final long getCooldown() {
		return this.cooldown;
	}

	public final GuiTypes.DataGui getGuiData() {
		return this.guiData;
	}

	/**
	 * Closes the {@link Inventory} of this {@link GUI} save.
	 */
	public final void closeInventory() {
		Bukkit.getScheduler().runTask(JAGIL.loaderPlugin, () -> getPlayer().closeInventory());
	}

	private void updateInternal() {
		if(this.inv == null) {
			if(this.size != 0) {
				this.inv = Bukkit.createInventory(null, this.size, this.name);
			}else {
				this.inv = Bukkit.createInventory(null, this.type, this.name);
			}
			this.fill();
		}else {
			this.fill();
			getPlayer().updateInventory();
		}
	}

	/**
	 * This method is called to create an Inventory.
	 * This is called by {@link GUI#show()} automatically.
	 */
	@Internal
	protected final void update() {
		if(this.p == null) throw new RuntimeException("This method should not be called on universal GUIs");
		this.updateInternal();
	}

	/**
	 * Call this method to open the inventory of a non-universal {@link GUI}.
	 *
	 * @throws RuntimeException if the internal {@link Player} is null or this is used on a universal {@link GUI}
	 * @return instance for chaining
	 */
	public final GUI show() {
		this.update();
		if(this.p != null) {
			register();
			if(!Bukkit.isPrimaryThread()) {
				Bukkit.getScheduler().runTask(JAGIL.loaderPlugin, () -> getPlayer().openInventory(this.inv));
			}else {
				getPlayer().openInventory(this.inv);
			}
			getPlayer().updateInventory();

			AtomicInteger ticks = new AtomicInteger(0);

			if(animationTaskId != -1) Bukkit.getScheduler().cancelTask(animationTaskId);

			animationTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(JAGIL.loaderPlugin, () -> {
				if(this.inv == null) return;
				animate(ticks.incrementAndGet());
			}, 0L, 1L);

			Bukkit.getScheduler().runTaskLater(JAGIL.loaderPlugin, () -> GUIManager.getInstance().lockIfNotLocked(getIdentifier()), 1L);
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
	public final GUI show(OfflinePlayer p) {
		if (p != null) {
			Logger.getLogger("JAGIL")
					.warning("Using show(OfflinePlayer) for non-universal GUIs is dangerous. Please try to avoid it.");
		}
		this.p = p;
		register();
		this.updateInternal();
		this.show();
		this.p = null;
		return this;
	}

	/**
	 * Fills this {@link GUI}
	 */
	public void fill() {
		if(guiData == null) return;
		for(GuiTypes.GuiItem guiItem : guiData.items.values()) {
			if(guiItem.slot < 0) continue;
			ItemStack is = guiItem.toItem();
			this.inv.setItem(guiItem.slot, is);
		}
	}

	@Internal
	public final boolean handleInternal(InventoryClickEvent e) {
		if(System.currentTimeMillis() - this.lastHandle <= this.cooldown) {
			handleBlocked(e);
			return isCancelledByDefault();
		}
		this.lastHandle = System.currentTimeMillis();
		return handle(e);
	}

	/**
	 * This method is called once a tick to animate the {@link GUI}.
	 *
	 * @param tick the current tick after the {@link GUI} was opened
	 */
	public void animate(long tick) {
		if(guiData == null) return;
		if(guiData.animationMod == 0) return;
		if(tick % guiData.animationMod != 0) return;
		for(GuiTypes.GuiItem guiItem : guiData.items.values()) {
			if(guiItem.slot < 0) continue;
			if(guiItem.animationFrames.isEmpty()) continue;
			ItemStack is = guiData.getItem(guiItem.animationFrames.get((int) (tick % guiItem.animationFrames.size())));
			this.inv.setItem(guiItem.slot, is);
		}
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
	 */
	@OptionalImplementation
	public void handleLast(InventoryClickEvent e) {}

	/**
	 * Called when the cooldown is not yet over but the {@link InventoryClickEvent} is fired.
	 *
	 * @param e the fired {@link InventoryClickEvent}
	 */
	@OptionalImplementation
	public void handleBlocked(InventoryClickEvent e) {}

	/**
	 * Called on an {@link InventoryDragEvent} in this {@link Inventory}.
	 *
	 * @param e the fired {@link InventoryDragEvent}
	 * @return if the event should be cancelled or not.
	 */
	@OptionalImplementation
	public boolean handleDrag(InventoryDragEvent e) { return isCancelledByDefault(); }

	/**
	 * Like {@link GUI#handleDrag(InventoryDragEvent)} but optional and one tick later.
	 *
	 * @param e the fired {@link InventoryClickEvent}
	 */
	@OptionalImplementation
	public void handleDragLast(InventoryDragEvent e) {}

	/**
	 * Like {@link GUI#handleDrag(InventoryDragEvent)} but optional and one tick later.
	 *
	 * @param e the fired {@link InventoryClickEvent}
	 */
	@OptionalImplementation
	public void handleClose(InventoryCloseEvent e) {}

	/**
	 * Returns if the event should be cancelled by default.
	 *
	 * @return the default cancel-value
	 */
	@OptionalImplementation
	public boolean isCancelledByDefault() { return true; }

	@Internal
	public final String getIdentifier() {
		return this.name + "-" + (this.p != null ? this.p.getUniqueId() : "universal");
	}
}