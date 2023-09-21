package tech.showierdata.pickaxe.config;

import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

public class MDTConfig {

    /** The Door opens at 1380 in the daylight cycle */
	public static final long MOON_TIME = 13480L;

    /** There is a 35 second window for getting to the door */
	public static final int MOON_WINDOW = 35;

    /** For some reason it ticks by 28 every second */
	public static final int MOON_TICK_SPEED = 28;

    public TimerLocation location = TimerLocation.TOPRIGHT;

    public boolean soundEnabled = false;
    public boolean reverseCCTorder = false;

    public boolean enabled = true;

    /**
     * Gets the time of day (in seconds) of the Minecraft world.
     * @return (int) Time of day of the Minecraft world
     */
	public int getTimeOfDay() {
		MinecraftClient client = MinecraftClient.getInstance();
		return (int)(client.world.getTimeOfDay() % 24000L);
	}

    /**
     * Gets the time until the Mood Door in seconds.
     * @return (int) Time (in seconds) until the Moon Door Opens
     * @see getTimeOfDay
     */
	public int getMoonDoorTime() {
		MinecraftClient client = MinecraftClient.getInstance();
		int ctime = (int)((MOON_TIME - client.world.getTimeOfDay()) % 24000L);
		if (ctime < 0) {
            ctime += 24000; // You can get negative numbers, which is not useful >:(
			mdtReadySounded = false;
			mdtNowSounded = false;
        }
		return Math.floorDiv(ctime, MOON_TICK_SPEED);
	}

    private boolean mdtReadySounded = false; // To prevent it from creating a ton of timers and crashing the game
	private boolean mdtNowSounded = false;
    public void prepSounds() {
        MinecraftClient client = MinecraftClient.getInstance();
        int time = getMoonDoorTime();

		if (time > MDTConfig.MOON_WINDOW && !mdtReadySounded) {
			mdtReadySounded = true;
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					MDTConfig mdt = Options.getInstance().mdtConfig;
					int time = mdt.getMoonDoorTime();
					if (time == MDTConfig.MOON_WINDOW) {
						if (mdt.soundEnabled) client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_BEACON_POWER_SELECT, 1, 1));
						timer.cancel();
						timer.purge();
					}
				}
			}, 1000, 1000);
		} else if (time > 0 && !mdtNowSounded) {
			mdtNowSounded = true;
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					MDTConfig mdt = Options.getInstance().mdtConfig;
					int time = mdt.getMoonDoorTime();
					if (time == 0) {
						if (mdt.soundEnabled) client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_BEACON_DEACTIVATE, 1, 1));
						timer.cancel();
						timer.purge();
					}
				}
			}, 1000, 1000);
		}
    }
}
