package tech.showierdata.pickaxe.config;

import net.minecraft.client.MinecraftClient;

public class MDTConfig {

	public static final long MOON_TIME = 13480L;
	public static final int MOON_WINDOW = 35;
	public static final int MOON_TICK_SPEED = 28;

    public CCTLocation location = CCTLocation.TOPRIGHT;

    public boolean soundEnabled = false;
    public boolean reverseCCTorder = false;

    public boolean enabled = true;

    /**
     * Gets the time of day (in seconds) of the Minecraft world.
     * @return {@link Int} Time of day of the Minecraft world
     */
	public int getTimeOfDay() {
		MinecraftClient client = MinecraftClient.getInstance();
		return (int)(client.world.getTimeOfDay() % 24000L);
	}

    /**
     * Gets the time until the Mood Door in seconds.
     * @return {@link Int} Time until the Moon Door Opens
     * @see getTimeOfDay
     */
	public int getMoonDoorTime() {
		MinecraftClient client = MinecraftClient.getInstance();
		int ctime = (int)((MOON_TIME - MOON_WINDOW - client.world.getTimeOfDay()) % 24000L);
		if (ctime < -MOON_WINDOW) ctime += 24000; // You can get negative numbers, which is not useful >:(
		return Math.floorDiv(ctime, MOON_TICK_SPEED); // For some reason it ticks by 28 per second;
	}
}
