package me.waterarchery.autosellchest.handlers;

import me.waterarchery.autosellchest.SellChest;
import me.waterarchery.autosellchest.hooks.VaultHook;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import me.waterarchery.autosellchest.AutoSellMain;

import java.util.HashMap;

public class MainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("asc")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                Plugin pl = AutoSellMain.getPlugin();
                if (args.length == 0) {
                    for (String mes : ConfigManager.getLang().getStringList("HelpCommand")) {
                        ConfigManager.SendMessage(p, mes.replace("&", "§"));
                    }
                    return true;
                }
                else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("get")) {
                        if (sender.hasPermission("asc.get")) {
                            double price = pl.getConfig().getDouble("ChestPrice");
                            if (price == 0) {
                                p.getInventory().addItem(ChestHandler.getChestItem());
                                ConfigManager.SendMessage(p, false, "BoughtChest");
                            }
                            else {
                                double balance = VaultHook.getEconomy().getBalance(p);
                                if (balance >= price) {
                                    HashMap<Integer, ItemStack> hash = p.getInventory().addItem(ChestHandler.getChestItem());
                                    if (hash != null) {
                                        VaultHook.getEconomy().withdrawPlayer(p, price);
                                        ConfigManager.SendMessage(p, false, "BoughtChest");
                                    }
                                    else {
                                        String title = ConfigManager.getStringLang("NoSpaceTitle" + ".Title");
                                        String subtitle = ConfigManager.getStringLang("NoSpaceTitle" + ".SubTitle");
                                        SoundAndTitleHandler.SendTitle(p, title, subtitle);
                                        SoundAndTitleHandler.SendSound("NoSpaceSound", p);
                                    }
                                }
                                else {
                                    ConfigManager.SendMessage(p, false, "NotEnoughMoney");
                                }
                            }
                        }
                        else {
                            ConfigManager.SendMessage(p, false, "NoPermission");
                        }
                        return true;
                    }
                    else if (args[0].equalsIgnoreCase("delete")) {
                        SellChest chest = ChestHandler.getSellChestFromData(p.getUniqueId().toString());
                        if (chest != null) {
                            chest.DeleteHologram();
                            chest.getLoc().getBlock().setType(Material.AIR);
                            ChestHandler.RemoveChestFromData(p.getUniqueId().toString());
                        }
                        else {
                            ConfigManager.SendMessage(p, false, "YouDontHaveChest");
                        }
                        return true;
                    }
                    else if (args[0].equalsIgnoreCase("collect")) {
                        SellChest chest = ChestHandler.getSellChestFromData(p.getUniqueId().toString());
                        if (chest != null) {
                            double money = chest.getMoney();
                            if (money > 0) {
                                String mes = ConfigManager.getStringLang("MoneyCollected").replace("%money%", money + "");
                                String title = ConfigManager.getStringLang("MoneyCollectedTitle" + ".Title").replace("%money%", money + "");
                                String subtitle = ConfigManager.getStringLang("MoneyCollectedTitle" + ".SubTitle").replace("%money%", money + "");
                                SoundAndTitleHandler.SendTitle(p, title, subtitle);
                                SoundAndTitleHandler.SendSound("MoneyCollectedSound", p);
                                ConfigManager.SendMessage(p, mes);
                                VaultHook.getEconomy().depositPlayer(p, money);
                                chest.setMoney(0.0);
                            }
                            else {
                                ConfigManager.SendMessage(p, false, "NoMoneyToCollected");
                            }
                        }
                        else {
                            ConfigManager.SendMessage(p, false, "YouDontHaveChest");
                        }
                        return true;
                    }
                    else if (args[0].equalsIgnoreCase("help")) {
                        for (String mes : ConfigManager.getConfig().getStringList("HelpCommand")) {
                            ConfigManager.SendMessage(p, mes.replace("&", "§"));
                        }
                        return true;
                    }
                }
                else if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("admin")) {
                        if (args[1].equalsIgnoreCase("delete")) {
                            if (p.hasPermission("asc.remove.player")) {
                                SellChest chest = ChestHandler.getSellChestFromName(args[2]);
                                if (chest != null) {
                                    ChestHandler.RemoveChestFromData(chest.getOwner().toString());
                                    String mesAdmin = ConfigManager.getStringLang("ChestRemovedAdmin").replace("%player%", chest.getOwnerName());
                                    ConfigManager.SendMessage(p, mesAdmin);

                                }
                                else {
                                    String mes = ConfigManager.getStringLang("PlayerNotFound").replace("%player%", args[2]);
                                    ConfigManager.SendMessage(p, mes);
                                }
                            }
                            else {
                                ConfigManager.SendMessage(p, false, "NoPermission");
                            }
                            return true;
                        }
                    }
                }
            }
            for (String mes : ConfigManager.getConfig().getStringList("HelpCommand")) {
                ConfigManager.SendMessage(sender, mes.replace("&", "§"));
            }
        }
        return false;
    }
}
