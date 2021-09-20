package de.neo.jagil.gui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import de.neo.jagil.JAGIL;
import de.neo.jagil.annotation.Internal;
import de.neo.jagil.annotation.NoCompatibilityMode;
import de.neo.jagil.annotation.OptionalImplementation;
import de.neo.jagil.annotation.UnstableFeature;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import de.neo.jagil.manager.GUIManager;
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
	 * Use this constructor when you like to load a {@link GUI} from a xml-file.
	 * Use this constructor when you like to create a universal {@link GUI}.
	 *
	 * @param xmlFile the path to the xml-file
	 */
	public GUI(Path xmlFile) throws IOException, XMLStreamException {
		XmlGui gui = loadFromXml(xmlFile);
		this.name = gui.name;
		this.size = gui.size;
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
		this.name = gui.name;
		this.size = gui.size;
		this.p = p;
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
			this.fillInternal();
		}else {
			this.fillInternal();
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

	public GUI fillInternal() {
		fillInternal();
		return this;
	}

	/**
	 * Fills this {@link GUI}
	 *
	 * @return the returntype is only for compatibility reasons.
	 */
	public abstract GUI fill();

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
		while(reader.hasNext()) {
			XMLEvent event = reader.nextEvent();

			switch(event.getEventType()) {
				case XMLStreamConstants.START_ELEMENT:
					StartElement element = event.asStartElement();
					String elem = element.getName().getLocalPart();
					if(elem.equalsIgnoreCase("gui") || elem.equalsIgnoreCase("item")
							|| elem.equalsIgnoreCase("lore")) {
						tag = elem;
						if(elem.equalsIgnoreCase("item")) {
							item = new XmlItem();
						}
					}else {
						next = elem;
					}
					break;

				case XMLStreamConstants.CHARACTERS:
					Characters chars = event.asCharacters();
					switch (next) {
						case "id":
							item.id = chars.getData();
							break;

						case "slot":
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
					}
					next = "done";
					break;

				case XMLStreamConstants.END_ELEMENT:
					EndElement endElement = event.asEndElement();
					if(endElement.getName().getLocalPart().equalsIgnoreCase("item")) {
						gui.items.add(item);
					}
					break;
			}
		}

		reader.close();

		for(XmlItem xmlItem : gui.items) {
			ItemStack is = new ItemStack(item.material, item.amount);
			ItemMeta meta = is.getItemMeta();
			meta.setDisplayName(item.name);
			meta.setLore(item.lore);
			is.setItemMeta(meta);
			this.inv.setItem(item.slot, is);
		}

		return gui;
	}

	@Internal
	private String normalizeString(String unfiltered) {
		StringBuilder r = new StringBuilder();
		for(char c : unfiltered.toCharArray()) {
			if(Character.isDigit(c)) {
				r.append(c);
			}
		}
		return r.toString();
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
	 * Returns if the event should cancelled by default.
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
			this.items = new HashSet<>();
		}

		public String name;
		public int size;
		public HashSet<XmlItem> items;

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
			this.id = "ID HERE";
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

		@Override
		public String toString() {
			return "JsonItem{id=" + this.id + ", " +
					"slot=" + this.slot + ", " +
					"material=" + this.material + ", " +
					"name=" + this.name + ", " +
					"amount=" + this.amount + ", " +
					"lore=" + this.lore + "}";
		}
	}
}