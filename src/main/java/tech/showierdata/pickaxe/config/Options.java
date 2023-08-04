package tech.showierdata.pickaxe.config;


public class Options {
	public boolean enabled = true;
	public XPBarEnum XPBarType = XPBarEnum.Radiation;
	public boolean AutoCL = false;
	public boolean ShowLockIcon = false;

	public ItemConfig itemconfig;
	static Options INSTANCE;

	public Options() { // all default vals
		this.itemconfig = new ItemConfig();
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

}
