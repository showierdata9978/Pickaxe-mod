package tech.showierdata.pickaxe.config;

import net.minecraft.client.MinecraftClient;

public class MDTConfig {

    /** The Door opens at 1380 in the daylight cycle */
	public static final long MOON_TIME = 13480L;

    /** There is a 35-second window for getting to the door */
	public static final int MOON_WINDOW = 35;

    /** For some reason it ticks by 28 every second */
	public static final int MOON_TICK_SPEED = 28;

    public TimerLocation location = TimerLocation.TOPLEFT;

    /** Is sound enabled? */
    public boolean soundEnabled = false;

    /** 
     * Setting for showierdata9978
     * <p>Allows you to switch which timer is on top
     */

    public boolean enabled = true;


    /**
     * Gets the time until the Mood Door in seconds.
     * @return (int) Time (in seconds) until the Moon Door Open
	*/
	public int getMoonDoorTime() {
		MinecraftClient client = MinecraftClient.getInstance();
        assert client.world != null;
        int ctime = (int)((MOON_TIME - client.world.getTimeOfDay()) % 24000L);
		if (ctime < -MOON_TICK_SPEED) ctime += 24000; // You can get negative numbers, which is not useful >:(
		return Math.floorDiv(ctime, MOON_TICK_SPEED);
	}
}
