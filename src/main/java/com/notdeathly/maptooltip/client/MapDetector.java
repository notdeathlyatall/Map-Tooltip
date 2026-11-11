package com.notdeathly.maptooltip.client;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.saveddata.maps.MapId;

import org.jspecify.annotations.Nullable;

/**
 * Finds the map that should be previewed for a stack.
 *
 * <p>Detection is deliberately component-based: the item identity must be
 * {@link Items#FILLED_MAP} and the map is identified by {@link DataComponents#MAP_ID}. Display
 * names, custom names and lore are never consulted, so an anvil-renamed map behaves exactly like
 * an unnamed one.</p>
 */
public final class MapDetector {
	private MapDetector() {
	}

	public record PreviewTarget(MapId mapId, boolean nested) {
	}

	public static @Nullable PreviewTarget findPreviewTarget(ItemStack stack) {
		if (stack.isEmpty()) {
			return null;
		}

		MapId directId = stack.get(DataComponents.MAP_ID);
		if (stack.getItem() == Items.FILLED_MAP && directId != null) {
			return new PreviewTarget(directId, false);
		}

		MapId nestedId = findNestedMap(stack);
		return nestedId == null ? null : new PreviewTarget(nestedId, true);
	}

	/**
	 * Best-effort nested lookup: bundles and item containers (shulker boxes) expose their
	 * contents to the client. Only the first filled map is previewed. Other container types,
	 * and containers whose contents are hidden from the tooltip, are intentionally not scanned.
	 */
	private static @Nullable MapId findNestedMap(ItemStack stack) {
		BundleContents bundle = stack.get(DataComponents.BUNDLE_CONTENTS);
		if (bundle != null && tooltipShows(stack, DataComponents.BUNDLE_CONTENTS)) {
			for (ItemStackTemplate template : bundle.items()) {
				MapId mapId = mapIdOf(template);
				if (mapId != null) {
					return mapId;
				}
			}
		}

		ItemContainerContents container = stack.get(DataComponents.CONTAINER);
		if (container != null && tooltipShows(stack, DataComponents.CONTAINER)) {
			for (ItemStackTemplate template : container.nonEmptyItems()) {
				MapId mapId = mapIdOf(template);
				if (mapId != null) {
					return mapId;
				}
			}
		}

		return null;
	}

	private static @Nullable MapId mapIdOf(ItemStackTemplate template) {
		if (template.item().value() != Items.FILLED_MAP) {
			return null;
		}

		return template.get(DataComponents.MAP_ID);
	}

	private static boolean tooltipShows(ItemStack stack, DataComponentType<?> type) {
		TooltipDisplay display = stack.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
		return display.shows(type);
	}
}
