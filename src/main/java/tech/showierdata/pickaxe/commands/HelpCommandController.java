package tech.showierdata.pickaxe.commands;

import tech.showierdata.pickaxe.Pickaxe;
import tech.showierdata.pickaxe.PickaxeCommand;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class HelpCommandController implements  PickaxeCommandController
{
    public void use(String command, List<String> args) {
        Minecraft client = Minecraft.getInstance();


        ArrayList<String> s = new ArrayList<>();
        for (PickaxeCommand c : Pickaxe.getInstance().commands) {
            s.add(
                    "@" + c.name +
                            " " +
                            String.join(" ", c.arguments) +
                            "\n    " +
                            c.data
            );
        }
        assert client.player != null;
        client.player.sendSystemMessage(Component.literal(
                "-- Help --\n" + String.join("\n", s)
        ));
    }
}
