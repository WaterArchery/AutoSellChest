package tr.waterarchery.autosellchest.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import tr.waterarchery.autosellchest.AutoSellMain;
import tr.waterarchery.autosellchest.SellChest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConfigManager {

    private static File langFile;
    private static YamlConfiguration lang;
    private static File dataFile;
    private static YamlConfiguration data;
    private static Plugin pl;

    public static void StartConfig(){
        pl = AutoSellMain.getPlugin();
        pl.saveDefaultConfig();
        CreateLang();
        CreateData();
        new BukkitRunnable() {
            @Override
            public void run() {
                SaveData();
            }
        }.runTaskTimer(pl, 0L, pl.getConfig().getLong("DataSaveInterval"));
    }

    public static void SaveData(){
        try {
            data.save(dataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static void CreateLang(){
        langFile = new File(pl.getDataFolder(), "lang.yml");
        lang = new YamlConfiguration();
        if (!langFile.exists()) {
            pl.saveResource("lang.yml", false);
        }
        try {
            lang.load(langFile);
        } catch (IOException | NullPointerException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private static void CreateData(){
        dataFile = new File(pl.getDataFolder(), "data.yml");
        data = new YamlConfiguration();
        if (!dataFile.exists()) {
            pl.saveResource("data.yml", false);
        }
        try {
            data.load(dataFile);
        } catch (IOException | NullPointerException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static String getString(String path){
        try {
            return pl.getConfig().getString(path).replace("&", "§");
        } catch (Exception e){
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("§cAutoSellChest - ERROR IN CONFIG.YML PATH: " + path);
            return null;
        }
    }

    public static String getStringLang(String path){
        try {
            return lang.getString(path).replace("&", "§");
        } catch (Exception e){
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("§cAutoSellChest - ERROR IN LANG.YML PATH: " + path);
            return null;
        }
    }

    public static void SendMessage(CommandSender sender, String mes){
        String prefix = getStringLang("Prefix");
        sender.sendMessage(prefix + " " + mes);
    }

    public static void SendMessage(CommandSender sender, boolean config, String path){
        String prefix = getStringLang("Prefix");
        String mes;
        if (config) {
            mes = getString(path);
        }
        else {
            mes = getStringLang(path);
        }
        sender.sendMessage(prefix + " " + mes);
    }

    public static List<String> GetLangList(String path){
        List<String> tempList = lang.getStringList(path);
        ArrayList<String> holoLines = new ArrayList<>();
        for (String part : tempList) {
            holoLines.add(part.replace("&", "§"));
        }
        return holoLines;
    }

    public static void SetDataVariable(SellChest chest){

    }

    public static void LoadAllChestFromData(){
        AutoSellMain.getSellChests().clear();
        for (String uuid : data.getConfigurationSection("").getKeys(false)) {
            ArrayList<ItemStack> contents;
            if (ConfigManager.getData().get(uuid + ".Contents") == null) {
                contents = null;
            }
            else{
                contents = (ArrayList<ItemStack>) ConfigManager.getData().get(uuid + ".Contents");
            }
            Location loc = (Location) ConfigManager.getData().get(uuid + ".Location");
            String ownerName = ConfigManager.getData().getString(uuid + ".OwnerName");
            boolean status = ConfigManager.getData().getBoolean(uuid + ".Working");
            SellChest chest =  new SellChest(contents, loc, UUID.fromString(uuid), ownerName, status);
            AutoSellMain.getSellChests().add(chest);
            chest.PlaceHologram();
        }
    }

    public static FileConfiguration getConfig() { return pl.getConfig(); }
    public static YamlConfiguration getLang() { return lang; }
    public static YamlConfiguration getData() { return data; }

}
