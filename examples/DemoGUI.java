package de.neo.jagil.examples;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import de.neo.jagil.util.ItemTool;
import de.neo.jagil.gui.GUI;

public class DemoGUI extends GUI {
	
	/* Basic constructor. */
	public DemoGUI(OfflinePlayer p) {
		super("§aGUI", 9, p); //Creates an GUI for the Player p. The GUI has the name §aGUI (§a is a colorcode) and it has 9 slots. Always remember to give GUIs a colorcode to prevent bugusing.
	}
	
	/* Here we can fill our GUI with staff. I recommend using the shipped ItemTool Class to create Items without pain :D */
	@Override
	public GUI fill() {
		Inventory inv = this.getInventory(); //We can get the Inventory to fill it using this.getInventory()
		inv.setItem(2, ItemTool.createItem("§aHello World", Material.APPLE)); //Basic method inv.setItem(int, ItemStack); but we are using ItemTool for creating items easily.
		String frozen_bee = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTdlMTIzYzA4NTg2N2JhYmU0ZmQ1ODIxMjg4OTNlM2JkMmYyOGEwOThhZGRiMWExZmM1ZDI3NTViZGFiYzgifX19";
		inv.setItem(6, ItemTool.createBase64Skull(frozen_bee)); //We can even create Base64Skulls easily with ItemTool. And now we have a sweet little frozen bee in our GUI.
		return this; //Remember to return this. It is needed for chaining.
	}

	/* And here we can handle the click if someone clicked in our Inventory. It's easy, isn't is? */
	@Override
	public Boolean handle(InventoryClickEvent e) {
		if(e.getWhoClicked().getType().equals(EntityType.PLAYER)) { //Check if a Player clicked in this Inventory. (To be honest: I do not know who else could click in a Inventory)
			Player p = (Player) e.getWhoClicked(); //We cast the clicker to a player.
			Integer slot = e.getSlot(); //and we save the slot in that the player clicked.
			switch (slot) { //The easiest way to do is using a switch case. We only handle clicks on slots two and six because we put only items in this slots. But you do not need to handle them!
			case 2:
				p.sendMessage("You clicked on the apple"); //We send the player a message, if he clicked on the Apple.
				break;
				
			case 6:
				p.sendMessage("You clicked on the bee but you are lucky it is frozen :D"); //We send him a different message, if he clicked on the bee
				break;

			default:
				break;
			}
		}
		return true; //Return if the event should cancelled or not. If you call e.setCancelled(Boolean); it will not work!
	}
	
	/* And now we have a really basic GUI. But for the most cases of use it will be enough. If not you should look in the GUI class or read more tutorials that I will add soon. */
}
