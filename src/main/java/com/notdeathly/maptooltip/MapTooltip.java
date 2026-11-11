package com.notdeathly.maptooltip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shared constants for MapTooltip X.
 *
 * <p>The mod is client-only, so there is deliberately no common initializer: everything is
 * registered from {@link com.notdeathly.maptooltip.client.MapTooltipClient}.</p>
 */
public final class MapTooltip {
	public static final String MOD_ID = "maptooltip-x";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private MapTooltip() {
	}
}
