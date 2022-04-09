package de.neo.jagil.gui;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.neo.jagil.JAGIL;
import de.neo.jagil.annotation.*;
import de.neo.jagil.manager.GUIManager;
import de.neo.jagil.manager.GuiReaderManager;
import de.neo.jagil.reader.GuiReader;
import de.neo.jagil.util.ItemTool;
import de.neo.jagil.util.ParseUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

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
	protected HashMap<String, Integer> itemIds;
	protected DataGui guiData;

	private long cooldown;
	private long lastHandle;

	{
		cooldown = 50;
	}

	/**
	 * Creates a new instance of the {@link GUI} class.
	 * Use this constructor when you like to load a {@link GUI} from a xml/json-file.
	 * Use this constructor when you like to create a universal {@link GUI}.
	 *
	 * @param guiFile the path to the xml/json-file
	 */
	public GUI(Path guiFile) throws IOException, XMLStreamException {
		DataGui gui = loadGui(guiFile);
		this.guiData = gui;
		this.name = gui.name;
		this.size = gui.size;
		this.itemIds = new HashMap<>();
		for(GuiItem item : gui.items.values()) {
			this.itemIds.put(item.id, item.slot);
		}
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
	 * Use this constructor when you like to load a {@link GUI} from a xml/json-file.
	 * Use this constructor when you like to create a non-universal {@link GUI}.
	 *
	 * @param guiFile the path to the xml/json-file
	 */
	public GUI(Path guiFile, OfflinePlayer p) throws IOException, XMLStreamException {
		DataGui gui = loadGui(guiFile);
		this.guiData = gui;
		this.name = gui.name;
		this.size = gui.size;
		this.p = p;
		this.itemIds = new HashMap<>();
		for(GuiItem item : gui.items.values()) {
			this.itemIds.put(item.id, item.slot);
		}
		register();
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
			register();
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
			register();
		}
	}

	private void register() {
		Bukkit.getScheduler().runTaskLater(JAGIL.loaderPlugin, () -> {
			GUIManager.getInstance().register(this);
		}, 1L);
	}

	public GUI setName(String name) {
		this.name = name;
		register();
		return this;
	}

	public String getName() {
		return this.name;
	}
	
	public int getSize() {
		return this.size;
	}

	@Internal
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
		Bukkit.getScheduler().runTask(JAGIL.getPlugin(this.getClass().getName()), () -> getPlayer().closeInventory());
	}

	/**
	 * Returns the slot of the {@link ItemStack} with the given id.
	 *
	 * @param itemId the id of the {@link ItemStack}
	 * @return the slot of the {@link ItemStack} with the given id
	 */
	protected int getSlot(String itemId) {
		return this.itemIds.getOrDefault(itemId, 999);
	}

	/**
	 * Returns the {@link ItemStack} with the given id.
	 *
	 * @param itemId the id of the {@link ItemStack}
	 * @return the {@link ItemStack} with the given id
	 */
	protected ItemStack getItem(String itemId) {
		int slot = getSlot(itemId);
		if(slot == 999) {
			return new ItemStack(Material.AIR);
		}
        return this.guiData.items.get(slot).toItem();
    }

	/**
	 * Returns the itemId of the {@link ItemStack} with the given slot.
	 *
	 * @param slot the slot of the {@link ItemStack}
	 * @return the itemId of the {@link ItemStack} with the given slot
	 */
	protected String getItemId(int slot) {
        String itemId = "";
		for(Map.Entry<String, Integer> entry : this.itemIds.entrySet()) {
			if(entry.getValue() == slot) {
				itemId = entry.getKey();
			}
		}
		return itemId;
    }

	/**
	 * This method is called to create an Inventory.
	 * This is called by {@link GUI#show()} automatically.
	 */
	@Internal
	protected void update() {
		if(this.p != null) {
			this.updateInternal();
			return;
		}
		throw new RuntimeException("This method should not be called on universal GUIs");
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
			this.p.getPlayer().updateInventory();
		}
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
			register();
			if(JAGIL.getPlugin(this.getClass().getName()) != null) {
				getPlayer().getInventory();
				Bukkit.getScheduler().runTask(JAGIL.getPlugin(this.getClass().getName()), () -> this.p.getPlayer().openInventory(this.inv));
			}else {
				this.p.getPlayer().openInventory(this.inv);
			}
			this.p.getPlayer().updateInventory();
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
	public GUI show(OfflinePlayer p) {
		if (p != null) {
			Logger.getLogger("JAGIL").warning("Using show(OfflinePlayer) for non-universal GUIs is dangerous. Please try to avoid it.");
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
	public void fill() {}

	/**
	 * Loads a full {@link GUI} from a file
	 *
	 * @param file the file to load from
	 * @return the {@link DataGui}
	 */
	private DataGui loadGui(Path file) throws IOException {
		String[] fileName = file.toString().split("[.]");
		GuiReader reader = GuiReaderManager.getInstance().getReader(fileName[fileName.length - 1].toLowerCase());

		DataGui gui = reader.read(file);

		this.inv = Bukkit.createInventory(null, gui.size, gui.name);

		for(GUI.GuiItem guiItem : gui.items.values()) {
			if(guiItem.slot < 0) continue;
			ItemStack is = guiItem.toItem();
			this.inv.setItem(guiItem.slot, is);
		}

		return gui;
	}

	@Internal
	public boolean handleInternal(InventoryClickEvent e) {
		if(System.currentTimeMillis() - this.lastHandle <= this.cooldown) return isCancelledByDefault();
		this.lastHandle = System.currentTimeMillis();
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
	@DeprecatedSignature
	public GUI handleLast(InventoryClickEvent e) { return this; }

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
	 * @return instance for chaining
	 */
	@OptionalImplementation
	@DeprecatedSignature
	public GUI handleDragLast(InventoryDragEvent e) { return this; }

	/**
	 * Like {@link GUI#handleDrag(InventoryDragEvent)} but optional and one tick later.
	 *
	 * @param e the fired {@link InventoryClickEvent}
	 * @return instance for chaining
	 */
	@OptionalImplementation
	@DeprecatedSignature
	public GUI handleClose(InventoryCloseEvent e) { return this; }

	/**
	 * Returns if the event should be cancelled by default.
	 *
	 * @return the default cancel-value
	 */
	@OptionalImplementation
	public boolean isCancelledByDefault() { return true; }

	@Internal
	public static class DataGui {

		public DataGui() {
			this.name = "";
			this.size = 0;
			this.items = new HashMap<>();
		}

		public String name;
		public int size;
		public HashMap<Integer, GuiItem> items;

		@Override
		public String toString() {
			return "JsonGui{name=" + this.name + ", " +
					"size=" + this.size + ", " +
					"items=" + this.items + "}";
		}
	}

	@Internal
	public static class GuiItem {

		public GuiItem() {
			this.id = "";
			this.slot = 0;
			this.material = Material.AIR;
			this.name = "";
			this.lore = new ArrayList<>();
			this.enchantments = new HashSet<>();
		}

		public GuiItem(GuiItem item) {
			this.id = item.id;
			this.slot = item.slot;
			this.material = item.material;
			this.name = item.name;
			this.amount = item.amount;
			this.lore = new ArrayList<>(item.lore);
			this.enchantments = new HashSet<>(item.enchantments);
		}

		public String id;
		public int slot;
		public Material material;
		public String name;
		public int amount;
		public List<String> lore;
		public HashSet<GuiEnchantment> enchantments;
		public int customModelData;

		public ItemStack toItem() {
			ItemStack is;
			if (this instanceof XmlHead) {
				if (((XmlHead) this).texture.isEmpty()) {
					is = new ItemStack(this.material, this.amount);
				} else {
					is = ItemTool.createBase64Skull("", this.amount, ((XmlHead) this).texture);
				}
			} else {
				is = new ItemStack(this.material, this.amount);
			}
			if (this.enchantments != null) {
				for (GuiEnchantment enchantment : this.enchantments) {
					is.addUnsafeEnchantment(enchantment.enchantment, enchantment.level);
				}
			}
			ItemMeta meta = is.getItemMeta();
			meta.setDisplayName(this.name);
			meta.setLore(this.lore);
			if(this.customModelData != 0) meta.setCustomModelData(this.customModelData);
			is.setItemMeta(meta);
			return is;
		}

		@Override
		public String toString() {
			return "JsonItem{id=" + this.id + ", " +
					"slot=" + this.slot + ", " +
					"material=" + this.material + ", " +
					"name=" + this.name + ", " +
					"amount=" + this.amount + ", " +
					"lore=" + this.lore + ", " +
					"enchantments=" + this.enchantments + "}";
		}
	}

	@Internal
	public static class GuiEnchantment {

		public GuiEnchantment() {
			this.enchantment = null;
			this.level = 0;
		}

		public Enchantment enchantment;
		public int level;

		@Override
		public String toString() {
            return "JsonEnchantment{enchantment=" + this.enchantment + ", " +
                    "level=" + this.level + "}";
        }
	}

	@Internal
	public static class XmlHead extends GuiItem {

		public XmlHead() {
            this.texture = "";
        }

		public XmlHead(XmlHead head) {
			super(head);
			this.texture = head.texture;
		}

		public String texture;

		@Override
        public String toString() {
            return "JsonHead{id=" + this.id + ", " +
					"slot=" + this.slot + ", " +
					"material=" + this.material + ", " +
					"name=" + this.name + ", " +
					"amount=" + this.amount + ", " +
					"lore=" + this.lore + ", " +
					"enchantments=" + this.enchantments + ", " +
					"texture=" + this.texture + "}";
        }

	}
}