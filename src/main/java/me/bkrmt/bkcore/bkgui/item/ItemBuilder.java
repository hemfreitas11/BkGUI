package me.bkrmt.bkcore.bkgui.item;

import me.bkrmt.bkcore.textanimator.AnimationPlaceholder;
import me.bkrmt.bkcore.textanimator.AnimatorManager;
import me.bkrmt.bkcore.bkgui.ColorUtil;
import me.bkrmt.bkcore.xlibs.XMaterial;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ItemBuilder {
    private ItemStack item;
    private ItemMeta meta;
    private XMaterial type;
    private AnimationPlaceholder placeholder;

    public ItemBuilder(XMaterial material, int amount) {
        setItem(material, amount);
    }

    public ItemBuilder(XMaterial material) {
        setItem(material, 1);
    }

    public ItemBuilder() {
        setItem(XMaterial.DIRT, 1);
    }

    public ItemBuilder(ItemStack itemStack) {
        setItem(itemStack);
    }

    public ItemBuilder setItem(ItemStack itemStack) {
        placeholder = null;
        item = itemStack;
        type = XMaterial.matchXMaterial(itemStack);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null && itemMeta.getDisplayName() != null && AnimatorManager.isAnimation(itemMeta.getDisplayName())) {
            placeholder = AnimatorManager.deserializePlaceholder(itemMeta.getDisplayName());
            if (placeholder != null) {
                itemMeta.setDisplayName(placeholder.getColoredCleanText());
                meta = itemMeta;
            } else meta = item.getItemMeta();
        } else meta = item.getItemMeta();
        return this;
    }

    public ItemBuilder setItem(XMaterial material, int amount) {
        placeholder = null;
        item = material.parseItem();
        type = material;
        meta = item.getItemMeta();
        return this;
    }

    public XMaterial getType() {
        return type;
    }

    public ItemBuilder setName(String name) {
        if (AnimatorManager.isAnimation(name)) {
            AnimationPlaceholder placeholder = AnimatorManager.deserializePlaceholder(name);
            if (placeholder != null) {
                this.placeholder = placeholder;
                name = placeholder.getColoredCleanText();
            }
        }
        meta.setDisplayName(ColorUtil.fixColor(name));
        update();
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        meta.setLore(ColorUtil.fixColor(lore));
        update();
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        meta.setLore(Arrays.asList(ColorUtil.fixColor(lore)));
        update();
        return this;
    }

    public ItemBuilder setEnchantments(List<ItemEnchantment> enchantments) {
        for (ItemEnchantment enchantment : enchantments)
            meta.addEnchant(enchantment.getEnchantment(), enchantment.getLevel(), enchantment.isUnsafe());
        update();
        return this;
    }

    public ItemBuilder hideTags() {
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        update();
        return this;
    }

    public ItemBuilder setEnchantments(Map<Enchantment, Integer> enchantments) {
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet())
            meta.addEnchant(entry.getKey(), entry.getValue(), true);
        update();
        return this;
    }

    public ItemBuilder update() {
        item.setItemMeta(meta);
        return this;
    }

    public ItemStack getItem() {
        return item;
    }

    public ItemBuilder setMeta(ItemMeta meta) {
        if (meta != null && AnimatorManager.isAnimation(meta.getDisplayName())) {
            placeholder = AnimatorManager.deserializePlaceholder(meta.getDisplayName());
            meta.setDisplayName(placeholder.getColoredCleanText());
        }
        this.meta = meta;
        return this;
    }

    public ItemMeta getMeta() {
        return meta;
    }

    public AnimationPlaceholder getPlaceholder() {
        return placeholder;
    }
}
