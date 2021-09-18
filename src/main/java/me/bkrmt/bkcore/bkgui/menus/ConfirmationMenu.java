package me.bkrmt.bkcore.bkgui.menus;

import me.bkrmt.bkcore.BkPlugin;
import me.bkrmt.bkcore.bkgui.BkGUI;
import me.bkrmt.bkcore.bkgui.gui.GUI;
import me.bkrmt.bkcore.bkgui.gui.Rows;
import me.bkrmt.bkcore.bkgui.page.Page;
import me.bkrmt.bkcore.bkgui.page.PageItem;

public class ConfirmationMenu {
    BkPlugin plugin;
    Page menu;

    public ConfirmationMenu(String title, PageItem confirmInfo, PageItem confirmButton, PageItem declineButton) {
        plugin = BkGUI.INSTANCE.getInstance();

        menu = new Page(getPlugin(), getPlugin().getAnimatorManager(), new GUI(title, Rows.SIX), 1);

        for (int i = 2; i < 5; i++) {
            for (int c = 2; c < 5; c++) {
                menu.setItemOnXY(c, i, confirmButton.getPageItem(), confirmButton.getIdentifier() + "-" + c + "-" + i, confirmButton.getPageItemResponse());
            }
        }
        for (int i = 2; i < 5; i++) {
            for (int c = 6; c < 9; c++) {
                menu.setItemOnXY(c, i, declineButton.getPageItem(), declineButton.getIdentifier() + "-" + c + "-" + i, declineButton.getPageItemResponse());
            }
        }

        menu.setItemOnXY(5, 6, confirmInfo.getPageItem(), confirmInfo.getIdentifier(), confirmInfo.getPageItemResponse());
        menu.setUnregisterOnClose(true);
    }

    public Page getMenu() {
        return menu;
    }

    public BkPlugin getPlugin() {
        return plugin;
    }
}
