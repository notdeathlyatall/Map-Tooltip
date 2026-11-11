package com.notdeathly.maptooltip.client;

import com.notdeathly.maptooltip.MapTooltip;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;

/**
 * Client entrypoint. Registers the bridge that turns our tooltip data object into a tooltip
 * renderer. No server entrypoint exists, and the mod never talks to the network.
 */
public final class MapTooltipClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientTooltipComponentCallback.EVENT.register(TooltipHandler::getClientComponent);
		MapTooltip.LOGGER.info("MapTooltip X ready: hold Shift over a filled map to preview it.");
	}
}
