package me.bkrmt.bkcore.bkgui.page;

import me.bkrmt.bkcore.bkgui.BkGUI;
import me.bkrmt.bkcore.textanimator.TextAnimator;

import java.util.List;

public class PageUtils {
    public static void wipePage(Page page) {
        if (!page.isWiped()) {
            for (PageItem pageItem : page.getItemStorage().values()) {
                destroyAnimator(page, pageItem);
            }
            BackMenuButton button = page.getBackMenuButton();
            if (button != null) destroyAnimator(page, button);

            page.setWiped(true);
            BkGUI.INSTANCE.getPages().remove(page.getGui().getID());
        }
    }

    public static void wipePreviousPages(Page page) {
        Page previousPage = page.getPreviousPage();
        List<Page> previousMenus = page.getPreviousMenus();
        if (!previousMenus.isEmpty()) {
            for (Page previousMenu : previousMenus) {
                if (!previousMenu.isWiped()) {
                    wipeLinkedPages(previousMenu);

                }
            }
        }
        if (previousPage != null && !previousPage.isWiped()) wipePreviousPages(previousPage);
        wipePage(page);
    }

    public static void wipeNextPages(Page page) {
        Page nextPage = page.getNextPage();
        List<Page> nextMenus = page.getNextMenus();
        if (!nextMenus.isEmpty()) {
            for (Page nextMenu : nextMenus) {
                if (!nextMenu.isWiped())
                    wipeLinkedPages(nextMenu);
            }
        }
        if (nextPage != null && !nextPage.isWiped()) wipeNextPages(nextPage);
        wipePage(page);
    }

    public static void wipeLinkedPages(Page page) {
        PageUtils.wipePreviousPages(page);
        PageUtils.wipeNextPages(page);
    }

    public static void destroyAnimator(Page page, PageItem pageItem) {
        TextAnimator animator = pageItem.getTitleAnimator();
        if (animator != null) page.getAnimatorManager().destroy(animator);

    }

}
