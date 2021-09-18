package me.bkrmt.bkcore.bkgui.page;

import me.bkrmt.bkcore.textanimator.TextAnimator;
import me.bkrmt.bkcore.bkgui.event.ElementResponse;
import me.bkrmt.bkcore.bkgui.item.HeadBuilder;
import me.bkrmt.bkcore.bkgui.item.ItemBuilder;

public class PageItem {
    private ItemBuilder item;
    private HeadBuilder head;
    private final TextAnimator titleAnimator;
    private ElementResponse response;
    private boolean unclickable;
    private String identifier;

    public PageItem(ItemBuilder item, String identifier, boolean unclickable, TextAnimator titleAnimator, ElementResponse response) {
        this.item = item;
        this.head = null;
        this.identifier = identifier;
        this.response = response;
        this.unclickable = unclickable;
        this.titleAnimator = titleAnimator;
    }

    public PageItem(HeadBuilder head, String identifier, boolean unclickable, TextAnimator titleAnimator, ElementResponse response) {
        this.head = head;
        this.item = null;
        this.identifier = identifier;
        this.response = response;
        this.unclickable = unclickable;
        this.titleAnimator = titleAnimator;
    }

    public void setPageItemResponse(ElementResponse response) {
        this.response = response;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public TextAnimator getTitleAnimator() {
        return titleAnimator;
    }

    public ElementResponse getPageItemResponse() {
        return response;
    }

    public ItemBuilder getPageItem() {
        return item;
    }

    public HeadBuilder getHead() {
        return head;
    }

    public void setPageItem(ItemBuilder item) {
        this.item = item;
    }

    public void setPageItem(HeadBuilder head) {
        this.head = head;
    }

    public boolean isUnclickable() {
        return unclickable;
    }

    public void setUnclickable(boolean unclickable) {
        this.unclickable = unclickable;
    }
}
