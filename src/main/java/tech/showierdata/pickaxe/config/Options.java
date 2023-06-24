package tech.showierdata.pickaxe.config;

public class Options {
	public boolean enabled = true;
	public XPBarEnum XPBarType = XPBarEnum.Radiation;
	static Options INSTANCE;

	public Options() { // all default vals
		INSTANCE = this;
	}

	public static Options getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Options();
		}
		return INSTANCE;
	}

	public static void setInstance(Options instance) {
		INSTANCE = instance;
	}

	public Options(boolean enabled) {
		this.enabled = enabled;
		INSTANCE = this;
	}
}
