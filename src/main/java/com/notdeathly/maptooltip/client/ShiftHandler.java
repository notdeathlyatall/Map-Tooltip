package com.notdeathly.maptooltip.client;

import net.minecraft.client.Minecraft;

/**
 * Reads the current keyboard state through Minecraft's own input helpers.
 *
 * <p>{@link Minecraft#hasShiftDown()} and {@link Minecraft#hasControlDown()} check the raw
 * left/right keys. Using the sneak key binding instead would be wrong when toggle-sneak is
 * enabled, because a toggle can stay down after the physical key is released.</p>
 *
 * <p>Control is only used to match Shulker Box Tooltip's lock-preview key when that mod is
 * installed. It has no effect on direct filled maps.</p>
 */
public final class ShiftHandler {
	private ShiftHandler() {
	}

	public static boolean isShiftDown() {
		Minecraft minecraft = Minecraft.getInstance();
		return minecraft != null && minecraft.hasShiftDown();
	}

	public static boolean isControlDown() {
		Minecraft minecraft = Minecraft.getInstance();
		return minecraft != null && minecraft.hasControlDown();
	}
}
