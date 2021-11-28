package de.neo.jagil.gui;

import de.neo.jagil.JAGIL;
import de.neo.jagil.annotation.Internal;
import de.neo.jagil.annotation.NoCompatibilityMode;
import de.neo.jagil.annotation.OptionalImplementation;
import de.neo.jagil.annotation.UnstableFeature;
import de.neo.jagil.manager.GUIManager;
import de.neo.jagil.util.ItemTool;
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
	
	private final String name;
	private int size;
	private InventoryType type;
	private OfflinePlayer p;
	private Inventory inv;
	private HashMap<String, Integer> itemIds;
	protected XmlGui guiData;

	private long cooldown;
	private long lastHandle;

	{
		cooldown = 10_000_000;
	}

	/**
	 * Creates a new instance of the {@link GUI} class.
	 * Use this constructor when you like to load a {@link GUI} from a xml-file.
	 * Use this constructor when you like to create a universal {@link GUI}.
	 *
	 * @param xmlFile the path to the xml-file
	 */
	public GUI(Path xmlFile) throws IOException, XMLStreamException {
		XmlGui gui = loadFromXml(xmlFile);
		this.guiData = gui;
		this.name = gui.name;
		this.size = gui.size;
		this.itemIds = new HashMap<>();
		for(XmlItem item : gui.items.values()) {
			this.itemIds.put(item.id, item.slot);
			System.out.println("REGISTERED: " + item);
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
	 * Use this constructor when you like to load a {@link GUI} from a xml-file.
	 * Use this constructor when you like to create a non-universal {@link GUI}.
	 *
	 * @param xmlFile the path to the xml-file
	 */
	public GUI(Path xmlFile, OfflinePlayer p) throws IOException, XMLStreamException {
		XmlGui gui = loadFromXml(xmlFile);
		this.guiData = gui;
		this.name = gui.name;
		this.size = gui.size;
		this.p = p;
		this.itemIds = new HashMap<>();
		for(XmlItem item : gui.items.values()) {
			this.itemIds.put(item.id, item.slot);
			System.out.println("REGISTERED: " + item);
		}
		GUIManager.getInstance().register(this);
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
			if(JAGIL.getPlugin(this.getClass().getName()) != null) {
				getPlayer().getInventory();
				Bukkit.getScheduler().runTask(JAGIL.getPlugin(this.getClass().getName()), () -> this.p.getPlayer().openInventory(this.inv));
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
		if (p != null) {
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
	 */
	public void fill() {}

	/**
	 * Loads a full {@link GUI} from a xml-file.
	 *
	 * @param file the {@link Path} to the xml-file.
	 * @throws IOException an {@link IOException} occured.
	 * @throws XMLStreamException a {@link XMLStreamException} occured.
	 */
	public XmlGui loadFromXml(Path file) throws IOException, XMLStreamException {
		XmlGui gui = new XmlGui();

		XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(Files.newInputStream(file));

		String tag = "";
		String next = "";
		XmlItem item = null;
		XmlEnchantment enchantment = null;

		parser: {
			while(reader.hasNext()) {
				XMLEvent event = reader.nextEvent();

				switch(event.getEventType()) {
					case XMLStreamConstants.START_ELEMENT:
						StartElement element = event.asStartElement();
						String elem = element.getName().getLocalPart();
						if(elem.equalsIgnoreCase("gui") || elem.equalsIgnoreCase("item")
								|| elem.equalsIgnoreCase("lore")
								|| elem.equalsIgnoreCase("enchantment")) {
							tag = elem;
							if(elem.equalsIgnoreCase("item")) {
								item = new XmlHead();
							}else if(elem.equalsIgnoreCase("enchantment")) {
								enchantment = new XmlEnchantment();
							}
						}else {
							next = elem;
						}
						break;

					case XMLStreamConstants.CHARACTERS:
						Characters chars = event.asCharacters();
						switch (next) {
							case "id":
								System.out.println("id: " + chars.getData().trim());
								item.id = chars.getData().trim();
								break;

							case "slot":
								System.out.println("slot: " + chars.getData());
								System.out.println("slot_n: " + Integer.parseInt(normalizeString(chars.getData())));
								item.slot = Integer.parseInt(normalizeString(chars.getData()));
								break;

							case "material":
								item.material = Material.getMaterial(chars.getData().toUpperCase());
								break;

							case "name":
								if(tag.equalsIgnoreCase("item")) {
									item.name = chars.getData();
								}else if(tag.equalsIgnoreCase("gui")) {
									gui.name = chars.getData();
								}
								break;

							case "amount":
								item.amount = Integer.parseInt(normalizeString(chars.getData()));
								break;

							case "line":
								item.lore.add(chars.getData());
								break;

							case "size":
								gui.size = Integer.parseInt(normalizeString(chars.getData()));
								break;

							case "enchantmentName":
								enchantment.enchantment = Arrays.stream(Enchantment.values())
										.filter((it) -> chars.getData().equalsIgnoreCase(it.toString()))
										.findFirst().get();
								break;

							case "enchantmentLevel":
								enchantment.level = Integer.parseInt(normalizeString(chars.getData()));
								break;

							case "base64":
								if(item instanceof XmlHead) {
									((XmlHead)item).texture = chars.getData();
								}
								break;
						}
						next = "done";
						break;

					case XMLStreamConstants.END_ELEMENT:
						EndElement endElement = event.asEndElement();
						if(endElement.getName().getLocalPart().equalsIgnoreCase("item")) {
							gui.items.put(item.slot, item);
						}else if(endElement.getName().getLocalPart().equalsIgnoreCase("enchantment")) {
							item.enchantments.add(enchantment);
						}
						break;

					case XMLStreamConstants.END_DOCUMENT:
						reader.close();
						break parser;
				}
			}
		}

		this.inv = Bukkit.createInventory(null, gui.size, gui.name);

		for(XmlItem xmlItem : gui.items.values()) {
			if(xmlItem.slot < 0) continue;
			ItemStack is = xmlItem.toItem();
			this.inv.setItem(xmlItem.slot, is);
		}

		return gui;
	}

	@Internal
	private String normalizeString(String unfiltered) {
		StringBuilder r = new StringBuilder();
		for(char c : unfiltered.toCharArray()) {
			if(Character.isDigit(c) || c == '-') {
				r.append(c);
			}
		}
		return r.toString().trim();
	}

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
	 * Returns if the event should be cancelled by default.
	 *
	 * @return the default cancel-value
	 */
	@OptionalImplementation
	public boolean isCancelledByDefault() { return true; }

	@Internal
	public static class XmlGui {

		public XmlGui() {
			this.name = "";
			this.size = 0;
			this.items = new HashMap<>();
		}

		public String name;
		public int size;
		public HashMap<Integer, XmlItem> items;

		@Override
		public String toString() {
			return "JsonGui{name=" + this.name + ", " +
					"size=" + this.size + ", " +
					"items=" + this.items + "}";
		}
	}

	@Internal
	public static class XmlItem {

		public XmlItem() {
			this.id = "";
			this.slot = 0;
			this.material = Material.AIR;
			this.name = "";
			this.lore = new ArrayList<>();
		}

		public String id;
		public int slot;
		public Material material;
		public String name;
		public int amount;
		public List<String> lore;
		public HashSet<XmlEnchantment> enchantments;

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
				for (XmlEnchantment enchantment : this.enchantments) {
					is.addUnsafeEnchantment(enchantment.enchantment, enchantment.level);
				}
			}
			ItemMeta meta = is.getItemMeta();
			meta.setDisplayName(this.name);
			meta.setLore(this.lore);
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
	public static class XmlEnchantment {

		public XmlEnchantment() {
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
	public static class XmlHead extends XmlItem {

		public XmlHead() {
            this.texture = "";
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