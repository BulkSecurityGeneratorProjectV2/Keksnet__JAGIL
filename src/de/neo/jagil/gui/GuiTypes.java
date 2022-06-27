package de.neo.jagil.gui;

import de.neo.jagil.JAGIL;
import de.neo.jagil.annotation.Internal;
import de.neo.jagil.util.ItemTool;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GuiTypes {

    @Internal
    public static class DataGui {

        public String name;
        public int size;
        public HashMap<Integer, GuiItem> items;
        public HashMap<String, Integer> itemIdTable;

        public DataGui() {
            this.name = "";
            this.size = 0;
            this.items = new HashMap<>();
        }

        /**
         * Returns the slot of the {@link ItemStack} with the given id.
         *
         * @param itemId the id of the {@link ItemStack}
         * @return the slot of the {@link ItemStack} with the given id
         */
        public int getSlot(String itemId) {
            return this.itemIdTable.getOrDefault(itemId, 999);
        }

        /**
         * Returns the {@link ItemStack} with the given id.
         *
         * @param itemId the id of the {@link ItemStack}
         * @return the {@link ItemStack} with the given id
         */
        public ItemStack getItem(String itemId) {
            int slot = getSlot(itemId);
            if(slot == 999) {
                return new ItemStack(Material.AIR);
            }
            return this.items.get(slot).toItem();
        }

        /**
         * Returns the itemId of the {@link ItemStack} with the given slot.
         *
         * @param slot the slot of the {@link ItemStack}
         * @return the itemId of the {@link ItemStack} with the given slot
         */
        public String getItemId(int slot) {
            String itemId = "";
            for(Map.Entry<String, Integer> entry : this.itemIdTable.entrySet()) {
                if(entry.getValue() == slot) {
                    itemId = entry.getKey();
                    break;
                }
            }
            return itemId;
        }

        @Override
        public String toString() {
            return "DataGui{name=" + this.name + ", " +
                    "size=" + this.size + ", " +
                    "items=" + this.items + "}";
        }
    }

    @Internal
    public static class GuiItem {

        public String id;
        public int slot;
        public Material material;
        public String name;
        public int amount;
        public List<String> lore;
        public HashSet<GuiEnchantment> enchantments;
        public int customModelData;
        public String texture;

        public GuiItem() {
            this.id = "";
            this.slot = 0;
            this.material = Material.AIR;
            this.name = "";
            this.lore = new ArrayList<>();
            this.enchantments = new HashSet<>();
            this.texture = "";
        }

        public GuiItem(GuiItem item) {
            this.id = item.id;
            this.slot = item.slot;
            this.material = item.material;
            this.name = item.name;
            this.amount = item.amount;
            this.lore = new ArrayList<>(item.lore);
            this.enchantments = new HashSet<>(item.enchantments);
            this.customModelData = item.customModelData;
            this.texture = "";
        }

        public ItemStack toItem() {
            ItemStack is;
            if (this.material == Material.PLAYER_HEAD || this.material == Material.PLAYER_WALL_HEAD) {
                if (this.texture.isEmpty()) {
                    is = new ItemStack(this.material, this.amount);
                } else {
                    is = ItemTool.createBase64Skull("", this.amount, this.texture);
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

            if(meta == null) {
                JAGIL.loaderPlugin.getLogger().warning("Could not create item meta for item " + this);
                meta = is.getItemMeta();
            }

            meta.setDisplayName(this.name);
            meta.setLore(this.lore);
            if(this.customModelData != 0) meta.setCustomModelData(this.customModelData);
            is.setItemMeta(meta);
            return is;
        }

        @Override
        public String toString() {
            return "GuiItem{id=" + this.id + ", " +
                    "slot=" + this.slot + ", " +
                    "material=" + this.material + ", " +
                    "name=" + this.name + ", " +
                    "amount=" + this.amount + ", " +
                    "lore=" + this.lore + ", " +
                    "enchantments=" + this.enchantments + "," +
                    "texture=" + this.texture + "}";
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
            return "GuiEnchantment{enchantment=" + this.enchantment + ", " +
                    "level=" + this.level + "}";
        }
    }

}
