package tech.showierdata.pickaxe.server;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import tech.showierdata.pickaxe.Pickaxe;
import tech.showierdata.pickaxe.config.Options;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Regexs {
	
	public static Pattern getPlotOwnerPattern() {
		/*String donorRanksRegex = String.join("|", Arrays.stream(DonorRank.values())
            .map(DonorRank::toString)
            .toArray(String[]::new));
		
		return Pattern.compile(String.format("Owner: (?:\\[%s\\])(\\w+)", donorRanksRegex));*/
		return Pattern.compile("Owner: (?:\\[.*\\])?(\\w+)");
	}

	public static Pattern getPlotNamePattern() {
		//return Pattern.compile("You are currently (?:playing on|at): \\n\\n. (.*) (\\[(\\d+)\\])");
		return Pattern.compile(". (.*) (\\[(\\d+)\\])");
	}

	public static Pattern getServerPattern() {
		return Pattern.compile("Server: (Node \\d)");
	}

	public static Pattern getPlotAdPattern() {
		return Pattern.compile(".*\\n\\s*(.*) by ([\\w\\d]*)");
	}

	public static final Pattern PlotOwnerPattern = getPlotOwnerPattern();
	public static final Pattern PlotNamePattern = getPlotNamePattern();
	public static final Pattern ServerPattern = getServerPattern();
	public static final Pattern PlotAdPattern = getPlotAdPattern();

	public static boolean isLocateCommand(String message) {
		Matcher plotOwnerMatcher = PlotOwnerPattern.matcher(message);
		Matcher plotNameMatcher = PlotNamePattern.matcher(message);
		Matcher serverMatcher = ServerPattern.matcher(message);

		return plotOwnerMatcher.find() && plotNameMatcher.find() && serverMatcher.find();
	}

	public static boolean isPlotAd(String message) {
		Matcher plotAdMatcher = PlotAdPattern.matcher(message);

		return plotAdMatcher.find();
	}

	public static Ad getAdDetails(String message) {
		/*
		 * Returns a the plot in the ad.
		 * server and id are null.
		 */
		Matcher plotAdMatcher = PlotAdPattern.matcher(message);

		if (plotAdMatcher.find()) {
			String name = plotAdMatcher.group(1);
			String owner = plotAdMatcher.group(2);
			//String desc = plotAdMatcher.group(3);

			return new Ad(new Plot(owner, name, null, null));
		}

		return null;
	}


	public static Plot getLocateDetails(String message) {
		Matcher plotOwnerMatcher = PlotOwnerPattern.matcher(message);
		Matcher plotNameMatcher = PlotNamePattern.matcher(message);
		Matcher serverMatcher = ServerPattern.matcher(message);

		if (plotOwnerMatcher.find() && plotNameMatcher.find() && serverMatcher.find()) {
			String owner = plotOwnerMatcher.group(1);
			String name = plotNameMatcher.group(1);
			String id = plotNameMatcher.group(2);
			String server = serverMatcher.group(1);

			return new Plot(owner, name, server, id);
		}

		return null;
	}

	public static Text removeTimestamps(Text text) {

		String string = text.getString();
        String withoutTimestamps = string.replaceAll(".?\\d{1,2}:\\d{2}(:\\d{2})*.?", "");

		// No changes? No action
        if (withoutTimestamps.equals(string)) return text;

		/*
		 * Create new Text that contains new text
		 * - Set text
		 * - Copy style
		 * - Copy Changes (Siblings)
		 */
        MutableText newText = Text.literal(withoutTimestamps.trim());
        newText.setStyle(newText.getStyle());
        newText.getSiblings().addAll(text.getSiblings());

        return newText;
	}
}
