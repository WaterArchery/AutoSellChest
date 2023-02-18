package me.waterarchery.autosellchest.menus;

import me.waterarchery.autosellchest.SellChest;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import me.waterarchery.autosellchest.handlers.ConfigManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ChestMenu {

    private static final DecimalFormat df = new DecimalFormat("0.00");
    public static Inventory getChestMenu(SellChest chest){
        int size = ConfigManager.getLang().getInt("Menus.ChestMenu.Size");
        Inventory inv = Bukkit.createInventory(null, size, ConfigManager.getStringLang("Menus.ChestMenu.Title"));
        String filler = ConfigManager.getLang().getString("Menus.ChestMenu.Filler");
        if (!(filler.equalsIgnoreCase("AIR") | filler.equalsIgnoreCase("NONE"))) {
            MenuHandler.FillMenu(filler, inv);
        }
        for (String menuItem : ConfigManager.getLang().getConfigurationSection("Menus.ChestMenu.Items").getKeys(false)) {
            if (chest.isWorking()) {
                if(menuItem.equalsIgnoreCase("Start")) {
                    continue;
                }
            }
            else {
                if(menuItem.equalsIgnoreCase("Stop")) {
                    continue;
                }
            }
            int slot = ConfigManager.getLang().getInt("Menus.ChestMenu.Items." + menuItem + ".Slot");
            ItemStack item;
            item = MenuHandler.CreateItem(menuItem, "ChestMenu");
            List<String> tempLore = item.getItemMeta().getLore();
            List<String> lore = new ArrayList<>();
            ItemMeta meta = item.getItemMeta();
            for (String lorePart : tempLore) {
                lore.add(lorePart.replace("%money%", df.format(chest.getMoney()) + ""));
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(slot, item);
        }
        return inv;
    }

}
