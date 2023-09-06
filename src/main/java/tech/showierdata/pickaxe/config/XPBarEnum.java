package tech.showierdata.pickaxe.config;

import net.minecraft.entity.boss.BossBar;
import net.minecraft.client.gui.hud.ClientBossBar;

public enum XPBarEnum {
	Radiation("Radiation"),
	Depth("Depth"),
	Suit_Charge("Suit Charge"),
	O2("O2");


	final String name;

	XPBarEnum(String name) {
		this.name = name;
	}

	public boolean detect(BossBar bar) {
		boolean detected = false;
		String[] split = bar.getName().getString().split(" ");

		// Breaks it
		//noinspection EnhancedSwitchMigration
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

		}
		return detected;
	}

	public boolean detect(ClientBossBar bar) {
		boolean detected = false;
		String[] split = bar.getName().getString().split(" ");

		// Breaks it
		//noinspection EnhancedSwitchMigration
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

		}
		return detected;
	}

}
