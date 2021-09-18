package me.bkrmt.bkcore.bkgui.gui;

import me.bkrmt.bkcore.bkgui.event.ElementResponse;
import me.bkrmt.bkcore.bkgui.event.WindowResponse;
import me.bkrmt.bkcore.bkgui.item.FinalItemJob;
import me.bkrmt.bkcore.bkgui.item.HeadBuilder;
import me.bkrmt.bkcore.bkgui.item.ItemBuilder;
import me.bkrmt.bkcore.xlibs.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public abstract class GUIExtender {
    private final List<FinalItemJob> jobs = new ArrayList<>();
    private final Hashtable<Integer, GUIElement> elements = new Hashtable<>();
    protected final GUISettings guiSettings;
    protected GUI gui;
    protected WindowResponse windowResponse;

    public GUIExtender(GUI gui) {
        this.gui = gui;

        this.guiSettings = new GUISettings();
        this.guiSettings.setCanEnterItems(false);
        this.guiSettings.setCanDrag(false);
    }

    public void setGUI(GUI gui) {
        this.gui = gui;
        this.elements.clear();
        this.jobs.clear();
    }

    public void addEmptyElementResponse(int slot, boolean pullable) {
        GUIElement guiElement = new GUIElement(slot, pullable);
        elements.put(slot, guiElement);
    }

    public void addElementResponse(int slot, ElementResponse elementResponse) {
        GUIElement guiElement = new GUIElement(slot);
        guiElement.addElementResponse(slot, elementResponse);
        elements.put(slot, guiElement);
    }

    public void addElementResponse(int slot, boolean pullable, ElementResponse elementResponse) {
        GUIElement guiElement = new GUIElement(slot);
        guiElement.addElementResponse(slot, pullable, elementResponse);
        elements.put(slot, guiElement);
    }

    public void addElementResponse(int slot, GUIExtenderItem guiExtenderItem) {
        GUIElement guiElement = new GUIElement(slot);
        guiElement.addElementResponse(slot, guiExtenderItem.isPullable(), guiExtenderItem);
        elements.put(slot, guiElement);
    }

    private void addEmptyElementResponse(int slot) {
        GUIElement guiElement = new GUIElement(slot);
        elements.put(slot, guiElement);
    }

    public void addWindowResponse(WindowResponse windowResponse) {
        this.windowResponse = windowResponse;
    }

    public void changeItem(int slot, ItemBuilder itemBuilder) {
        gui.setItem(slot, itemBuilder);
        updateInventory();
    }

    public void setItem(int slot, ItemBuilder itemBuilder) {
        gui.setItem(slot, itemBuilder);
        addEmptyElementResponse(slot);
        updateInventory();
    }

    public void setItem(int slot, ItemBuilder itemBuilder, ElementResponse elementResponse) {
        gui.setItem(slot, itemBuilder);
        addElementResponse(slot, elementResponse);
        updateInventory();
    }

    public void setHead(int slot, HeadBuilder headBuilder, ElementResponse elementResponse) {
        gui.setHead(slot, headBuilder);
        addElementResponse(slot, elementResponse);
        updateInventory();
    }

    public void setItem(int slot, GUIExtenderItem guiExtenderItem) {
        jobs.add(new FinalItemJob(slot, guiExtenderItem));
    }

    public int addItem(ItemBuilder itemBuilder) {
        int index = gui.addItem(itemBuilder);
        addEmptyElementResponse(index);
        updateInventory();
        return index;
    }

    public int addItem(ItemBuilder itemBuilder, ElementResponse elementResponse) {
        int index = gui.addItem(itemBuilder);
        addElementResponse(index, elementResponse);
        updateInventory();
        return index;
    }

    public void addItem(GUIExtenderItem guiExtenderItem) {
        jobs.add(new FinalItemJob(guiExtenderItem));
    }

    public void removeItem(int slot) {
        gui.removeItem(slot);
    }

    public void openInventory(Player player) {
        if (!jobs.isEmpty()) {
            for (FinalItemJob finalItemJob : jobs) {
                if (finalItemJob.getSlot() == -1) {
                    addExtenderItem(finalItemJob.getGuiExtenderItem(),
                            player);
                } else setExtenderItem(finalItemJob.getSlot(),
                        finalItemJob.getGuiExtenderItem(),
                        player);
            }
            jobs.clear();
        }

        player.openInventory(getBukkitInventory());
    }

    public Inventory getBukkitInventory() {
        return gui.getInventory();
    }

    public void updateInventory() {
        List<Integer> slots = new ArrayList<>();
        int temp = 0;
        for (ItemStack itemStack : getBukkitInventory().getContents()) {
            temp++;
            if (itemStack == null || itemStack.getType().equals(XMaterial.AIR.parseMaterial()))
                continue;

            int current = temp - 1;
            slots.add(current);
        }

        for (int slot : elements.keySet())
            if (slots.contains(slot))
                slots.remove((Integer) slot);

        for (int slot : slots)
            addEmptyElementResponse(slot);

        getBukkitInventory().getViewers().forEach(viewer -> ((Player) viewer).updateInventory());
    }

    private void setExtenderItem(int slot, GUIExtenderItem guiExtenderItem, Player player) {
        gui.setItem(slot, guiExtenderItem.getItemBuilder(player));
        addElementResponse(slot, guiExtenderItem);
        updateInventory();
    }

    private void addExtenderItem(GUIExtenderItem guiExtenderItem, Player player) {
        int index = gui.addItem(guiExtenderItem.getItemBuilder(player));
        addElementResponse(index, guiExtenderItem);
        updateInventory();
    }

    public List<FinalItemJob> getJobs() {
        return jobs;
    }

    public Map<Integer, GUIElement> getElements() {
        return elements;
    }

    public GUISettings getGuiSettings() {
        return guiSettings;
    }

    public GUI getGui() {
        return gui;
    }

    public WindowResponse getWindowResponse() {
        return windowResponse;
    }
}
