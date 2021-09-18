package me.bkrmt.bkcore.bkgui.item;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.heads.HeadManager;
import me.bkrmt.bkcore.bkgui.BkGUI;
import me.bkrmt.bkcore.bkgui.gui.GUI;
import me.bkrmt.bkcore.xlibs.XMaterial;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class HeadBuilder {
    ItemBuilder builder;
    OfflinePlayer owner;
    String displayName;
    List<String> lore;

    public HeadBuilder(OfflinePlayer owner, String displayName, List<String> lore) {
        this.displayName = displayName;
        this.lore = lore;
        this.owner = owner;
        this.builder = new ItemBuilder()
            .setName(displayName)
            .setLore(lore);
    }

    public ItemBuilder getBuilder() {
        return builder;
    }

    public OfflinePlayer getOwner() {
        return owner;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public ItemStack getHead(int slot, GUI gui) {
        BkPlugin instance = BkGUI.INSTANCE.getInstance();
        ItemStack head = instance.getHeadManager().getPlayerHead(owner, head1 -> {
            if (head1 != null) {
                ItemStack inventoryHead = gui.getInventory().getItem(slot);
                if (inventoryHead != null && inventoryHead.getType().equals(XMaterial.PLAYER_HEAD.parseMaterial())) {
                    SkullMeta headMeta = (SkullMeta) inventoryHead.getItemMeta();
                    HeadManager.applyGameProfile(head1.getTexture(), headMeta);
                    inventoryHead.setItemMeta(headMeta);
                    gui.getInventory().setItem(slot, inventoryHead);
                }
            }
        });

        ItemMeta meta = head.getItemMeta();
        ItemMeta builderMeta = getBuilder().getItem().getItemMeta();

        meta.setDisplayName(builderMeta.getDisplayName());
        meta.setLore(builderMeta.getLore());

        head.setItemMeta(meta);
        return head;
    }
}
