package me.bkrmt.bkcore.bkgui;

import me.bkrmt.bkcore.bkgui.gui.GUIElement;
import me.bkrmt.bkcore.bkgui.page.Page;
import me.bkrmt.bkcore.bkgui.page.PageItem;
import me.bkrmt.bkcore.bkgui.page.PageUtils;
import me.bkrmt.bkcore.bkgui.page.itemmessage.ItemMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

public class InteractionListener implements Listener {
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Page page = getInteractedPage(event.getInventory());
        if (page != null) {
            Player player = (Player) event.getPlayer();

            for (ItemMessage message : page.getDisplayMessages()) {
                page.clearDisplayMessage(message, page);
                page.getDisplayMessages().remove(message);

            }
            for (int key : page.getItemStorage().keySet()) {
                page.startAnimator(player, key);
            }
            if (page.getWindowResponse() != null) page.getWindowResponse().onOpen(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        Page page = getInteractedPage(event.getView().getTopInventory());
        if (page != null) {
            if (event.getView().getTopInventory().equals(page.getBukkitInventory())) {
                if (page.isUnregisterOnClose()) {
                    if (page.isWipeOnlySelf()) PageUtils.wipePage(page);
                    else PageUtils.wipeLinkedPages(page);
                    Bukkit.getScheduler().runTaskLater(BkGUI.INSTANCE.getInstance(), () ->
                            ((Player) event.getPlayer()).updateInventory(), 5);
                } else {
                    if (page.isSwitchingPages()) {
                        page.pauseAnimators();
                        page.setSwitchingPages(false);
                    } else {
                        if (page.getGuiSettings().getPageWipeCloseResponse() != null) page.getGuiSettings().getPageWipeCloseResponse().event(event);
                        if (page.isWipeOnlySelf()) PageUtils.wipePage(page);
                        else PageUtils.wipeLinkedPages(page);
                        Bukkit.getScheduler().runTaskLater(BkGUI.INSTANCE.getInstance(), () ->
                                ((Player) event.getPlayer()).updateInventory(), 5);
                    }
                }

                if (page.getWindowResponse() != null) page.getWindowResponse().onClose(event);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Page page = getInteractedPage(event.getView().getTopInventory());
        if (page != null) {
            if (!page.getGuiSettings().isCanDrag()) {
                event.setCancelled(true);
                return;
            }
            if (page.getGuiSettings().isCanDrag() &&
                    canEnter(page, event.getCursor())) {
                event.setCancelled(false);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Page page = getInteractedPage(event.getView().getTopInventory());
        if (page != null) {
            if (event.getView() == null
                    || event.getView().getTopInventory() == null
                    || event.getView().getBottomInventory() == null
                    || event.getClickedInventory() == null)
                return;

            if (page.getGuiSettings().isCanEnterItems()) {
                if (!event.isShiftClick()) {
                    if (event.getClickedInventory().equals(page.getBukkitInventory())
                            && event.getCursor() != null
                            && !event.getCursor().getType().equals(Material.AIR)
                            && canEnter(page, event.getCursor())) {
                        event.setCancelled(true);
                        for (GUIElement element : page.getElements().values()) {
                            if (event.getSlot() == element.getSlot()) return;
                        }
                        if (page.getGuiSettings().getEnteredItemResponse() != null)
                            page.getGuiSettings().getEnteredItemResponse().event(event);
                    } else if (event.getClickedInventory().equals(page.getBukkitInventory())
                            && event.getCursor() != null
                            && !canEnter(page, event.getCursor())) {
                        if (page.getGuiSettings().getNotEnterableItemResponse() != null)
                            page.getGuiSettings().getNotEnterableItemResponse().event(event);
                        event.setCancelled(true);
                    }
                } else {
                    if (!event.getClickedInventory().equals(page.getBukkitInventory())
                            && event.getCurrentItem() != null
                            && !event.getCurrentItem().getType().equals(Material.AIR)
                            && canEnter(page, event.getCurrentItem())) {
                        event.setCancelled(true);
                        for (GUIElement element : page.getElements().values()) {
                            if (event.getSlot() == element.getSlot()) return;
                        }
                        if (page.getGuiSettings().getEnteredItemResponse() != null)
                            page.getGuiSettings().getEnteredItemResponse().event(event);
                    } else if (!event.getClickedInventory().equals(page.getBukkitInventory())
                            && event.getCurrentItem() != null
                            && !canEnter(page, event.getCurrentItem())) {
                        if (page.getGuiSettings().getNotEnterableItemResponse() != null)
                            page.getGuiSettings().getNotEnterableItemResponse().event(event);
                        event.setCancelled(true);
                    }
                }
            }

            if (event.isShiftClick() &&
                    !event.getClickedInventory().equals(page.getBukkitInventory())) {
                event.setCancelled(true);
                return;
            } else if (!event.isShiftClick() &&
                    event.getClickedInventory().equals(page.getBukkitInventory())
                    && (event.getCursor() == null || event.getCursor().getType().equals(Material.AIR)) &&
                    (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR))) {
                event.setCancelled(true);
                if (page.getGuiSettings().getEmptyClickResponse() != null) {
                    page.getGuiSettings().getEmptyClickResponse().event(event);
                }
                return;
            } else if (!event.isShiftClick() &&
                    event.getClickedInventory().equals(page.getBukkitInventory())
                    && (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR))) {
                event.setCancelled(true);
                return;
            } else if (!event.isShiftClick() &&
                    event.getClickedInventory().equals(page.getBukkitInventory())) {
                checkElements(page, event, page.getItemStorage().get(event.getSlot()));
                return;
            } else if (event.isShiftClick() &&
                    event.getClickedInventory().equals(page.getBukkitInventory())) {
                checkElements(page, event, page.getItemStorage().get(event.getSlot()));
                return;
            }
            event.setCancelled(false);
            return;
        }
    }

    protected boolean canEnter(Page page, ItemStack itemStack) {
        if (page.getGuiSettings().isCanEnterItems()) {
            List<ItemStack> materials = page.getGuiSettings().getEnterableItems();

            if (materials.isEmpty())
                return true;

            if (itemStack == null || itemStack.getType().equals(Material.AIR))
                return true;

            for (ItemStack entry : materials) {
                Material material = entry.getType();
                short data = entry.getDurability();

                if (itemStack.getType().equals(material)
                        && itemStack.getDurability() == data)
                    return true;
            }
        }
        return false;
    }

    protected void checkElements(Page page, InventoryClickEvent event, PageItem item) {
        for (GUIElement element : page.getElements().values()) {
            int slot = element.getSlot();

            if (slot != event.getSlot())
                continue;
            if (!event.getClickedInventory().equals(page.getBukkitInventory()))
                continue;
            if (!event.getView().getTopInventory().equals(page.getBukkitInventory()))
                continue;

            event.setCancelled(!element.isPullable());
            if (element.getElementResponse() != null) {
                if (item != null && !item.isUnclickable())
                    element.getElementResponse().onClick(event);
            } else if (element.getGuiExtenderItem() != null) {
                if (item != null && !item.isUnclickable())
                    element.getGuiExtenderItem().onClick(event);
            }
        }

    }

    private Page getInteractedPage(Inventory inventory) {
        Page returnValue = null;
        Collection<Page> pages = BkGUI.INSTANCE.getPages().values();
        if (pages.size() > 0) {
            for (Page page : pages) {
                if (inventory.equals(page.getBukkitInventory())) {
                    returnValue = page;
                    break;
                }
            }
        }
        return returnValue;
    }
}
