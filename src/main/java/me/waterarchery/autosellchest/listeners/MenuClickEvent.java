package me.waterarchery.autosellchest.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import me.waterarchery.autosellchest.SellChest;
import me.waterarchery.autosellchest.handlers.ChestHandler;
import me.waterarchery.autosellchest.handlers.ConfigManager;
import me.waterarchery.autosellchest.menus.MenuHandler;

public class MenuClickEvent implements Listener {

    @EventHandler
    public void MenuClick(InventoryClickEvent e) {
        if (e.getCurrentItem() != null) {
            if (e.getInventory() != null) {
                InventoryView invView = e.getView();
                String chestMenu = ConfigManager.getStringLang("Menus.ChestMenu.Title");
                String manageItemsMenu = ConfigManager.getStringLang("Menus.ManageItems.Title");
                Player p = (Player) e.getWhoClicked();
                ItemStack clickedItem = e.getCurrentItem();
                if (menuTitle(invView).equalsIgnoreCase(chestMenu)) {
                    e.setCancelled(true);
                    if (!clickedItem.hasItemMeta()) {
                        return;
                    }
                    String clickedName = clickedItem.getItemMeta().getDisplayName();
                    String startItem = ConfigManager.getStringLang("Menus.ChestMenu.Items.Start.Name");
                    String stopItem = ConfigManager.getStringLang("Menus.ChestMenu.Items.Stop.Name");
                    String manageItems = ConfigManager.getStringLang("Menus.ChestMenu.Items.ManageItems.Name");
                    String moneyItem = ConfigManager.getStringLang("Menus.ChestMenu.Items.Money.Name");

                    SellChest chest = ChestHandler.getSellChestFromList(p.getUniqueId().toString());
                    //Eğer başka biri chesti kapatırsa
                    //null verip eşya bugu olmasın diye
                    if (chest == null) {
                        p.closeInventory();
                        return;
                    }
                    if (clickedName.equalsIgnoreCase(startItem)) {
                        chest.setStatus(true);
                        ItemStack stop = MenuHandler.CreateItem("Stop", "ChestMenu");
                        e.getInventory().setItem(e.getSlot(), stop);
                        ConfigManager.SendMessage(p, false, "StartedSelling");
                    }
                    else if (clickedName.equalsIgnoreCase(stopItem)) {
                        chest.setStatus(false);
                        ItemStack start = MenuHandler.CreateItem("Start", "ChestMenu");
                        e.getInventory().setItem(e.getSlot(), start);
                        ConfigManager.SendMessage(p, false, "StoppedSelling");
                    }
                    else if (clickedName.equalsIgnoreCase(manageItems)) {
                        p.openInventory(chest.getContentsInventory());
                    }
                    else if (clickedName.equalsIgnoreCase(moneyItem)) {
                        Bukkit.getServer().dispatchCommand(p, "asc collect");
                        p.closeInventory();
                    }
                }
                if (menuTitle(invView).equalsIgnoreCase(manageItemsMenu)) {
                    if (!clickedItem.hasItemMeta()) {
                        return;
                    }
                    String clickedName = clickedItem.getItemMeta().getDisplayName();
                    String manageItems = ConfigManager.getStringLang("Menus.ManageItems.Items.ManageItems.Name");
                    if (clickedName.equalsIgnoreCase(manageItems)) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    public String menuTitle (InventoryView invView) {
        return invView.getTitle();
    }

}
