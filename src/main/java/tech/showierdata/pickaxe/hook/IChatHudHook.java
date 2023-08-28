package tech.showierdata.pickaxe.hook;

import net.minecraft.client.gui.hud.ChatHudLine;

import java.util.List;

public interface IChatHudHook {
    List<ChatHudLine> getMessages();
    void refreshMessages();
    void clear();
}
