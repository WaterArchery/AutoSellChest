package tr.waterarchery.autosellchest.events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import tr.waterarchery.autosellchest.AutoSellMain;
import tr.waterarchery.autosellchest.SellChest;
import tr.waterarchery.autosellchest.handlers.ConfigManager;

public class ChestBreakEvent implements Listener {

    @EventHandler
    public void ChestBreak(BlockBreakEvent e){
        Player p = e.getPlayer();
        Block block = e.getBlock();
        if (block.getType().equals(Material.CHEST)) {
            for (SellChest chest : AutoSellMain.getSellChests()) {
                if (chest.getLoc().equals(block.getLocation())) {
                    e.setCancelled(true);
                    if (p.getUniqueId().toString().equalsIgnoreCase(chest.getOwner().toString())) {
                        ConfigManager.SendMessage(p, false, "CantRemoveWithBreakOwner");
                    }
                    else {
                        String mes = ConfigManager.getStringLang("CantRemoveWithBreakOther").replace("%player%", chest.getOwnerName());
                        ConfigManager.SendMessage(p, mes);
                    }
                    return;
                }
            }
        }
    }
}
