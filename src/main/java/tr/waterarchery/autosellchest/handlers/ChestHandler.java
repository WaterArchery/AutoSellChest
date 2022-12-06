package tr.waterarchery.autosellchest.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import tr.waterarchery.autosellchest.AutoSellMain;
import tr.waterarchery.autosellchest.SellChest;
import tr.waterarchery.autosellchest.menus.ManageItemsMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChestHandler {

    private static ItemStack chestItem;

    public static void CreateChestItem() {
        Material type = Material.getMaterial(ConfigManager.getStringLang("ChestItem.Material"));
        String name = ConfigManager.getStringLang("ChestItem.Name");
        List<String> lore = ConfigManager.GetLangList("ChestItem.Lore");
        chestItem = new ItemStack(type);
        ItemMeta meta = chestItem.getItemMeta();
        meta.setDisplayName(name.replace("&", "§"));
        meta.setLore(lore);
        chestItem.setItemMeta(meta);
    }

    public static ItemStack getChestItem() {
        return chestItem;
    }

    public static SellChest getSellChestFromData(String uuid) {
        if (ConfigManager.getData().get(uuid) == null) {
            return null;
        } else {
            ArrayList<ItemStack> contents;
            if (ConfigManager.getData().get(uuid + ".Contents") == null) {
                contents = null;
            } else {
                contents = (ArrayList<ItemStack>) ConfigManager.getData().get(uuid + ".Contents");
            }
            Location loc = (Location) ConfigManager.getData().get(uuid + ".Location");
            String ownerName = ConfigManager.getData().getString(uuid + ".OwnerName");
            boolean status = ConfigManager.getData().getBoolean(uuid + ".Working");
            return new SellChest(contents, loc, UUID.fromString(uuid), ownerName, status);
        }
    }

    public static SellChest getSellChestFromName(String name) {
        for (String uuid : ConfigManager.getData().getConfigurationSection("").getKeys(false)) {
            String ownerName = ConfigManager.getData().getString(uuid + ".OwnerName");
            if (ownerName.equalsIgnoreCase(name)) {
                return getSellChestFromList(uuid);
            }
        }
        return null;
    }

    public static SellChest getSellChestFromList(String uuid) {
        for (SellChest chest : AutoSellMain.getSellChests()) {
            if (chest.getOwner().toString().equalsIgnoreCase(uuid)) {
                return chest;
            }
        }
        return null;
    }

    public static void RemoveChestFromData(String uuid) {
        Player p = Bukkit.getPlayer(UUID.fromString(uuid));
        if (ConfigManager.getData().get(uuid) == null) {
            if (p != null) {
                ConfigManager.SendMessage(p, false, "YouDontHaveChest");
            }
        } else {
            ConfigManager.getData().set(uuid, null);
            SellChest chest = getSellChestFromList(uuid);
            AutoSellMain.getSellChests().remove(chest);
            if (p != null) {
                ConfigManager.SendMessage(p, false, "ChestRemoved");
            }
        }
    }

    public static void SaveChestToData(SellChest chest) {
        Location loc = chest.getLoc();
        double money = chest.getMoney();
        String uuid = chest.getOwner().toString();
        String ownerName = chest.getOwnerName();
        boolean status = chest.isWorking();
        ArrayList<ItemStack> contents;
        if (chest.getContents() != null) {
            contents = chest.getContents();
        } else {
            contents = null;
        }
        ConfigManager.getData().set(uuid + ".OwnerName", ownerName);
        ConfigManager.getData().set(uuid + ".Money", money);
        ConfigManager.getData().set(uuid + ".Location", loc);
        ConfigManager.getData().set(uuid + ".Contents", contents);
        ConfigManager.getData().set(uuid + ".Working", status);
    }

    public static void SellItemsInChests() {
        for (SellChest chest : AutoSellMain.getSellChests()) {
            if (chest.isWorking()) {
                double price = 0;
                Inventory playerInv = chest.getContentsInventory();
                for (int i = 0; i < playerInv.getSize(); i++) {
                    ItemStack item = chest.getContentsInventory().getItem(i);
                    if (item != null) {
                        if (!item.isSimilar(ManageItemsMenu.manageItemsItem())) {
                            if (!item.getType().equals(Material.AIR)) {
                                double priceItem = PriceHandler.getPrice(item);
                                if (priceItem > 0) {
                                    price += priceItem;
                                    playerInv.setItem(i, null);
                                }
                            }
                        }
                    }
                }
                if (price > 0) {
                    OfflinePlayer p = Bukkit.getOfflinePlayer(chest.getOwner());
                    if (p.isOnline()) {
                        String mes = ConfigManager.getStringLang("ItemsSold").replace("%money%", price + "");
                        ConfigManager.SendMessage((CommandSender) p, mes);
                    }
                    double chestBalance = chest.getMoney();
                    chest.setMoney(chestBalance + price);
                    chest.setContents(playerInv);
                }
            }
            chest.UpdateHologram();
        }
        ConfigManager.SaveData();
    }


    public static void StartRemainingTime(){
        int holoUpdateTime = ConfigManager.getConfig().getInt("IntervalHoloReplacer");
        final int[] i = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                if (i[0] != 0) {
                    AutoSellMain.remTime -= holoUpdateTime;
                }
                else {
                    i[0]++;
                }
                if (AutoSellMain.remTime <= 0) {
                    SellItemsInChests();
                    AutoSellMain.remTime = ConfigManager.getConfig().getInt("SellInterval");
                }
                for (SellChest chest : AutoSellMain.getSellChests()) {
                    chest.UpdateHologram();
                }
            }
        }.runTaskTimer(AutoSellMain.getPlugin(), 0, holoUpdateTime * 20L);
    }

}
