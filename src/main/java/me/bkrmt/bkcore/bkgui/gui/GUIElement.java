package me.bkrmt.bkcore.bkgui.gui;

import me.bkrmt.bkcore.bkgui.event.ElementResponse;

public class GUIElement {

    private int slot;
    private ElementResponse elementResponse;
    private GUIExtenderItem guiExtenderItem;
    private boolean pullable;

    public GUIElement(int slot) {
        this.slot = slot;
    }

    public GUIElement(int slot, boolean pullable) {
        this.slot = slot;
        this.pullable = pullable;
    }

    public void addElementResponse(int slot, ElementResponse elementResponse) {
        this.slot = slot;
        this.elementResponse = elementResponse;
        this.pullable = false;
    }

    public void addElementResponse(int slot, boolean pullable, ElementResponse elementResponse) {
        this.slot = slot;
        this.elementResponse = elementResponse;
        this.pullable = pullable;
    }

    public void addElementResponse(int slot, GUIExtenderItem guiExtenderItem) {
        this.slot = slot;
        this.pullable = guiExtenderItem.isPullable();
        this.guiExtenderItem = guiExtenderItem;
    }

    public int getSlot() {
        return slot;
    }

    public ElementResponse getElementResponse() {
        return elementResponse;
    }

    public GUIExtenderItem getGuiExtenderItem() {
        return guiExtenderItem;
    }

    public boolean isPullable() {
        return pullable;
    }

    @Override
    public String toString() {
        return "[" + slot + "] " + elementResponse.hashCode();
    }
}
