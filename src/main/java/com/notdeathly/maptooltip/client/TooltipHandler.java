package com.notdeathly.maptooltip.client;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;

import org.jspecify.annotations.Nullable;

/**
 * Connects detection, shift state and tooltip components.
 *
 * <p>The only Mixin in the mod calls {@link #createPreview(ItemStack)} from
 * {@code ItemStack.getTooltipImage()}; Fabric's client tooltip component callback then supplies
 * the renderer. No text line is removed or rewritten.</p>
 */
public final class TooltipHandler {
	private TooltipHandler() {
	}

	/**
	 * @return a preview component, or {@code null} to leave vanilla (and every other tooltip mod)
	 *         completely untouched
	 */
	public static @Nullable TooltipComponent createPreview(ItemStack stack) {
		if (!ShiftHandler.isShiftDown()) {
			return null;
		}

		TooltipDisplay display = stack.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
		if (display.hideTooltip()) {
			return null;
		}

		MapDetector.PreviewTarget target = MapDetector.findPreviewTarget(stack);
		if (target == null) {
			return null;
		}

		TooltipComponent original = null;
		if (target.nested()) {
			original = stack.getItem().getTooltipImage(stack).orElse(null);
			if (original instanceof MapTooltipComponent) {
				original = null;
			}
		}

		return new MapTooltipComponent(target.mapId(), target.nested(), original);
	}

	public static @Nullable ClientTooltipComponent getClientComponent(TooltipComponent component) {
		if (component instanceof MapTooltipComponent mapComponent) {
			return new MapTooltipClientComponent(mapComponent);
		}

		return null;
	}
}
