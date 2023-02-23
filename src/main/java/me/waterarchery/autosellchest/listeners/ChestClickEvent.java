package me.waterarchery.autosellchest.listeners;

import me.waterarchery.autosellchest.AutoSellMain;
import me.waterarchery.autosellchest.SellChest;
import me.waterarchery.autosellchest.handlers.ConfigManager;
import me.waterarchery.autosellchest.menus.ChestMenu;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class ChestClickEvent implements Listener {

    @EventHandler
    public void MenuClickEvent(PlayerInteractEvent e){
        Block block = e.getClickedBlock();
        Player p = e.getPlayer();
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (block != null) {
                if (block.getType().equals(Material.CHEST)) {
                    for (SellChest chest : AutoSellMain.getSellChests()) {
                        if (chest.getLoc().equals(block.getLocation())) {
                            e.setCancelled(true);
                            if (chest.getOwner().equals(p.getUniqueId())) {
                                Inventory chestInv = ChestMenu.getChestMenu(chest);
                                p.openInventory(chestInv);
                            }
                            else {
                                ConfigManager.SendMessage(p, false, "CantOpenThisChest");
                            }
                            return;
                        }
                    }
                }
            }
        }
    }

}
