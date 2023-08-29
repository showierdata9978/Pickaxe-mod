package tech.showierdata.pickaxe.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import tech.showierdata.pickaxe.Pickaxe;
import tech.showierdata.pickaxe.server.Regexs;

/*
 * This name sucks :I
 * I wanted to keep a descriptive name, but it is really long
 */
public enum MessageStackingBorderEnum {
    Square("Square"),
    Curly("Curly"),
    Angled("Angled"),
    Round("Round"),
    None("None");

    final String name;

    MessageStackingBorderEnum(String name) {
        this.name = name;

		this.MessageStackPattern = getMessageStackPattern();
    }
    
    public String getBorderString(Object inside, boolean regexSafe) {
        String brackets = "";
		switch (this) {
			case Curly:
				brackets = "{}";
				break;
			case Angled:
				brackets = "<>";
				break;
			case Round:
				brackets = "()";
				break;
			case Square:
				brackets = "[]";
				break;
			case None:
				return String.format("§bx%s§8", inside);
		}
        String res = String.format("§8%s§bx%s§8%s", brackets.charAt(0), inside, brackets.charAt(1));
        if (regexSafe) {
            res = res.replaceAll("([\\[\\(\\{\\]\\)\\}])", "\\\\$1");
        }
        return res;
    }

    public String getBorderString(Object inside) {
        return getBorderString(inside, false);
    }

    
    /*
     * These cannot be static as settings can change
     * Moved from Regexs
     */

	public Pattern getMessageStackPattern() {
		String res = String.format(
			this.getBorderString("\\d+", true)
		) + "$";
		Pickaxe.LOGGER.info("I did a thing: " + res);
		return Pattern.compile(res);
	}

	
	public Pattern MessageStackPattern;

	public boolean hasBeenStacked(String message) {
		Matcher messageMatcher = MessageStackPattern.matcher(message);

		return messageMatcher.find();
	}

	public Text removeStackMods(Text modifiedText) {
		MutableText res = Regexs.removeTimestamps(modifiedText).copy();
        res.getSiblings().removeIf((text) -> { return hasBeenStacked(text.getString()); });
        return res;
    }
}
