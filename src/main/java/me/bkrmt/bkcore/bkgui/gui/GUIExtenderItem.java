package me.bkrmt.bkcore.bkgui.gui;

import me.bkrmt.bkcore.bkgui.event.ElementResponse;
import me.bkrmt.bkcore.bkgui.item.ItemBuilder;
import me.bkrmt.bkcore.xlibs.XMaterial;
import org.bukkit.entity.Player;

public abstract class GUIExtenderItem implements ElementResponse {

    private final ItemBuilder itemBuilder;
    private boolean pullable;

    public GUIExtenderItem(ItemBuilder itemBuilder) {
        this.itemBuilder = itemBuilder;
    }

    public GUIExtenderItem() {
        this.itemBuilder = new ItemBuilder(XMaterial.AIR);
    }

    // You can override this based on a player for example.
    public ItemBuilder getItemBuilder(Player player) {
        return itemBuilder;
    }

    public boolean isPullable() {
        return pullable;
    }

    public void setPullable(boolean pullable) {
        this.pullable = pullable;
    }

}