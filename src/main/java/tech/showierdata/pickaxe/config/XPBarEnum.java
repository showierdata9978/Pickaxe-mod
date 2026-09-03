package tech.showierdata.pickaxe.config;

import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.world.BossEvent;

public enum XPBarEnum {
	Radiation("Radiation"),
	Depth("Depth"),
	Suit_Charge("Suit Charge"),
	O2("O2");


	final String name;

	XPBarEnum(String name) {
		this.name = name;
	}

	public boolean detect(BossEvent bar) {
		boolean detected = false;
		String[] split = bar.getName().getString().split(" ");

		// Breaks it
        switch (this) {
			case Radiation:
				if (!(split.length > 1)) {
					break;
				}
				detected = split[1].equals("Radiation:");
				break;
			case Depth:
				if (!(split.length > 0)) {
					break;
				}
				detected = split[0].equals("Depth:");
				break;
			case Suit_Charge:
				if (!(split.length > 1))
				{
					break;
				}

				detected = (
						split[0].equals("Suit") &&
								split[1].equals("Charge:")
				);
				break;
			case O2:
				detected = split[1].startsWith("O");
				break;

		}
		return detected;
	}

	public boolean detect(LerpingBossEvent bar) {
		return detect((BossEvent)bar);
	}

	public int getBarDetails(BossEvent bar) {
		String[] splits = bar.getName().getString().split(" ");
		String last = "";
		int index = splits.length - 1;
		if (this == O2) {
			index--;
			if (splits[index].equals("EMPTY")) {
				return 0;
			} 
		}
		while (last.isEmpty() && index >= 0) {
			last = splits[index].replaceAll("[^0-9].*$", "")
				.replaceAll("[A-Z,a-z:\\s]", "");
			index--;
		}
		if (index < 0) return 0;
		return Integer.parseInt(last);
	}

}
