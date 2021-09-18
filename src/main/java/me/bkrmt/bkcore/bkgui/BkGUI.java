package me.bkrmt.bkcore.bkgui;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.bkgui.page.Page;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public enum BkGUI {
    INSTANCE;

    private BkPlugin instance;

    private final Map<Integer, Page> pages = new HashMap<>();

    public Map<Integer, Page> getPages() {
        return pages;
    }

    public void register(BkPlugin instance) {
        this.instance = instance;
        Bukkit.getPluginManager().registerEvents(new InteractionListener(), instance);
//        debug();
    }

    private void debug() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (pages.size() > 0) {
                    instance.getLogger().log(Level.INFO, "++++++++++++++++++++");
                    for (Page page : pages.values()) {
                        instance.getLogger().log(Level.INFO, page.toString());
                    }
                    instance.getLogger().log(Level.INFO, "++++++++++++++++++++");
                } else {
                    instance.getLogger().log(Level.INFO, "No pages");
                }
            }
        }.runTaskTimerAsynchronously(instance, 0, 20);
    }

    public BkPlugin getInstance() {
        return instance;
    }

}
