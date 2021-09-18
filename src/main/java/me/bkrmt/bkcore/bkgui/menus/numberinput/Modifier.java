package me.bkrmt.bkcore.bkgui.menus.numberinput;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.bkgui.BkGUI;
import me.bkrmt.bkcore.bkgui.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Modifier {
    private final ItemBuilder displayItem;
    private final BigDecimal increment;
    private final boolean positive;
    private final int slot;

    public Modifier(int slot, ItemStack displayItem, String incrementObject) {
        BkPlugin plugin = BkGUI.INSTANCE.getInstance();
        String incrementString;
        try {
            incrementString = String.valueOf(Long.parseLong(incrementObject)).replace("-", "");
        } catch (Exception ignored) {
            incrementString = String.valueOf(Double.parseDouble(incrementObject)).replace("-", "");
        }
        this.increment = new BigDecimal(incrementObject);
        this.positive = increment.longValue() >= 0;
        this.slot = slot;
        this.displayItem = new ItemBuilder(displayItem)
                .setName(plugin.getLangFile().get("info.number-input." + (positive ? "increase" : "decrease") + ".name").replace("{amount}", incrementString));
        List<String> lore = new ArrayList<>();
        for (String line : plugin.getLangFile().getStringList("info.number-input." + (positive ? "increase" : "decrease") + ".description")) {
            lore.add(line.replace("{amount}", incrementString));
        }
        this.displayItem.setLore(lore);
    }

    public boolean isPositive() {
        return positive;
    }

    public ItemBuilder getDisplayItem() {
        return displayItem;
    }

    public int getSlot() {
        return slot;
    }

    public ItemBuilder getBuilder() {
        return displayItem;
    }

    public BigDecimal getIncrement() {
        return increment;
    }
}
