package tech.showierdata.pickaxe.server;

import net.minecraft.client.MinecraftClient;

import java.util.Objects;

public class CommandHelper {
	String lastSentCommand = "";
	private static CommandHelper instance;

	private CommandHelper() {
		//call sendLocate() every 5 seconds

		/* 
		new Thread(() -> {
			while (true) {
				try {

					Thread.sleep(5000);
					if (p[0] == null) {
						p[0] = Pickaxe.getInstance();
					}
					if (!p[0].enabled) {
						continue;
					}
					if (mc.world == null) {
						continue;
    				}
    				if (mc.isInSingleplayer()) {
        				continue;
    				}
    				if (!mc.getCurrentServerEntry().address.endsWith("mcdiamondfire.com")) {
			        	continue;
   	 				}

					
					sendLocate();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();*/
	}

	public static CommandHelper getInstance() {
		if (instance == null) {
			instance = new CommandHelper();
		}
		return instance;
	}

	public String getLastSentCommand() {
		return lastSentCommand;
	}

	public void clearLastSentCommand() {
		lastSentCommand = "";
	}

	public void sendLocate() {
		lastSentCommand = "locate";

		// create a message callback
		// this will be called when the server sends a message
		
		// to the client

		Objects.requireNonNull(MinecraftClient.getInstance().player).networkHandler.sendChatCommand("locate");

	}

}
