package de.neo.jagil.gui.prebuild;

import de.neo.jagil.gui.GUI;
import de.neo.jagil.util.ItemTool;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A GUI for displaying a list of players.
 * Implementation by Nononitas from the "KeinSurvival". (https://github.com/Nononitas)
 * Edited by Neo8
 *
 * @author Nononitas
 * @version 3.3.5
 */
public class PlayerListGUI extends GUI {

    private int page = 0;

    protected String backHead = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==";
    protected String backString = "�cBack";
    protected String playerNameFormat = "�9%player%";
    protected String nextPageHead = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWYxMzNlOTE5MTlkYjBhY2VmZGMyNzJkNjdmZDg3YjRiZTg4ZGM0NGE5NTg5NTg4MjQ0NzRlMjFlMDZkNTNlNiJ9fX0=";
    protected String prevPageHead = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTNmYzUyMjY0ZDhhZDllNjU0ZjQxNWJlZjAxYTIzOTQ3ZWRiY2NjY2Y2NDkzNzMyODliZWE0ZDE0OTU0MWY3MCJ9fX0=";
    protected String nextPageString = "�aNext Page";
    protected String prevPageString = "�aPrevious Page";

    public PlayerListGUI(Player player, String title) {
        super(title, 54, player);
    }

    @Override
    public void fill() {
        List<OfflinePlayer> playerList = getPlayerList();
        final int constant = page * 28;
        for (int i = 0, j = 0; i < 54; i++) {
            ItemStack item = ItemTool.createItem(Material.BLACK_STAINED_GLASS_PANE);
            if (i != 17 && i != 18 && i != 26 && i != 27 && i != 35 && i != 36) {
                if (i == 8) {
                    item = ItemTool.createBase64Skull(backString, backHead);
                } else if (i > 9 && i < 44) {
                    if (playerList.size() > j + constant) {
                        OfflinePlayer offlinePlayer = playerList.get(j + constant);
                        item = ItemTool.createSkull(playerNameFormat.replace("%player%", offlinePlayer.getName()), offlinePlayer);
                        j++;
                    } else {
                        item = ItemTool.createItem(Material.GRAY_STAINED_GLASS_PANE);
                    }

                } else if (i == 47 && page > 0) {
                    item = ItemTool.createBase64Skull(nextPageString, nextPageHead);
                } else if (i == 51) {
                    if (constant + 28 < playerList.size()) {
                        item = ItemTool.createBase64Skull(prevPageString, prevPageHead);
                    }
                }
            }

            getInventory().setItem(i, item);
        }
    }

    @Override
    public boolean handle(InventoryClickEvent event) {
        int slot = event.getSlot();
        ItemStack clickedItem = getInventory().getItem(slot);
        if (slot > 9 && slot < 18) {
            if (clickedItem != null) {
                if (clickedItem.getType() == Material.PLAYER_HEAD) {
                    SkullMeta skullMeta = (SkullMeta) clickedItem.getItemMeta();
                    onPlayerClick(skullMeta.getOwningPlayer());
                }
            }
            return true;
        }
        if (slot == 8) {
            onBack();
        }
        if (slot == 47 && page > 0) {
            if (clickedItem.getType() == Material.PLAYER_HEAD) {
                page--;
                fill();
            }

        } else if (slot == 51) {
            if (clickedItem.getType() == Material.PLAYER_HEAD) {
                page++;
                fill();
            }
        }


        return true;
    }

    public List<OfflinePlayer> getPlayerList() {
        return new LinkedList<>(Bukkit.getOnlinePlayers().stream().collect(Collectors.toList()));
    }


    public void onPlayerClick(OfflinePlayer offlinePlayer) {}

    public void onBack() {}


}