package tr.waterarchery.autosellchest.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import tr.waterarchery.autosellchest.SellChest;
import tr.waterarchery.autosellchest.handlers.ChestHandler;
import tr.waterarchery.autosellchest.handlers.ConfigManager;
import tr.waterarchery.autosellchest.handlers.PriceHandler;
import tr.waterarchery.autosellchest.handlers.SoundAndTitleHandler;

import java.util.ArrayList;

public class MenuCloseEvent implements Listener {

    @EventHandler
    public void SaveItemsToData(InventoryCloseEvent e){
        Player p = (Player) e.getPlayer();
        if (e.getInventory() != null) {
            String manageItemsMenu = ConfigManager.getStringLang("Menus.ManageItems.Title");
            InventoryView invView = e.getView();
            Inventory inv = e.getInventory();
            if (menuTitle(invView).equalsIgnoreCase(manageItemsMenu)) {
                int manageItemsSlot = ConfigManager.getLang().getInt("Menus.ManageItems.Items.ManageItems.Slot");
                SellChest chest = ChestHandler.getSellChestFromList(p.getUniqueId().toString());
                ArrayList<ItemStack> contents = new ArrayList<>();
                for (int i = 0; i < inv.getSize(); i++) {
                    if (i != manageItemsSlot) {
                        if (inv.getItem(i) != null) {
                            if (!inv.getItem(i).getType().equals(Material.AIR)) {
                                ItemStack item = inv.getItem(i);
                                double priceItem = PriceHandler.getPrice(item);
                                if (priceItem > 0) {
                                    contents.add(item);
                                }
                                else {
                                    inv.removeItem(item);
                                    p.getInventory().addItem(item);
                                }
                            }
                        }
                    }
                }
                if (!chest.getContents().equals(contents)) {
                    chest.setContents(contents);
                    ConfigManager.SendMessage(p, false, "ItemsSaved");
                    String title = ConfigManager.getStringLang("ItemsAddedOrRemovedTitle" + ".Title");
                    String subtitle = ConfigManager.getStringLang("ItemsAddedOrRemovedTitle" + ".SubTitle");
                    SoundAndTitleHandler.SendTitle(p, title, subtitle);
                    SoundAndTitleHandler.SendSound("ClosedManageItemsMenuSound", p);
                }
            }
        }
    }

    public String menuTitle (InventoryView invView) {
        return invView.getTitle();
    }

}
