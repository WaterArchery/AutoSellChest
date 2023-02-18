package me.waterarchery.autosellchest;

import me.waterarchery.autosellchest.listeners.*;
import me.waterarchery.autosellchest.handlers.ChestHandler;
import me.waterarchery.autosellchest.handlers.ConfigManager;
import me.waterarchery.autosellchest.handlers.MainCommand;
import me.waterarchery.autosellchest.hooks.VaultHook;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class AutoSellMain extends JavaPlugin {

    private static Plugin pl;
    private static ArrayList<SellChest> sellChests = new ArrayList<>();
    public static int remTime;
    private static HoloType holoType;

    @Override
    public void onEnable() {
        pl = this;
        getServer().getConsoleSender().sendMessage("§7[§bAutoSellChest§7] §7Enabling AutoSellChest");
        ConfigManager.StartConfig();
        SetHoloType();
        if (!VaultHook.setupEconomy() ) {
            getServer().getConsoleSender().sendMessage(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        new Metrics(this, 16897);
        getServer().getPluginManager().registerEvents(new ChestClickEvent(), pl);
        getServer().getPluginManager().registerEvents(new ChestPlaceEvent(), pl);
        getServer().getPluginManager().registerEvents(new ChestBreakEvent(), pl);
        getServer().getPluginManager().registerEvents(new MenuClickEvent(), pl);
        getServer().getPluginManager().registerEvents(new MenuCloseEvent(), pl);
        ChestHandler.CreateChestItem();
        ChestHandler.StartRemainingTime();
        getServer().getPluginCommand("asc").setExecutor(new MainCommand());
        getServer().getConsoleSender().sendMessage("§7[§bAutoSellChest§7] §7Registering plugin listeners");
        ConfigManager.LoadAllChestFromData();
        getServer().getConsoleSender().sendMessage("§7[§bAutoSellChest§7] §aAutoSellChest enabled §cv" + this.getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        ConfigManager.SaveData();
        for (SellChest chest : sellChests) {
            chest.DeleteHologram();
        }
    }

    public void SetHoloType() {
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("DecentHolograms")) {
            holoType = HoloType.DECENTHOLOGRAMS;
        }
        else if (Bukkit.getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
            holoType = HoloType.HOLOGRAPHICDISPLAYS;
        }
        else {
            Bukkit.getConsoleSender().sendMessage("§bAutoSellChest - §cNo hologram plugin found in the server.");
            holoType = null;
        }
    }

    public static ArrayList<SellChest> getSellChests(){ return sellChests; }
    public static Plugin getPlugin(){ return pl; }
    public static HoloType getHoloType() { return holoType; }
}
