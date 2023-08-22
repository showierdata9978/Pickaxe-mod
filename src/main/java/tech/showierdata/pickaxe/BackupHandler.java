package tech.showierdata.pickaxe;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.inventory.Inventory;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

public class BackupHandler {
	final MinecraftClient client;
	final ArrayList<Inventory> stack = new ArrayList<>();

	public BackupHandler(MinecraftClient client) {
		this.client = client;
	}


	@SuppressWarnings({"ResultOfMethodCallIgnored", "ReassignedVariable"})
	public void handleInventory(Inventory inventory) {
		stack.add(inventory);
		if (stack.size() > 10) {
			stack.remove(0);
		}

		//take a screenshot of the inventory

		Date date = Date.from(Instant.now());
		File directory = new File("./pickaxe/backups/" + date.getTime() + "/");

		if (!directory.exists()) {

			directory.mkdirs();
		}

		ScreenshotRecorder recorder = null;

		try {

			recorder = new ScreenshotRecorder(directory, client.getWindow().getWidth(), client.getWindow().getHeight(), client.getWindow().getWidth());

			recorder.finish();
		} catch (Exception e) {
			Pickaxe.LOGGER.error("Failed to create backup screenshot", e);
			return;
		} finally {
			//remove the last one
			if (!stack.isEmpty()) stack.remove(stack.size() - 1);

			if (recorder != null) {
				try {
					recorder.finish();
				} catch (Exception e) {
					Pickaxe.LOGGER.error("Failed to save backup screenshot (again)", e);
				}
			}
		}


		//save screenshot to /pickaxe/backups/<name>.png
		//get current date and time
		//


		//make sure the directory exists
		
		/* 
		try {
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
		} catch (IOException e) {
			Pickaxe.LOGGER.error("Failed to create backup screenshot", e);
			return;
		}



		try {
			screenshot.writeTo(file);
		} catch (IOException e) {
			Pickaxe.LOGGER.error("Failed to save backup screenshot", e);
		}*/

		//remove the last one
		if (!stack.isEmpty()) stack.remove(stack.size() - 1);
	}
}
