package com.notdeathly.maptooltip.client;

import net.minecraft.client.Minecraft;

/**
 * Reads the current keyboard state through Minecraft's own 26.3 input helper.
 *
 * <p>{@link Minecraft#hasShiftDown()} checks the raw left/right Shift keys. Using the sneak
 * key binding instead would be wrong when toggle-sneak is enabled, because a toggle can stay
 * down after the physical key is released.</p>
 */
public final class ShiftHandler {
	private ShiftHandler() {
	}

	public static boolean isShiftDown() {
		Minecraft minecraft = Minecraft.getInstance();
		return minecraft != null && minecraft.hasShiftDown();
	}
}
