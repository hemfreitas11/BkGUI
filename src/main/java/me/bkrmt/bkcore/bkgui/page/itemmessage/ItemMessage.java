package me.bkrmt.bkcore.bkgui.page.itemmessage;

import org.bukkit.inventory.ItemStack;

public class ItemMessage {
    private final ItemStack displayItem;
    private double seconds;
    private final int slot;
    private MessageRunnable messageRunnable;

    public ItemMessage(ItemStack displayItem, double seconds, int slot) {
        this.displayItem = displayItem;
        this.seconds = seconds;
        this.slot = slot;
        messageRunnable = null;
    }

    public ItemMessage setMessageRunnable(MessageRunnable messageRunnable) {
        this.messageRunnable = messageRunnable;
        return this;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public double getSeconds() {
        return seconds;
    }

    public int getSlot() {
        return slot;
    }

    public MessageRunnable getMessageRunnable() {
        return messageRunnable;
    }
}
