package me.bkrmt.bkcore.bkgui;

import me.bkrmt.bkcore.xlibs.XSound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum MenuSound {
    ERROR(XSound.BLOCK_NOTE_BLOCK_PLING, 0.4F, 0.1F),
    CLICK(XSound.UI_BUTTON_CLICK, 0.4F, 1.0F),
    WARN(XSound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.4F, 0.8F),
    BACK(XSound.UI_BUTTON_CLICK, 0.4F, 0.1F),
    FOWARD(XSound.UI_BUTTON_CLICK, 0.4F, 0.8F),
    SPECIAL(null, 0.3F, 1F),
    SUCCESS(XSound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.4F, 0.9F);

    private final float volume;
    private final float pitch;
    private final XSound sound;

    MenuSound(XSound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public void play(HumanEntity humanEntity) {
        if (humanEntity != null) {
            executeSound((Player) humanEntity);
        }
    }

    public void play(Player player) {
        if (player != null) {
            executeSound(player);
        }
    }

    private void executeSound(Player player) {
        if (this.equals(MenuSound.SPECIAL) ) {
            getSpecial().play(player, volume, pitch);
        } else {
            sound.play(player, volume, pitch);
        }
    }
    public XSound getSpecial() {
        List<XSound> soundList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            soundList.add(XSound.ENTITY_VILLAGER_TRADE);
        }
        for (int i = 0; i < 7; i++) {
            soundList.add(XSound.ENTITY_DONKEY_AMBIENT);
        }
        soundList.add(XSound.ENTITY_BAT_AMBIENT);
        soundList.add(XSound.BLOCK_CHEST_CLOSE);
        soundList.add(XSound.BLOCK_CHEST_CLOSE);
        soundList.add(XSound.BLOCK_CHEST_CLOSE);
        soundList.add(XSound.BLOCK_CHEST_OPEN);
        soundList.add(XSound.BLOCK_CHEST_OPEN);
        soundList.add(XSound.BLOCK_CHEST_OPEN);
        soundList.add(XSound.BLOCK_CHEST_OPEN);
        soundList.add(XSound.ENTITY_ENDER_DRAGON_GROWL);
        soundList.add(XSound.ENTITY_WOLF_GROWL);
        soundList.add(XSound.ENTITY_WOLF_GROWL);
        soundList.add(XSound.ENTITY_BLAZE_AMBIENT);
        soundList.add(XSound.ENTITY_PLAYER_BURP);
        soundList.add(XSound.ENTITY_PLAYER_BURP);
        soundList.add(XSound.ENTITY_PLAYER_BURP);
        soundList.add(XSound.ENTITY_CREEPER_PRIMED);
        soundList.add(XSound.ENTITY_CREEPER_PRIMED);
        soundList.add(XSound.ENTITY_CREEPER_PRIMED);
        soundList.add(XSound.ENTITY_ENDER_DRAGON_AMBIENT);
        soundList.add(XSound.ENTITY_CAT_AMBIENT);
        soundList.add(XSound.ENTITY_WITHER_SPAWN);
        soundList.add(XSound.ENTITY_WITHER_SPAWN);
        soundList.add(XSound.ENTITY_SHEEP_SHEAR);
        soundList.add(XSound.ENTITY_SHEEP_SHEAR);
        Collections.shuffle(soundList);
        return soundList.get((int) (Math.random() * soundList.size()));
    }

}