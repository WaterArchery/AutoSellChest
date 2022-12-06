package tr.waterarchery.autosellchest.hooks;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;

public class EssentialsXHook {

    public static double getEssentialsPrice(ItemStack item){
        Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        BigDecimal price = essentials.getWorth().getPrice(essentials, item);
        if (price == null) {
            return -1;
        }
        else {
            return price.doubleValue() * item.getAmount();
        }
    }

}
