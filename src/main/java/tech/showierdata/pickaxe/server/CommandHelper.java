package tech.showierdata.pickaxe.server;

public class CommandHelper {
	String lastSentCommand = "";
	private static CommandHelper instance;

	private CommandHelper() {
		//call sendLocate() every 5 seconds

	}

	public static CommandHelper getInstance() {
		if (instance == null) {
			instance = new CommandHelper();
		}
		return instance;
	}


	public void clearLastSentCommand() {
		lastSentCommand = "";
	}


}
