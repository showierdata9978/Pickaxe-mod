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
public enum BracketEnum {
    Square("Square"),
    Curly("Curly"),
    Angled("Angled"),
    Round("Round"),
    None("None"),
	Custom("Custom");

    final String name;

    BracketEnum(String name) {
        this.name = name;

		//this.MessageStackPattern = this.getMessageStackPattern();
    }
    
	/**
	 * 
	 * @param inside The inner content
	 * @param regexSafe If you want it to be regex safe
	 * @return The current Border String
	 */
    public String getBorderString(Object inside, boolean regexSafe) {
        String prefix = "";
		String sufix = "";
		String res = (regexSafe)? "§8\\Q%s\\E§bx%s§8\\Q%s\\E" : "§8%s§bx%s§8%s";
		switch (this) {
			case Curly:
				prefix = "{";
				sufix = "}";
				break;
			case Angled:
				prefix = "<";
				sufix = ">";
				break;
			case Round:
				prefix = "(";
				sufix = ")";
				break;
			case Square:
				prefix = "[";
				sufix = "]";
				break;
			case None:
				return String.format("§bx%s§8", inside);
			/*case Custom:
				break;
				// Color codes :)
				assert Options.getInstance() != null;
				assert Options.getInstance().messageStackPrefix != null;
				assert Options.getInstance().messageStackSufix != null;
				prefix = Options.getInstance().messageStackPrefix
					.replaceAll("&([a-f,j-n,r,x,0-9])", "§$1");
				sufix = Options.getInstance().messageStackSufix
					.replaceAll("&([a-f,j-n,r,x,0-9])", "§$1");
				if (regexSafe) {
					// Seems unlikely, but possible
					prefix.replaceAll("\\\\(Q|E)", "\\\\$1")
						.replaceAll("\\\\", "\\\\");
					sufix.replaceAll("\\\\(Q|E)", "\\\\$1")
						.replaceAll("\\\\", "\\\\");
				}*/
		}
        return String.format(res, prefix, inside, sufix);
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
		//Pickaxe.LOGGER.info("I did a thing: " + res);
		return Pattern.compile(res);
	}
	
	//public Pattern MessageStackPattern;

	public boolean hasBeenStacked(String message) {
		Matcher messageMatcher = getMessageStackPattern().matcher(message);//MessageStackPattern.matcher(message);

		return messageMatcher.find();
	}

	public Text removeStackMods(Text modifiedText) {
		MutableText res = Regexs.removeTimestamps(modifiedText).copy();
        res.getSiblings().removeIf((text) -> { return hasBeenStacked(text.getString()); });
        return res;
    }
}
