package tech.showierdata.pickaxe.commands;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import tech.showierdata.pickaxe.Pickaxe;
import tech.showierdata.pickaxe.PickaxeCommand;

import java.util.ArrayList;
import java.util.List;

public class HelpCommandController implements  PickaxeCommandController
{
    public void use(String command, List<String> args) {
        MinecraftClient client = MinecraftClient.getInstance();


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
        client.player.sendMessage(Text.literal(
                "-- Help --\n" + String.join("\n", s)
        ));
    }
}
