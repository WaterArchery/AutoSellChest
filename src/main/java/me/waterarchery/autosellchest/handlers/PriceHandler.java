package me.waterarchery.autosellchest.handlers;

import me.waterarchery.autosellchest.hooks.CustomPriceHook;
import me.waterarchery.autosellchest.hooks.EssentialsXHook;
import me.waterarchery.autosellchest.hooks.ShopGUIHook;
import org.bukkit.inventory.ItemStack;

public class PriceHandler {

    public static double getPrice(ItemStack item){
        String priceSource = ConfigManager.getConfig().getString("PriceSource");
        if (priceSource.equalsIgnoreCase("Essentials")) {
            return EssentialsXHook.getEssentialsPrice(item);
        }
        else if (priceSource.equalsIgnoreCase("ShopGUI+") || ConfigManager.getConfig().getString("PriceSource").equalsIgnoreCase("ShopGUIPlus")) {
            return ShopGUIHook.getShopGUIPrice(item);
        }
        else if (priceSource.equalsIgnoreCase("Custom")) {
            return CustomPriceHook.getCustomPrice(item);
        }
        else {
            return 0;
        }
    }

}
