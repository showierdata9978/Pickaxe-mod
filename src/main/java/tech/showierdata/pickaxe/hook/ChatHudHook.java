package tech.showierdata.pickaxe.hook;

import java.util.ListIterator;

import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.Text;
import tech.showierdata.pickaxe.Pickaxe;
import tech.showierdata.pickaxe.server.Regexs;

public class ChatHudHook {
    private final IChatHudHook chatHud;

    // Defaults as null bc no previous message is known
    private Text prevMessage = null;
    private int count = 1;

    public ChatHudHook(IChatHudHook chatHud) {
        this.chatHud = chatHud;
    }

    public Text compactChatMessage(Text message) {
        // Timestamps are removed to compare texts (otherwise none would match)
        Text withoutTimestamps = Regexs.removeTimestamps(message);

        Text prevMessage = this.prevMessage;
        this.prevMessage = withoutTimestamps;

        // Return if this is new message.
        if (!withoutTimestamps.equals(prevMessage)) {
            this.count = 1;
            return message;
        }

        removeMessage(message);

        this.count++;
        return message.copy().append(String.format(" §8[§bx%s§8]", count));
    }

    // Remove all previous forms of the message
    public void removeMessage(Text originalMessage) {
        ListIterator<ChatHudLine> iterator = this.chatHud.getMessages().listIterator();
        while (iterator.hasNext()) {
            ChatHudLine chatHudLine = iterator.next();

            // Remove previously stacked versions too
            Text contentWithoutOccurrences = Regexs.removeStackMods(chatHudLine.content());
            Text textWithoutOccurrences = Regexs.removeStackMods(originalMessage);

            if (contentWithoutOccurrences.equals(textWithoutOccurrences)) {
                iterator.remove();
                this.chatHud.refreshMessages();

                return;
            }
        }
    }

    // Clears the previous message
    public void clear() {
        this.prevMessage = null;
        this.count = 1;
    }
}
