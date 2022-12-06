package tr.waterarchery.autosellchest.menus;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tr.waterarchery.autosellchest.handlers.ConfigManager;

import java.util.ArrayList;
import java.util.List;

public class MenuHandler {

    public static void FillMenu(String filler, Inventory inv) {
        Material mat = Material.getMaterial(filler);
        ItemStack fillerItem = new ItemStack(mat);
        ItemMeta meta = fillerItem.getItemMeta();
        meta.setDisplayName("§8");
        fillerItem.setItemMeta(meta);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, fillerItem);
        }
    }
    public static ItemStack CreateItem(String path, String menu) {
        String name = ConfigManager.getStringLang("Menus." + menu + ".Items." + path + ".Name");
        String block = ConfigManager.getStringLang("Menus." + menu + ".Items." + path + ".Block");
        List<String> tempLore = ConfigManager.getLang().getStringList("Menus." + menu + ".Items." + path + ".Lore");
        List<String> lore = new ArrayList<>();
        ItemStack item = new ItemStack(Material.getMaterial(block));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name.replace("&", "§"));
        for (String lorePart : tempLore) {
            lore.add(lorePart.replace("&", "§"));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

}
