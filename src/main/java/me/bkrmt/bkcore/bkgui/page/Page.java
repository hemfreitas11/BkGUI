package me.bkrmt.bkcore.bkgui.page;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkcore.bkgui.BkGUI;
import me.bkrmt.bkcore.bkgui.MenuSound;
import me.bkrmt.bkcore.bkgui.event.ElementResponse;
import me.bkrmt.bkcore.bkgui.gui.GUI;
import me.bkrmt.bkcore.bkgui.gui.GUIExtender;
import me.bkrmt.bkcore.bkgui.item.HeadBuilder;
import me.bkrmt.bkcore.bkgui.item.ItemBuilder;
import me.bkrmt.bkcore.bkgui.page.itemmessage.ItemMessage;
import me.bkrmt.bkcore.bkgui.page.itemmessage.MessageRunnable;
import me.bkrmt.bkcore.textanimator.AnimationPlaceholder;
import me.bkrmt.bkcore.textanimator.AnimatorManager;
import me.bkrmt.bkcore.textanimator.TextAnimator;
import me.bkrmt.bkcore.xlibs.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Page extends GUIExtender implements Comparable<Page>{
    private final int pageNumber;
    private Page nextPage;
    private Page previousPage;
    private final List<Page> previousMenus;
    private final List<Page> nextMenus;
    private ItemBuilder backButton;
    private ItemBuilder nextButton;
    private BackMenuButton backMenuButton;
    private final Map<Integer, PageItem> itemStorage;
    private final BkPlugin plugin;
    private boolean switchingPages;
    private boolean isWiped;
    private boolean wipeOnlySelf;
    private int[] buttonSlots;
    private boolean unregisterOnClose;
    private final AnimatorManager animatorManager;
    private final ConcurrentLinkedQueue<ItemMessage> displayMessages;

    public Page(BkPlugin plugin, AnimatorManager animatorManager, GUI gui, int pageNumber) {
        super(gui);
        this.plugin = plugin;
        this.animatorManager = animatorManager;
        this.pageNumber = pageNumber;
        this.itemStorage = new HashMap<>();
        displayMessages = new ConcurrentLinkedQueue<>();
        unregisterOnClose = false;
        isWiped = false;
        switchingPages = false;
        nextButton = null;
        wipeOnlySelf = false;
        buttonSlots = null;
        backMenuButton = null;
        backButton = null;
        nextPage = null;
        previousPage = null;
        previousMenus = new ArrayList<>();
        nextMenus = new ArrayList<>();
        BkGUI.INSTANCE.getPages().put(getGui().getID(), this);
    }

    public Page openGui(Player player) {
        buildPage();
        getGui().openInventory(player);
        return this;
    }

    public static String getRows(int rowsInt) {
        String rows = "ONE";
        switch (rowsInt) {
            case 1:
                rows = "ONE";
                break;
            case 2:
                rows = "TWO";
                break;
            case 3:
                rows = "THREE";
                break;
            case 4:
                rows = "FOUR";
                break;
            case 5:
                rows = "FIVE";
                break;
            case 6:
                rows = "SIX";
                break;
        }
        return rows;
    }

    public AnimatorManager getAnimatorManager() {
        return animatorManager;
    }

    public BackMenuButton getBackMenuButton() {
        return backMenuButton;
    }

    private Page buildPage() {
        int[] slots = getButtonSlots();
        if (slots[0] > 0 && slots[1] > 0) {
            if (nextPage != null) {
                if (itemStorage.get(slots[1]) == null) {
                    pageSetItem(slots[1], nextButton, "page-next-page-button-id" + getGui().getID(), event -> {
                        MenuSound.FOWARD.play((Player) event.getWhoClicked());
                        pauseAnimators();
                        setSwitchingPages(true);
                        nextPage.openGui((Player) event.getWhoClicked());
                    });
                }
            }
            if (previousPage != null || (!previousMenus.isEmpty() && backMenuButton != null)) {
                if (itemStorage.get(backMenuButton != null ? backMenuButton.getSlot() : slots[0]) == null) {
                    ElementResponse backResponse = event -> {
                        MenuSound.BACK.play((Player) event.getWhoClicked());
                        pauseAnimators();
                        setSwitchingPages(true);
                        previousPage.openGui((Player) event.getWhoClicked());
                    };

                    if (backMenuButton == null) {
                        pageSetItem(slots[0], backButton, "page-previous-page-button-id" + getGui().getID(), backResponse);
                    } else {
                        pageSetItem(backMenuButton.getSlot(), backMenuButton.getPageItem(), "page-previous-menu-button-id" + getGui().getID(), backMenuButton.getPageItemResponse());
                    }
                }
            }
        }
        return this;
    }

    public void pauseAnimators() {
        for (PageItem pageItem : itemStorage.values()) {
            TextAnimator titleAnimator = pageItem.getTitleAnimator();
            if (titleAnimator != null) {
                if (titleAnimator.getAnimationTask() != null) {
                    titleAnimator.pause();
                }
            }
        }
    }

    public void setBackMenuButton(int slot, ItemBuilder item, String identifier, ElementResponse response) {
        TextAnimator animator = null;
        if (item.getPlaceholder() != null) {
            animator = getAnimatorManager().getTextAnimator(identifier, item.getPlaceholder().getRawText());
        }
        this.backMenuButton = new BackMenuButton(slot, item, identifier, false, animator, response);
    }

    public Page addPreviousMenu(Page previousMenu) {
        if (!previousMenus.contains(previousMenu))
            this.previousMenus.add(previousMenu);
        return this;
    }

    public List<Page> getPreviousMenus() {
        return previousMenus;
    }

    public List<Page> getNextMenus() {
        return nextMenus;
    }

    public Page addNextMenu(Page nextMenu) {
        if (!nextMenus.contains(nextMenu))
            this.nextMenus.add(nextMenu);
        return this;
    }

    public boolean isWiped() {
        return isWiped;
    }

    private void updateItemTitle(HumanEntity player, int slot, String newTitle) {
        getGui().setItem(slot, itemStorage.get(slot).getPageItem().setName(newTitle).update());
        ((Player) player).updateInventory();
    }

    public boolean isWipeOnlySelf() {
        return wipeOnlySelf;
    }

    private int[] getButtonSlots() {
        if (buttonSlots == null) {
            int[] temp = new int[]{0, 8};
            switch (getGui().getRows()) {
                case FOUR:
                    temp[0] = 27;
                    temp[1] = 35;
                    break;
                case THREE:
                case FIVE:
                    temp[0] = 18;
                    temp[1] = 26;
                    break;
                case SIX:
                    temp[0] = 45;
                    temp[1] = 53;
                    break;
            }
            return temp;
        } else {
            return buttonSlots;
        }
    }

    public Page setButtonSlots(int[] buttonSlots) {
        this.buttonSlots = buttonSlots;
        return this;
    }

    public Page setNextPage(Page page) {
        this.nextPage = page;
        String tempName = plugin.getLangFile().get("gui-buttons.next-page.name");
        this.nextButton = new ItemBuilder(XMaterial.LIME_WOOL)
                .setName(tempName)
                .setLore(plugin.getLangFile().get("gui-buttons.next-page.description").replace("{page-number}", String.valueOf(page.getPageNumber())))
                .hideTags();
        return this;
    }

    public Page setPreviousPage(Page page) {
        this.previousPage = page;
        String tempName = plugin.getLangFile().get("gui-buttons.previous-page.name");
        this.backButton = new ItemBuilder(XMaterial.RED_WOOL)
                .setName(tempName)
                .setLore(plugin.getLangFile().get("gui-buttons.previous-page.description").replace("{page-number}", String.valueOf(page.getPageNumber())))
                .hideTags();
        return this;
    }

    public void setItemOnXY(int x, int y, ItemBuilder item, String identifier, ElementResponse response) {
        setItemOnXY(x, y, item, identifier, null, response);
    }

    public void setItemOnXY(int x, int y, ItemBuilder item, String identifier, TextAnimator animator, ElementResponse response) {
        for (int lin = 0; lin < getBukkitInventory().getSize() / 9; lin++) {
            int start = 9 * lin;
            int end = start + 8;
            int count = start;
            while (count <= end) {
                for (int col = 0; col < 9; col++) {
                    if (lin == y - 1 && col == x - 1) {
                        if (animator == null) {
                            pageSetItem(count, item, identifier, response);
                        } else {
                            pageSetItem(count, item, identifier, animator, response);
                        }
                        return;
                    }
                    count++;
                }
            }
        }
    }

    public BkPlugin getPlugin() {
        return plugin;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public Map<Integer, PageItem> getItems() {
        return itemStorage;
    }

    public void pageSetItem(int slot, ItemBuilder item, String identifier, ElementResponse response) {
        TextAnimator animator = null;
        if (item.getPlaceholder() != null) {
            animator = getAnimatorManager().getTextAnimator(identifier, item.getPlaceholder().getRawText());
        }
        pageSetItem(slot, item, identifier, animator, response);
    }

    public void pageSetHead(int slot, OfflinePlayer player, String displayName, List<String> lore, String identifier, ElementResponse response) {
        HeadBuilder headBuilder = new HeadBuilder(player, displayName, lore);

        TextAnimator animator = null;
        if (headBuilder.getBuilder().getPlaceholder() != null) {
            animator = getAnimatorManager().getTextAnimator(identifier, headBuilder.getBuilder().getPlaceholder().getRawText());
        }
        pageSetHead(slot, headBuilder, identifier, animator, response);
    }

    public void pageSetItem(int slot, ItemBuilder item, String identifier, TextAnimator animator, ElementResponse response) {
        boolean isUpdating = false;
        if (itemStorage.size() > 0) {
            PageItem pageItem = itemStorage.get(slot);
            if (pageItem != null) {
                isUpdating = true;
                PageUtils.destroyAnimator(this, pageItem);
            }
        }

        itemStorage.put(slot, new PageItem(item, identifier, false, animator, response));
        setItem(slot, item, response);
        if (isUpdating && animator != null && getGui().getInventory().getViewers().size() > 0) {
            for (HumanEntity entity : getGui().getInventory().getViewers()) {
                startAnimator(entity, slot);
            }
        }
    }

    public void pageSetHead(int slot, HeadBuilder item, String identifier, TextAnimator animator, ElementResponse response) {
        if (itemStorage.size() > 0) {
            PageItem pageItem = itemStorage.get(slot);
            if (pageItem != null) {
                PageUtils.destroyAnimator(this, pageItem);
            }
        }

        itemStorage.put(slot, new PageItem(item, identifier, false, null, response));
        setHead(slot, item, response);
        /*if (animator != null*//* && isBuilt()*//*) {
            if (getGui().getInventory().getViewers().size() > 0) {
                for (HumanEntity entity : getGui().getInventory().getViewers()) {
                    startAnimator(entity, slot);
                }
            }
        }*/
    }

    public boolean isSwitchingPages() {
        return switchingPages;
    }

    public void setSwitchingPages(boolean switchingPages) {
        this.switchingPages = switchingPages;
    }

    public void removeItem(int slot) {
        itemStorage.remove(slot);
        removeItem(slot);
    }

    private ItemStack getDisplayHead(String headSkin, String displayName, List<String> newLore) {
        ItemStack head = plugin.getHeadManager().getCustomTextureHead(headSkin);
        ItemMeta tempMeta = head.getItemMeta();

        tempMeta.setDisplayName(displayName);
        tempMeta.setLore(newLore);

        head.setItemMeta(tempMeta);

        return head;
    }

    public void displayItemMessage(ItemMessage itemMessage) {
        setUnclickable(itemMessage.getSlot(), itemMessage.getDisplayItem());
        Page callbackPage = this;

        displayMessages.add(itemMessage);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (findViewer() != null) {
                    clearDisplayMessage(itemMessage, callbackPage);
                }
            }
        }.runTaskLater(plugin, (long) (20 * itemMessage.getSeconds()));
    }

    public ConcurrentLinkedQueue<ItemMessage> getDisplayMessages() {
        return displayMessages;
    }

    public void clearDisplayMessage(ItemMessage itemMessage, Page callbackPage) {
        displayMessages.remove(itemMessage);
        unsetUnclickable(itemMessage.getSlot());
        if (itemMessage.getMessageRunnable() != null)
            Bukkit.getScheduler().runTask(plugin, () -> itemMessage.getMessageRunnable().run(callbackPage));
    }

    public HumanEntity findViewer() {
        if (getGui().getInventory().getViewers().size() > 0) return getGui().getInventory().getViewers().get(0);
        else {
            if (getPreviousPage() != null) {
                HumanEntity prevViewer = findInPreviousPages(getPreviousPage());
                if (prevViewer != null) return prevViewer;
            }
            if (getNextPage() != null) {
                HumanEntity nextViewer = findInNextPages(getNextPage());
                if (nextViewer != null) return nextViewer;
            }
        }
        return null;
    }

    private HumanEntity findInPreviousPages(Page page) {
        if (page.getGui().getInventory().getViewers().size() > 0)
            return page.getGui().getInventory().getViewers().get(0);
        else {
            if (page.getPreviousPage() != null) return findInPreviousPages(page.getPreviousPage());
        }
        return null;
    }

    private HumanEntity findInNextPages(Page page) {
        if (page.getGui().getInventory().getViewers().size() > 0)
            return page.getGui().getInventory().getViewers().get(0);
        else {
            if (page.getNextPage() != null) return findInNextPages(page.getNextPage());
        }
        return null;
    }

    public void displayItemMessage(int slot, double seconds, ChatColor iconColor, String displayName, List<String> lore, MessageRunnable messageRunnable) {
        String headSkin = getSkinString(iconColor);
        ItemMessage itemMessage = new ItemMessage(getDisplayHead(headSkin, displayName, lore), seconds, slot);
        if (messageRunnable != null) itemMessage.setMessageRunnable(messageRunnable);
        displayItemMessage(itemMessage);
    }

    public void displayItemMessage(int slot, double seconds, ChatColor titleColor, List<String> lore, MessageRunnable messageRunnable) {
        PageItem pageItem = itemStorage.get(slot);
        String headSkin = getSkinString(titleColor);

        if (pageItem != null) {
            String displayName;
            if (pageItem.getPageItem().getPlaceholder() != null) {
                displayName = pageItem.getPageItem().getPlaceholder().getCleanText();
            } else {
                ItemMeta meta = pageItem.getPageItem().getItem().getItemMeta();
                if (meta != null && meta.getDisplayName() != null)
                    displayName = ChatColor.stripColor(meta.getDisplayName());
                else displayName = pageItem.getPageItem().getItem().getType().toString();
            }
            displayName = titleColor + "" + ChatColor.COLOR_CHAR + "l" + displayName;
            ItemMessage itemMessage = new ItemMessage(getDisplayHead(headSkin, displayName, lore), seconds, slot);
            if (messageRunnable != null) itemMessage.setMessageRunnable(messageRunnable);
            displayItemMessage(itemMessage);
        } else {
            ItemMessage itemMessage = new ItemMessage(getDisplayHead(headSkin, ChatColor.RED + "Error!", lore), seconds, slot);
            if (messageRunnable != null) itemMessage.setMessageRunnable(messageRunnable);
            displayItemMessage(itemMessage);
        }
    }

    private String getSkinString(ChatColor titleColor) {
        String headSkin = "ewogICJ0aW1lc3RhbXAiIDogMTYyNjI4NDAyNjQ5OSwKICAicHJvZmlsZUlkIiA6ICIwZmIzZDQ5NTM5ZDI0OWM2OGZlZWY5YTUwYjJkMjNiMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJCa3JfXyIsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80ODkzNTg0Y2RkNjg1YzQ2NTVhODhkN2Q2M2U5YTcwOGVhODRmOTE5ZTk4NzI0YTk0ZGYwN2QwZTllNjFmMDQiCiAgICB9CiAgfQp9";
        switch (titleColor) {
            case GREEN:
            case DARK_GREEN:
                headSkin = "ewogICJ0aW1lc3RhbXAiIDogMTYyNjI4Mzg5NjI0NiwKICAicHJvZmlsZUlkIiA6ICIwZmIzZDQ5NTM5ZDI0OWM2OGZlZWY5YTUwYjJkMjNiMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJCa3JfXyIsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80Nzk1MWMwY2I1MWM5ZmFiMjE4N2U3NmI4OGM2NzhiYzgzNTcyOWYwYTYzNDExOGI3YjMwY2RiOTAwNDA4OTQ1IgogICAgfQogIH0KfQ==";
                break;
            case BLUE:
            case AQUA:
            case DARK_BLUE:
            case DARK_AQUA:
                headSkin = "ewogICJ0aW1lc3RhbXAiIDogMTYyNjI4Mzg3MDIyNSwKICAicHJvZmlsZUlkIiA6ICIwZmIzZDQ5NTM5ZDI0OWM2OGZlZWY5YTUwYjJkMjNiMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJCa3JfXyIsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83YzRmNjkxZDg3NDBjMGE0OTY1NjcwY2VlNTUxYzIyM2FkM2IzODNjMmQwZGFiYjU2OGNkNmFlMmQ5OGMzYjU3IgogICAgfQogIH0KfQ==";
                break;
            case YELLOW:
            case GOLD:
                headSkin = "ewogICJ0aW1lc3RhbXAiIDogMTYyNjI4Mzg1MTYzMSwKICAicHJvZmlsZUlkIiA6ICIwZmIzZDQ5NTM5ZDI0OWM2OGZlZWY5YTUwYjJkMjNiMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJCa3JfXyIsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83MjFjNzI4ODFjM2FmZTdkZDNhNDkzOTUxOWEyNDQ0NDJjMjRkZTg3ODkwZDcwYTRlZjRkYjcwNGRlYjg3ZDE4IgogICAgfQogIH0KfQ==";
                break;
        }
        return headSkin;
    }

    public void displayItemMessage(int slot, double seconds, ChatColor titleColor, String lore, MessageRunnable messageRunnable) {
        displayItemMessage(slot, seconds, titleColor, Collections.singletonList(lore), messageRunnable);
    }

    public static void clearUnclickable(ArrayList<Page> pages) {
        for (Page page : pages) {
            page.clearUnclickable(false);
        }
    }

    public void setUnclickable(int slot, boolean clearOthers, String displayName, List<String> newLore) {
        if (clearOthers) clearUnclickable(true);

        ItemStack barrier = XMaterial.BARRIER.parseItem();
        ItemMeta tempMeta = barrier.getItemMeta();

        PageItem item = itemStorage.get(slot);

        List<String> tempL = (item.getPageItem() == null ? item.getHead().getLore() : item.getPageItem().getItem().getItemMeta().getLore());
        List<String> oldLore = new ArrayList<>(tempL);
        if (!newLore.isEmpty()) {
            oldLore.addAll(newLore);
        }
        tempMeta.setLore(oldLore);

        tempMeta.setDisplayName(displayName);

        barrier.setItemMeta(tempMeta);

        PageUtils.destroyAnimator(this, itemStorage.get(slot));

        itemStorage.get(slot).setUnclickable(true);
        getGui().setItem(slot, new ItemBuilder(barrier));
    }

    public void setUnclickable(int slot, ItemStack displayItem) {
        PageUtils.destroyAnimator(this, itemStorage.get(slot));

        itemStorage.get(slot).setUnclickable(true);
        getGui().setItem(slot, new ItemBuilder(displayItem));
    }

    public static ItemBuilder buildButton(Material material, String displayName, List<String> newLore) {
        return new ItemBuilder(Utils.createItem(material, true, displayName, newLore));
    }

    public Page getNextPage() {
        return nextPage;
    }

    public Page getPreviousPage() {
        return previousPage;
    }

    public void clearUnclickable(boolean onlyFirst) {
        for (int keySlot : itemStorage.keySet()) {
            if (itemStorage.get(keySlot).isUnclickable()) {
                itemStorage.get(keySlot).setUnclickable(false);
                unsetUnclickable(keySlot);
                if (onlyFirst) break;
            }
        }
    }

    public void unsetUnclickable(int slot) {
        TextAnimator titleAnimator = itemStorage.get(slot).getTitleAnimator();
        if (titleAnimator != null) {
            pageSetItem(slot, itemStorage.get(slot).getPageItem().setName(itemStorage.get(slot).getPageItem().getPlaceholder().getRawText()), itemStorage.get(slot).getIdentifier(), itemStorage.get(slot).getPageItemResponse());
            /*HumanEntity viewer = findViewer();
            if (viewer != null) {
                startAnimator(viewer, slot);
            }*/
        } else {
            if (itemStorage.get(slot).getPageItem() == null) {
                pageSetHead(slot, itemStorage.get(slot).getHead(), itemStorage.get(slot).getIdentifier(), null, itemStorage.get(slot).getPageItemResponse());
            } else {
                pageSetItem(slot, itemStorage.get(slot).getPageItem(), itemStorage.get(slot).getIdentifier(), itemStorage.get(slot).getPageItemResponse());
            }
        }
    }

    public void startAnimator(HumanEntity player, int slot) {
        TextAnimator animator = itemStorage.get(slot).getTitleAnimator();
        if (animator != null) {
            if (animator.getAnimationTask() == null && animator.getReceiver() == null) {
                animator.setReceiver(animationFrame -> {
                    try {
                        AnimationPlaceholder placeholder = itemStorage.get(slot).getPageItem().getPlaceholder();
                        if (placeholder != null) {
                            String optionText = placeholder.getOptions().getText();
                            if (optionText != null && !optionText.isEmpty()) {
                                if (player != null)
                                    updateItemTitle(player, slot, placeholder.getRawText().replaceAll("\\{([^}]*)}", animationFrame));
                            } else {
                                if (player != null)
                                    updateItemTitle(player, slot, animationFrame);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            animator.animate();
        }
    }

    public Page setWiped(boolean wiped) {
        isWiped = wiped;
        return this;
    }

    public Map<Integer, PageItem> getItemStorage() {
        return itemStorage;
    }

    public boolean isUnregisterOnClose() {
        return unregisterOnClose;
    }

    public Page setUnregisterOnClose(boolean unregisterOnClose) {
        this.unregisterOnClose = unregisterOnClose;
        return this;
    }

    public Page setWipeOnlySelf(boolean wipeOnlySelf) {
        this.wipeOnlySelf = wipeOnlySelf;
        return this;
    }

    @Override
    public String toString() {
        return getGui().getTitle() + ChatColor.RESET + "-@" + hashCode() + "-id" + getGui().getID();
    }

    @Override
    public int compareTo(Page page) {
        if (page.getGui().getID() == this.getGui().getID()) return 0;
        else return 1;
    }
}
