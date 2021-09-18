package me.bkrmt.bkcore.bkgui.event;

import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public interface WindowResponse {

    void onOpen(InventoryOpenEvent event);
    void onClose(InventoryCloseEvent event);

}