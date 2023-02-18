package me.waterarchery.autosellchest.hooks;

import org.bukkit.inventory.ItemStack;
import me.waterarchery.autosellchest.AutoSellMain;

public class CustomPriceHook {

    public static double getCustomPrice(ItemStack item){
        String type = item.getType().name();
        if (AutoSellMain.getPlugin().getConfig().get("CustomPrices." + type) != null) {
            return AutoSellMain.getPlugin().getConfig().getDouble("CustomPrices." + type + ".Price") * item.getAmount();
        }
        else {
            return -1;
        }
    }

}
