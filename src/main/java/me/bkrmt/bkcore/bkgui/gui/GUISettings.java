package me.bkrmt.bkcore.bkgui.gui;

import me.bkrmt.bkcore.bkgui.event.EnteredItemResponse;
import me.bkrmt.bkcore.bkgui.event.EmptyClickResponse;
import me.bkrmt.bkcore.bkgui.event.NotEnterableItemResponse;
import me.bkrmt.bkcore.bkgui.event.pagewipe.PageWipeCloseResponse;
import me.bkrmt.bkcore.bkgui.event.pagewipe.PageWipeLeaveResponse;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GUISettings {

    private boolean canEnterItems;
    private boolean canDrag;
    private final List<ItemStack> enterableItems = new ArrayList<>();
    private NotEnterableItemResponse notEnterableItemResponse;
    private EnteredItemResponse enteredItemResponse;
    private EmptyClickResponse emptyClickResponse;
    private PageWipeCloseResponse pageWipeCloseResponse;
    private PageWipeLeaveResponse pageWipeLeaveResponse;

    public void addEnterableItem(Material material) {
        this.enterableItems.add(new ItemStack(material));
    }

    public void addEnterableItem(Material material, short data) {
        this.enterableItems.add(new ItemStack(material, 1, data));
    }

    public void addEnterableItem(Material material, int data) {
        this.enterableItems.add(new ItemStack(material, 1, (short) data));
    }

    public boolean isCanEnterItems() {
        return canEnterItems;
    }

    public void setCanEnterItems(boolean canEnterItems) {
        this.canEnterItems = canEnterItems;
    }

    public boolean isCanDrag() {
        return canDrag;
    }

    public void setCanDrag(boolean canDrag) {
        this.canDrag = canDrag;
    }

    public List<ItemStack> getEnterableItems() {
        return enterableItems;
    }

    public PageWipeCloseResponse getPageWipeCloseResponse() {
        return pageWipeCloseResponse;
    }

    public void setPageWipeCloseResponse(PageWipeCloseResponse pageWipeCloseResponse) {
        this.pageWipeCloseResponse = pageWipeCloseResponse;
    }

    public PageWipeLeaveResponse getPageWipeLeaveResponse() {
        return pageWipeLeaveResponse;
    }

    public void setPageWipeLeaveResponse(PageWipeLeaveResponse pageWipeLeaveResponse) {
        this.pageWipeLeaveResponse = pageWipeLeaveResponse;
    }

    public NotEnterableItemResponse getNotEnterableItemResponse() {
        return notEnterableItemResponse;
    }

    public void setNotEnterableItemResponse(NotEnterableItemResponse notEnterableItemResponse) {
        this.notEnterableItemResponse = notEnterableItemResponse;
    }

    public EnteredItemResponse getEnteredItemResponse() {
        return enteredItemResponse;
    }

    public void setEnteredItemResponse(EnteredItemResponse enteredItemResponse) {
        this.enteredItemResponse = enteredItemResponse;
    }

    public EmptyClickResponse getEmptyClickResponse() {
        return emptyClickResponse;
    }

    public void setEmptyClickResponse(EmptyClickResponse emptyClickResponse) {
        this.emptyClickResponse = emptyClickResponse;
    }
}
