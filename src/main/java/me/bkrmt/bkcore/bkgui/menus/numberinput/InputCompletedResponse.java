package me.bkrmt.bkcore.bkgui.menus.numberinput;

import me.bkrmt.bkcore.bkgui.page.Page;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.math.BigDecimal;

public interface InputCompletedResponse {

    void onComplete(BigDecimal value, Page inputPage, InventoryClickEvent event);

}
