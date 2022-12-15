package tr.waterarchery.autosellchest.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;


public class SoundAndTitleHandler {

    public static void SendSound(String path, Player p){
        if (ConfigManager.getConfig().get(path) != null) {
            String soundName = ConfigManager.getConfig().getString(path);
            try {
                Sound sound = Sound.valueOf(soundName);
                p.playSound(p.getLocation(), sound, 5f, 5f);
            }
            catch (Exception e){
                Bukkit.getConsoleSender().sendMessage("§bAutoSellChest - §cUnable to find sound in " + path);
            }
        }
    }

    public static void SendTitle(Player p, String titleText, String subtitleText){
            p.sendTitle(titleText, subtitleText, 10, 40, 10);
    }

    //Just in case I need it
    public static boolean isLegacy(){
        return Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9")
                || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11")
                || Bukkit.getVersion().contains("1.12");
    }

}
