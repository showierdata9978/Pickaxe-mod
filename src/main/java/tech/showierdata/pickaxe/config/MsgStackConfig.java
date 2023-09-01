package tech.showierdata.pickaxe.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import tech.showierdata.pickaxe.server.Regexs;

public class MsgStackConfig {
	public boolean enabled = true;

	public BracketEnum border = BracketEnum.Square;
	public String prefix = "";
	public String suffix = "";

    public boolean hasX = true;
    public ColorsEnum color = ColorsEnum.Azure;

	public String getBorderString(Object inside) {
        return getBorderString(inside, false);
    }

    
    /*
     * These cannot be static as settings can change
     * Moved from Regexs
     */

	/**
	 * @see java.util.regex.Pattern
	 * @return Pattern of current message stacking tag
	 */
	public Pattern getMessageStackPattern() {
		return Pattern.compile(String.format(
			this.getBorderString("\\d+", true)
		) + "$");
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
	 * @see BracketEnum#hasBeenStacked(String)
	 */
	public Text removeStackMods(Text modifiedText) {
		MutableText res = Regexs.removeTimestamps(modifiedText).copy();
        res.getSiblings().removeIf((text) -> { return hasBeenStacked(text.getString()); });
        return res;
    }

	/**
	 * Returns the current tag for message stacking
	 * @param inside The inner content
	 * @param regexSafe If you want it to be regex safe
	 * @return The current Border String
	 */
    public String getBorderString(Object inside, boolean regexSafe) {
        String _prefix = "";
		String _sufix = "";
		String res = (regexSafe)? "§8\\Q%s\\E%s%s%s§8\\Q%s\\E" : "§8%s§b%s%s§8%s";
		switch (this.border) {
			case Curly:
				_prefix = "{";
				_sufix = "}";
				break;
			case Angled:
				_prefix = "<";
				_sufix = ">";
				break;
			case Round:
				_prefix = "(";
				_sufix = ")";
				break;
			case Square:
				_prefix = "[";
				_sufix = "]";
				break;
			case None:
				return String.format("§bx%s§8", inside);
			case Custom:
				// Color codes :)
				_prefix = this.prefix
					.replaceAll("&([a-f,j-n,r,x,0-9])", "§$1");
				_sufix = this.suffix
					.replaceAll("&([a-f,j-n,r,x,0-9])", "§$1");
				if (regexSafe) {
					// Seems unlikely, but possible
					_prefix.replaceAll("\\\\(Q|E)", "\\\\$1")
						.replaceAll("\\\\", "\\\\");
					_sufix.replaceAll("\\\\(Q|E)", "\\\\$1")
						.replaceAll("\\\\", "\\\\");
				}
				break;
		}
		String color = this.color.name
			.replaceAll("^(..).*", "$1");
        return String.format(res, _prefix, color, (Options.getInstance().msgStackConfig.hasX)? "x" : "", inside, _sufix);
    }
}
