package tr.waterarchery.autosellchest.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import tr.waterarchery.autosellchest.AutoSellMain;
import tr.waterarchery.autosellchest.SellChest;
import tr.waterarchery.autosellchest.handlers.ChestHandler;
import tr.waterarchery.autosellchest.handlers.ConfigManager;
import tr.waterarchery.autosellchest.handlers.SoundAndTitleHandler;

import java.util.ArrayList;

public class ChestPlaceEvent implements Listener {

    @EventHandler
    public void ChestPlace(BlockPlaceEvent e){
        if (e.getBlock() != null) {
            ItemStack item = e.getItemInHand();
            Player p = e.getPlayer();
            if (!e.isCancelled()) {
                if (item.isSimilar(ChestHandler.getChestItem())) {
                    Location loc = e.getBlockPlaced().getLocation();
                    SellChest chest = ChestHandler.getSellChestFromData(p.getUniqueId().toString());
                    if (chest != null) {
                        ConfigManager.SendMessage(p, false, "AlreadyHaveChest");
                        e.setCancelled(true);
                    }
                    else {
                        SellChest sellChest = new SellChest(new ArrayList<>(), loc, p.getUniqueId(), p.getName(), false);
                        sellChest.PlaceHologram();
                        AutoSellMain.getSellChests().add(sellChest);
                        ChestHandler.SaveChestToData(sellChest);
                        ConfigManager.SendMessage(p, false, "ChestCreated");
                        String title = ConfigManager.getStringLang("ChestPlacedTitle" + ".Title");
                        String subtitle = ConfigManager.getStringLang("ChestPlacedTitle" + ".SubTitle");
                        SoundAndTitleHandler.SendTitle(p, title, subtitle);
                        SoundAndTitleHandler.SendSound("CreatedChestSound", p);
                    }
                }
            }
        }
    }
}
