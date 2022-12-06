package tr.waterarchery.autosellchest.hooks;

import net.brcdev.shopgui.ShopGuiPlusApi;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class ShopGUIHook implements Listener {

    public static double getShopGUIPrice(ItemStack item){
        double price = ShopGuiPlusApi.getItemStackPriceSell(item);
        if (price == 0) {
            return -1;
        }
        else {
            return price;
        }
    }

}
