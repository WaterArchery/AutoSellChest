package tr.waterarchery.autosellchest;

import eu.decentsoftware.holograms.api.DHAPI;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tr.waterarchery.autosellchest.handlers.ConfigManager;
import tr.waterarchery.autosellchest.handlers.PriceHandler;
import tr.waterarchery.autosellchest.menus.ManageItemsMenu;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

enum HoloType{
    DECENTHOLOGRAMS,
    HOLOGRAPHICDISPLAYS
}

public class SellChest {

    public ArrayList<ItemStack> contents;
    public Location loc;
    public UUID owner;
    public String ownerName;
    public boolean status;
    public Inventory itemsInventory;

    public SellChest(ArrayList<ItemStack> contents, Location loc, UUID owner, String ownerName, boolean status) {
        this.contents = contents;
        this.loc = loc;
        this.owner = owner;
        this.ownerName = ownerName;
        this.status = status;
        this.itemsInventory = ManageItemsMenu.getManageItemsMenu(this);

    }

    public void PlaceHologram(){
        double holoHeight = ConfigManager.getConfig().getDouble("HologramHeight");
        Location loc = this.getLoc().clone().add(0.5, holoHeight,0.5);
        List<String> tempLines = ConfigManager.GetLangList("HologramLines");
        ArrayList<String> lines = new ArrayList<>();
        int itemSize = 0;
        if (this.getContents() != null) {
            for (ItemStack item : this.getContents()) {
                if (item != null) {
                    itemSize += item.getAmount();
                }
            }
        }
        for (String line : tempLines) {
            if (line.contains("%status%")) {
                if (this.isWorking()) {
                    line = line.replace("%status%", ConfigManager.getStringLang("Status.working"));
                }
                else {
                    line = line.replace("%status%", ConfigManager.getStringLang("Status.stopped"));
                }
            }
            if (line.contains("%time%")) {
                line = line.replace("%time%", getTimePlaceHolder());
            }
            line = line.replace("%items%", itemSize + "");
            if (line.contains("%money%")) {
                line = line.replace("%money%", df.format(getMoney()) + "");
            }

            lines.add(line);
        }
        HoloType type = AutoSellMain.getHoloType();
        switch (type) {
            case DECENTHOLOGRAMS:
                String holoName = this.getOwnerName() + "-SellChest";
                if (DHAPI.getHologram(holoName) != null) {
                    DHAPI.getHologram(holoName).delete();;
                }
                DHAPI.createHologram(holoName, loc, lines);
                break;
            case HOLOGRAPHICDISPLAYS:
                HolographicDisplaysAPI holoApi = HolographicDisplaysAPI.get(AutoSellMain.getPlugin());
                for (Hologram tempHolo : holoApi.getHolograms()) {
                    if (tempHolo.getPosition().distance(tempHolo.getPosition()) < 0.5) {
                        tempHolo.delete();
                        break;
                    }
                }
                Hologram holo = holoApi.createHologram(loc);
                for (String line : lines) {
                    holo.getLines().appendText(line);
                }
                break;
        }
    }

    public void UpdateHologram(){
        PlaceHologram();
    }
    public void DeleteHologram(){
        HoloType type = AutoSellMain.getHoloType();
        switch (type) {
            case DECENTHOLOGRAMS:
                String holoName = ownerName + "-SellChest";
                if (DHAPI.getHologram(holoName) != null) {
                    DHAPI.getHologram(holoName).delete();;
                }
                break;
            case HOLOGRAPHICDISPLAYS:
                HolographicDisplaysAPI holoApi = HolographicDisplaysAPI.get(AutoSellMain.getPlugin());
                for (Hologram holo : holoApi.getHolograms()) {
                    if (holo.getPosition().distance(holo.getPosition()) < 0.5) {
                        holo.delete();
                        break;
                    }
                }
                break;
        }
    }

    public String getTimePlaceHolder(){
        String finalTime = "";
        int remTime = AutoSellMain.remTime;
        if (remTime / 3600 == 1) {
            finalTime = finalTime + (remTime / 3600) + " " + ConfigManager.getStringLang("TimeNames.Hour") + " ";
        }
        else if (remTime / 3600 > 1) {
            finalTime = finalTime + (remTime / 3600) + " " + ConfigManager.getStringLang("TimeNames.Hours") + " ";
        }
        if (remTime / 60 == 1) {
            finalTime = finalTime + (remTime / 60) + " " + ConfigManager.getStringLang("TimeNames.Minute") + " ";
        }
        else if (remTime / 60 > 1) {
            finalTime = finalTime + (remTime / 60) + " " + ConfigManager.getStringLang("TimeNames.Minutes") + " ";
        }
        if (remTime % 60 == 1) {
            finalTime = finalTime + (remTime % 60) + " " + ConfigManager.getStringLang("TimeNames.Second") + " ";
        }
        else if (remTime % 60 > 1) {
            finalTime = finalTime + (remTime % 60) + " " + ConfigManager.getStringLang("TimeNames.Seconds") + " ";
        }
        else if (remTime == 0) {
            finalTime = finalTime + ConfigManager.getStringLang("TimeNames.Now");
        }
        return finalTime;
    }

    public ArrayList<ItemStack> getContents() {
        return contents;
    }

    public void setContents(ArrayList<ItemStack> contents) {
        ConfigManager.getData().set(owner.toString() + ".Contents", contents);
        this.contents = contents;
        PlaceHologram();
    }

    public void setContents(Inventory contents) {
        ArrayList<ItemStack> newContents = new ArrayList<>();
        for (ItemStack item : contents) {
            if (item != null) {
                if (!item.getType().equals(Material.AIR)) {
                    if (!item.isSimilar(ManageItemsMenu.manageItemsItem())) {
                        if (PriceHandler.getPrice(item) > 0) {
                            newContents.add(item);
                        }
                    }
                }
            }
        }
        ConfigManager.getData().set(owner.toString() + ".Contents", newContents);
        this.contents = newContents;
        UpdateHologram();
    }


    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    private static final DecimalFormat df = new DecimalFormat("0.00");
    public double getMoney() {
        return ConfigManager.getData().getDouble(owner.toString() + ".Money");
    }

    public void setMoney(double money) {
        ConfigManager.getData().set(owner.toString() + ".Money", money);
        UpdateHologram();
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public boolean isWorking() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
        ConfigManager.getData().set(owner.toString() + ".Working", status);
        PlaceHologram();
    }

    public Inventory getContentsInventory() { return itemsInventory; }

}
