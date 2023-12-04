package tech.showierdata.pickaxe.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import tech.showierdata.pickaxe.server.Regexs;

public class MsgStackConfig {
	public boolean enabled = true;

	public String text = "&8[&bx{num}&8]";

    
    /**
     * These cannot be static as settings can change
     * Moved from Regexs
     */

	/**
	 * Returns the current tag for message stacking
	 * @param inside The inner content
	 * @param regexSafe If you want it to be regex safe
	 * @return The current Border String
	 */
    public String getBorderString(Object inside) {
        String res = this.text
			.replaceAll("&([a-f,j-n,r,x,0-9])", "§$1")
			.replaceAll("\\{num\\}", "%s");
		return String.format(res, inside);
    }

	/**
	 * @see java.util.regex.Pattern
	 * @return Pattern of current message stacking tag
	 */
	public Pattern getMessageStackPattern() {
		return Pattern.compile("\\Q" + String.format(
			this.getBorderString("\\E\\d+\\Q")
		) + "\\E$");
	}

	/**
	 * @param message Content of message from {@link Text#getString()}
	 * @return {@link Boolean} If the message has been stacked
	 */
	public boolean hasBeenStacked(String message) {
		Matcher messageMatcher = getMessageStackPattern().matcher(message);

		return messageMatcher.find();
	}

	/**
	 * 
	 * @param modifiedText {@link Text}
	 * @return {@link Text} with all {@link MutableText#getSiblings()} matching {@link BracketEnum#hasBeenStacked()} removed
	 * @see #hasBeenStacked(String)
	 */
	public Text removeStackMods(Text modifiedText) {
		MutableText res = Regexs.removeTimestamps(modifiedText).copy();
        res.getSiblings().removeIf(text -> hasBeenStacked(text.getString()));
        return res;
    }
}
