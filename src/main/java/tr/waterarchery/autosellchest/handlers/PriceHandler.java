package tr.waterarchery.autosellchest.handlers;

import org.bukkit.inventory.ItemStack;
import tr.waterarchery.autosellchest.hooks.CustomPriceHook;
import tr.waterarchery.autosellchest.hooks.EssentialsXHook;
import tr.waterarchery.autosellchest.hooks.ShopGUIHook;

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
