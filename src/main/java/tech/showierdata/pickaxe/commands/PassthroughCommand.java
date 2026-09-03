package tech.showierdata.pickaxe.commands;

import tech.showierdata.pickaxe.Pickaxe;

import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

public class PassthroughCommand implements PickaxeCommandController {
    public void use(String command, List<String> args) {
        if (!Pickaxe.getInstance().isInPickaxe()) return;

        Minecraft client = Minecraft.getInstance();

        ClientPacketListener net = Objects.requireNonNull(client.getConnection());

        net.sendChat("@" + command + " " + String.join(" ", args));

    }
}
