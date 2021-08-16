package de.neo.jagil.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

/**
 * This UtilityClass provides different methods to create and modify an {@link ItemStack}.
 *
 * @author Neo8
 * @version 2.0
 */
public class ItemTool {

	/**
	 * Creates a new {@link ItemStack} with the given {@link Material}.
	 * Invokes {@link ItemTool#createItem(int, Material)}.
	 *
	 * @param m material of the new {@link ItemStack}
	 * @return the new {@link ItemStack}
	 */
	public static ItemStack createItem(Material m) {
		return createItem(1, m);
	}

	/**
	 * Creates a new {@link ItemStack} with the given amount and {@link Material}.
	 * Invokes {@link ItemTool#createItem(String, int, Material)}.
	 *
	 * @param amount amount of items in the {@link ItemStack}
	 * @param m material of the new {@link ItemStack}
	 * @return the new {@link ItemStack}
	 */
	public static ItemStack createItem(int amount, Material m) {
		return createItem("", amount, m);
	}

	/**
	 * Creates a new {@link ItemStack} with the given name and {@link Material}.
	 * Invokes {@link ItemTool#createItem(String, int, Material)}.
	 *
	 * @param name name of the new {@link ItemStack}
	 * @param m material of the new {@link ItemStack}
	 * @return the new {@link ItemStack}
	 */
	public static ItemStack createItem(String name, Material m) {
		return createItem(name, 1, m);
	}

	/**
	 * Creates a new {@link ItemStack} with the given name and {@link Material}.
	 *
	 * @param name name of the new {@link ItemStack}
	 * @param amount amount of items in the {@link ItemStack}
	 * @param m material of the new {@link ItemStack}
	 * @return the new {@link ItemStack}
	 */
	public static ItemStack createItem(String name, int amount, Material m) {
		ItemStack is = new ItemStack(m, amount);
		if(!name.equalsIgnoreCase("")) {
			ItemMeta meta = is.getItemMeta();
			meta.setDisplayName(name);
			is.setItemMeta(meta);
		}
		return is;
	}

	/**
	 * Creates a new {@link ItemStack} of {@link Material#PLAYER_HEAD}.
	 * Invokes {@link ItemTool#createSkull(int, OfflinePlayer)}.
	 *
	 * @apiNote use {@link ItemTool#createBase64Skull(String, int, String)} for players that are offline
	 * @param skullOwner the owner of the skull
	 * @return the new {@link ItemStack}
	 */
	public static ItemStack createSkull(OfflinePlayer skullOwner) {
		return createSkull(1, skullOwner);
	}

	/**
	 * Creates a new {@link ItemStack} of {@link Material#PLAYER_HEAD}.
	 * Invokes {@link ItemTool#createSkull(String, int, OfflinePlayer)}.
	 *
	 * @apiNote use {@link ItemTool#createBase64Skull(String, int, String)} for players that are offline
	 * @param amount amount of items in the {@link ItemStack}
	 * @param skullOwner the owner of the skull
	 * @return the new {@link ItemStack}
	 */
	public static ItemStack createSkull(int amount, OfflinePlayer skullOwner) {
		return createSkull("", amount, skullOwner);
	}

	/**
	 * Creates a new {@link ItemStack} of {@link Material#PLAYER_HEAD}.
	 * Invokes {@link ItemTool#createSkull(String, int, OfflinePlayer)}.
	 *
	 * @param name name of the new {@link ItemStack}
	 * @param skullOwner the owner of the skull
	 * @return the new {@link ItemStack}
	 */
	public static ItemStack createSkull(String name, OfflinePlayer skullOwner) {
		return createSkull(name, 1, skullOwner);
	}

	/**
	 * Creates a new {@link ItemStack} of {@link Material#PLAYER_HEAD}.
	 *
	 * @param name name of the new {@link ItemStack}
	 * @param amount amount of items in the new {@link ItemStack}
	 * @param skullOwner the owner of the skull
	 * @return the new {@link ItemStack}
	 */
	public static ItemStack createSkull(String name, int amount, OfflinePlayer skullOwner) {
		ItemStack is = createItem(name, amount, Material.PLAYER_HEAD);
		if(!name.equalsIgnoreCase("")) {
			SkullMeta meta = (SkullMeta) is.getItemMeta();
			meta.setOwningPlayer(skullOwner);
			is.setItemMeta(meta);
		}
		return is;
	}

	/**
	 * Creates a new {@link ItemStack} of {@link Material#PLAYER_HEAD}.
	 * Invokes {@link ItemTool#createBase64Skull(String, int, String)}.
	 *
	 * @param base64 base64 of the skull
	 * @return the new {@link ItemStack}
	 */
	public static ItemStack createBase64Skull(String base64) {
		return createBase64Skull("", 1, base64);
	}

	/**
	 * Creates a new {@link ItemStack} of {@link Material#PLAYER_HEAD}.
	 * Invokes {@link ItemTool#createBase64Skull(String, int, String)}.
	 *
	 * @param name name of the new {@link ItemStack}
	 * @param base64 base64 of the skull
	 * @return the new {@link ItemStack}
	 */
	public static ItemStack createBase64Skull(String name, String base64) {
		return createBase64Skull(name, 1, base64);
	}

	/**
	 * Creates a new {@link ItemStack} of {@link Material#PLAYER_HEAD}.
	 *
	 * @param name name of the new {@link ItemStack}
	 * @param amount amount of items in the new {@link ItemStack}
	 * @param base64 base64 of the skull
	 * @return the new {@link ItemStack}
	 */
	public static ItemStack createBase64Skull(String name, int amount, String base64) {
		ItemStack is = new ItemStack(Material.PLAYER_HEAD, amount);
		SkullMeta meta = (SkullMeta) is.getItemMeta();
		if(!name.equalsIgnoreCase("")) {
			meta.setDisplayName(name);
		}
		GameProfile gp = new GameProfile(UUID.randomUUID(), "");
		gp.getProperties().put("textures", new Property("textures", base64));
		try {
			Field pr = meta.getClass().getDeclaredField("profile");
			pr.setAccessible(true);
			pr.set(meta, gp);
			pr.setAccessible(false);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		is.setItemMeta(meta);
		return is;
	}

	/**
	 * Creates a new {@link ItemStack} of {@link Material#PLAYER_HEAD}.
	 *
	 * @param is existing {@link ItemStack}
	 * @param lore the lines of the lore
	 * @return the new {@link ItemStack}
	 */
	public static ItemStack setLore(ItemStack is, String... lore) {
		ArrayList<String> lore_l = new ArrayList<>();
		for(String l : lore) {
			lore_l.add(l);
		}
		ItemMeta meta = is.getItemMeta();
		meta.setLore(lore_l);
		is.setItemMeta(meta);
		return is;
	}

	/**
	 * Creates a new {@link ItemStack} of {@link Material#PLAYER_HEAD}.
	 *
	 * @param is existing {@link ItemStack}
	 * @param ench enchament to add
	 * @param lvl level of enchantment
	 * @return the new {@link ItemStack}
	 */
	public static ItemStack addEnchantment(ItemStack is, Enchantment ench, int lvl) {
		is.addUnsafeEnchantment(ench, lvl);
		return is;
	}
	
}
