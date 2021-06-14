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

public class ItemTool {
	
	public static ItemStack createItem(Material m) {
		return createItem(1, m);
	}
	
	public static ItemStack createItem(Integer amount, Material m) {
		return createItem("", amount, m);
	}
	
	public static ItemStack createItem(String name, Material m) {
		return createItem(name, 1, m);
	}
	
	public static ItemStack createItem(String name, Integer amount, Material m) {
		ItemStack is = new ItemStack(m, amount);
		if(!name.equalsIgnoreCase("")) {
			ItemMeta meta = is.getItemMeta();
			meta.setDisplayName(name);
			is.setItemMeta(meta);
		}
		return is;
	}
	
	public static ItemStack createSkull(OfflinePlayer skullOwner) {
		return createSkull(1, skullOwner);
	}
	
	public static ItemStack createSkull(Integer amount, OfflinePlayer skullOwner) {
		return createSkull("", amount, skullOwner);
	}
	
	public static ItemStack createSkull(String name, OfflinePlayer skullOwner) {
		return createSkull(name, 1, skullOwner);
	}
	
	public static ItemStack createSkull(String name, Integer amount, OfflinePlayer skullOwner) {
		ItemStack is = createItem(name, amount, Material.PLAYER_HEAD);
		if(!name.equalsIgnoreCase("")) {
			SkullMeta meta = (SkullMeta) is.getItemMeta();
			meta.setOwningPlayer(skullOwner);
			is.setItemMeta(meta);
		}
		return is;
	}
	
	public static ItemStack createBase64Skull(String base64) {
		return createBase64Skull("", 1, base64);
	}
	
	public static ItemStack createBase64Skull(String name, String base64) {
		return createBase64Skull(name, 1, base64);
	}
	
	public static ItemStack createBase64Skull(String name, Integer amount, String base64) {
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
	
	public static ItemStack addEnchantment(ItemStack is, Enchantment ench, Integer lvl) {
		is.addUnsafeEnchantment(ench, lvl);
		return is;
	}
	
}
