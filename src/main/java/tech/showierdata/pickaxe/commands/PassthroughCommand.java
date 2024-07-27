package tech.showierdata.pickaxe.commands;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import tech.showierdata.pickaxe.Pickaxe;

import java.util.List;
import java.util.Objects;

public class PassthroughCommand implements PickaxeCommandController {
    public void use(String command, List<String> args) {
        if (!Pickaxe.getInstance().isInPickaxe()) return;

        MinecraftClient client = MinecraftClient.getInstance();

        ClientPlayNetworkHandler net = Objects.requireNonNull(client.getNetworkHandler());

        net.sendChatMessage("@" + command + " " + String.join(" ", args));

    }
}
