package com.notdeathly.maptooltip.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import org.jspecify.annotations.Nullable;

/**
 * Resolves the client-side map data that the server has already synchronized.
 *
 * <p>No request is ever sent. If the client has not received the map's data (for example a map
 * that has only been seen inside a chest), this returns {@code null} and the preview shows a
 * neutral unavailable state.</p>
 */
public final class MapDataResolver {
	private MapDataResolver() {
	}

	public static @Nullable MapItemSavedData resolve(MapId mapId) {
		ClientLevel level = Minecraft.getInstance().level;
		return level == null ? null : level.getMapData(mapId);
	}
}
