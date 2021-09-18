package me.bkrmt.bkcore.bkgui.page;

import me.bkrmt.bkcore.textanimator.TextAnimator;
import me.bkrmt.bkcore.bkgui.event.ElementResponse;
import me.bkrmt.bkcore.bkgui.item.ItemBuilder;

public class BackMenuButton extends PageItem{
    private Object customObject;
    private final int slot;

    public BackMenuButton(int slot, ItemBuilder item, String identifier, boolean unclickable, TextAnimator titleAnimator, ElementResponse response) {
        super(item, identifier, unclickable, titleAnimator, response);
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }

    public Object getCustomObject() {
        return customObject;
    }

    public void setCustomObject(Object customObject) {
        this.customObject = customObject;
    }
}
