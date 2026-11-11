package com.notdeathly.maptooltip.client;

import java.util.Objects;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.saveddata.maps.MapId;

import org.jspecify.annotations.Nullable;

/**
 * Data-side tooltip component. Fabric's {@code ClientTooltipComponentCallback} converts this into
 * {@link MapTooltipClientComponent} on the client.
 *
 * @param mapId    the synchronized map to preview
 * @param nested   whether the map was found inside a bundle or item container
 * @param original the container's own tooltip image, if it had one (for example a bundle grid)
 */
public record MapTooltipComponent(MapId mapId, boolean nested, @Nullable TooltipComponent original)
		implements TooltipComponent {
	public MapTooltipComponent {
		Objects.requireNonNull(mapId, "mapId");
	}
}
