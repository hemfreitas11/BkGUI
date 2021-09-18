package me.bkrmt.bkcore.bkgui.menus.numberinput;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.bkgui.BkGUI;
import me.bkrmt.bkcore.bkgui.MenuSound;
import me.bkrmt.bkcore.bkgui.gui.GUI;
import me.bkrmt.bkcore.bkgui.gui.Rows;
import me.bkrmt.bkcore.bkgui.item.ItemBuilder;
import me.bkrmt.bkcore.bkgui.page.Page;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class NumberInputMenu {
    private final BkPlugin plugin;
    private final Page menu;
    private List<Modifier> modifiers;
    private ItemStack valueDisplay;
    private InputCompletedResponse inputCompleted;
    private int displaySlot = 22;
    private BigDecimal value;
    private final Object castType;
    private final Player player;

    public NumberInputMenu(Player player, String title, InputCompletedResponse inputCompleted, String startingValue, Object castType) {
        plugin = BkGUI.INSTANCE.getInstance();
        this.player = player;
        this.castType = castType;
        this.inputCompleted = inputCompleted;
        value = new BigDecimal(startingValue);
        menu = new Page(getPlugin(), getPlugin().getAnimatorManager(), new GUI(title, Rows.FIVE), 1);
    }

    public NumberInputMenu buildMenu() {
        menu.pageSetItem(displaySlot,
                new ItemBuilder(valueDisplay).setName("§e" + formatValue()).setLore("&7Click here to confirm and go back."),
                player.getName().toLowerCase() + "-numberinput-value-display-slot-" + displaySlot,
                event -> inputCompleted.onComplete(value, menu, event)
        );

        for (Modifier modifier : modifiers) {
            menu.pageSetItem(modifier.getSlot(),
                    modifier.getBuilder(),
                    player.getName().toLowerCase() + "-numberinput-positive-increment-slot-" + modifier.getSlot(),
                    event -> {
                        if (modifier.isPositive()) MenuSound.FOWARD.play(event.getWhoClicked());
                        else MenuSound.BACK.play(event.getWhoClicked());
                        value = value.add(modifier.getIncrement());
                        menu.getGui().updateItem(displaySlot, (value.doubleValue() > 0 ? "§a" : (value.doubleValue() == 0 ? "§e" : "§c")) + formatValue(), null);
                    }
            );
        }
        return this;
    }

    private String formatValue() {
        if (castType instanceof Integer || castType instanceof Long) {
            return String.valueOf(value.longValue());
        }
        return String.valueOf(value.doubleValue());
    }

    public NumberInputMenu setValueDispay(ItemStack valueDisplay, int displaySlot) {
        this.valueDisplay = valueDisplay;
        if (displaySlot > 0)
            this.displaySlot = displaySlot;
        return this;
    }

    public NumberInputMenu setModifiers(Object... increments) {
        this.modifiers = new ArrayList<>();
        for (Object increment : increments) {
            if (increment instanceof Modifier) {
                this.modifiers.add((Modifier) increment);
            }
        }
        return this;
    }

    public List<Modifier> getModifiers() {
        return modifiers;
    }

    public ItemStack getValueDisplay() {
        return valueDisplay;
    }

    public Page getMenu() {
        return menu;
    }

    public BkPlugin getPlugin() {
        return plugin;
    }
}
