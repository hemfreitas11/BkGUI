package me.bkrmt.bkcore.bkgui.gui;

import me.bkrmt.bkcore.Utils;
import me.bkrmt.bkcore.bkgui.ColorUtil;
import me.bkrmt.bkcore.bkgui.BkGUI;
import me.bkrmt.bkcore.bkgui.item.HeadBuilder;
import me.bkrmt.bkcore.bkgui.item.ItemBuilder;
import me.bkrmt.bkcore.xlibs.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class GUI {
    private final int id;
    private final UUID uniqueId = UUID.randomUUID();
    private Inventory inventory;
    private final Rows rows;
    private final String title;
    private InventoryHolder holder;

    public GUI(InventoryHolder holder, String title) {
        this.rows = Rows.SIX;
        this.holder = holder;
        this.title = title;
        this.inventory = createInventory(holder, Rows.SIX, title);
        this.id = generateID();
    }

    public GUI(InventoryHolder holder, String title, Rows rows) {
        this.rows = rows;
        this.holder = holder;
        this.title = title;
        this.inventory = createInventory(holder, rows, title);
        this.id = generateID();
    }

    public GUI(String title, Rows rows) {
        this.rows = rows;
        this.title = title;
        this.inventory = createInventory(
                new GUIHolder(this, Bukkit.createInventory(null,
                        rows.getSlots())),
                rows,
                title);
        this.id = generateID();
    }

    public GUI(String title) {
        this.rows = Rows.SIX;
        this.title = title;
        this.inventory = createInventory(
                new GUIHolder(this, Bukkit.createInventory(null, Rows.SIX.getSlots())),
                Rows.SIX,
                title);
        this.id = generateID();
    }

    public int getID() {
        return id;
    }

    private int generateID() {
        int returnId = Utils.getRandomInRange(1, 9999999);

        while (BkGUI.INSTANCE.getPages().containsKey(returnId)) {
            returnId = Utils.getRandomInRange(1, 9999999);
        }
        return returnId;
    }

    public void updateItem(int slot, String dislayName, List<String> lore) {
        ItemStack item = getInventory().getItem(slot);
        ItemMeta meta = item.getItemMeta();
        if (dislayName != null) meta.setDisplayName(dislayName);
        if (lore != null) meta.setLore(lore);
        item.setItemMeta(meta);
        getInventory().setItem(slot, item);
    }

    public void openInventory(Player player) {
        player.openInventory(this.inventory);
    }

    public int addItem(ItemBuilder itemBuilder) {
        try {
            this.inventory.addItem(itemBuilder.getItem());
        } catch (Exception ignored) {
        }
        return getPosition(itemBuilder.getItem().getType(), itemBuilder.getItem().getDurability());
    }

    private int getPosition(Material material, int data) {
        try {
            for (int i = 0; i < this.inventory.getContents().length; i++) {
                ItemStack itemStack = this.inventory.getItem(i);

                if (itemStack.getType().equals(material)
                        && itemStack.getDurability() == data)
                    return i;
            }
        } catch (Exception ignored) {
        }
        return -1;
    }

    public void setItem(int slot, ItemBuilder itemBuilder) {
//        if (this.inventory.getItem(slot) == null) this.inventory.setItem(slot, new ItemStack(Material.AIR));
        this.inventory.setItem(slot, itemBuilder.getItem());
        /*Bukkit.getScheduler().runTaskLaterAsynchronously(OpenGUI.INSTANCE.getInstance(), () -> {
            try {
                ItemStack item = itemBuilder.getItem();
                if (this.inventory != null) this.inventory.setItem(slot, item);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }, 0);*/
    }

    public void setHead(int slot, HeadBuilder headBuilder) {
        ItemStack item = headBuilder.getHead(slot, this);
        if (this.inventory != null) this.inventory.setItem(slot, new ItemBuilder(item).hideTags().getItem());
    }

/*    public void setHead(int slot, ItemBuilder builder) {
        BkPlugin instance = OpenGUI.INSTANCE.getInstance();
        if (this.inventory.getItem(slot) == null)
            this.inventory.setItem(slot, instance.getCustomTextureHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDVjNmRjMmJiZjUxYzM2Y2ZjNzcxNDU4NWE2YTU2ODNlZjJiMTRkNDdkOGZmNzE0NjU0YTg5M2Y1ZGE2MjIifX19"));
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            SkullMeta meta = (SkullMeta) builder.getItem().getItemMeta();
            OfflinePlayer owner = meta.getOwningPlayer();
            if (owner != null) {
                System.out.println(owner.getUniqueId().toString());
                try {
                    UUID.fromString(owner.getName());
                } catch (Exception e) {
                    String texture;
                    String signature;
                    URL url;
                    try {
                        url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + owner.getUniqueId().toString().replaceAll("-", "") +
                                "?unsigned=false");
                    } catch (MalformedURLException ex) {
                        ex.printStackTrace();
                        return;
                    }

                    try {
                        InputStreamReader reader = new InputStreamReader(url.openStream());
                        System.out.println(reader);
                        JsonObject json = new JsonParser().parse(reader).getAsJsonObject().get("properties")
                                .getAsJsonArray().get(0).getAsJsonObject();
                        texture = json.get("value").getAsString();
                        signature = json.get("signature").getAsString();
                    } catch (IOException exc) {
                        return;
                    }
                    if (texture != null && signature != null) {
                        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                        profile.getProperties().put("textures", new Property("textures", texture, signature));
                        Field field;
                        try {
                            field = meta.getClass().getDeclaredField("profile");
                            field.setAccessible(true);
                            field.set(meta, profile);
                            builder.setMeta(meta).update();
                            if (this.inventory != null) this.inventory.setItem(slot, builder.getItem());
                        } catch (NoSuchFieldException | IllegalAccessException exce) {
                            exce.printStackTrace();
                        }
                    }
                }
            }
        });
    }*/

    public void removeItem(int slot) {
        this.inventory.setItem(slot, XMaterial.AIR.parseItem());
    }

    public static Inventory createInventory(InventoryHolder holder, Rows rows, String title) {
        return Bukkit.createInventory(holder,
                rows.getSlots(),
                ColorUtil.fixColor(title));
    }

    public static Inventory createInventory(InventoryHolder holder, int size, String title) {
        return Bukkit.createInventory(holder,
                size,
                ColorUtil.fixColor(title));
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public String toString() {
        return uniqueId.toString();
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Rows getRows() {
        return rows;
    }

    public String getTitle() {
        return title;
    }

    public InventoryHolder getHolder() {
        return holder;
    }
}
