package com.notdeathly.maptooltip.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;

import org.jspecify.annotations.Nullable;

/**
 * Connects detection, key state and tooltip components.
 *
 * <p>The only Mixin in the mod calls {@link #decorate(ItemStack, TooltipComponent)} from
 * {@code ItemStack.getTooltipImage()} at {@code RETURN}. Fabric's tooltip component callback then
 * supplies the renderer. No text line is removed or rewritten.</p>
 */
public final class TooltipHandler {
	private static final String SHULKER_BOX_TOOLTIP_ID = "shulkerboxtooltip";

	private TooltipHandler() {
	}

	/**
	 * Adds a map preview to the tooltip image slot.
	 *
	 * <p>A direct filled map previews on plain Shift. Nested maps (bundles and shulker-like
	 * containers) are different when Shulker Box Tooltip is installed: plain Shift is left to
	 * that mod so its own preview can appear, and our nested preview is only attached while
	 * Shift+Ctrl is held, which is the key combination Shulker Box Tooltip uses to lock its
	 * preview. The existing component is wrapped, not replaced, so both previews remain
	 * available. Without Shulker Box Tooltip, nested maps keep previewing on Shift.</p>
	 *
	 * @param stack    the stack whose tooltip image is being built
	 * @param original the component already returned by vanilla or another mod
	 * @return a replacement component, or {@code null} to leave the existing value untouched
	 */
	public static @Nullable TooltipComponent decorate(ItemStack stack, @Nullable TooltipComponent original) {
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

		if (target.nested()) {
			if (original instanceof MapTooltipComponent) {
				return null;
			}

			if (isShulkerBoxTooltipLoaded() && !ShiftHandler.isControlDown()) {
				return null;
			}

			return new MapTooltipComponent(target.mapId(), true, original);
		}

		// Keep another mod's direct-map component if it already supplied one.
		if (original != null) {
			return null;
		}

		return new MapTooltipComponent(target.mapId(), false, null);
	}

	private static boolean isShulkerBoxTooltipLoaded() {
		return FabricLoader.getInstance().isModLoaded(SHULKER_BOX_TOOLTIP_ID);
	}

	public static @Nullable ClientTooltipComponent getClientComponent(TooltipComponent component) {
		if (component instanceof MapTooltipComponent mapComponent) {
			return new MapTooltipClientComponent(mapComponent);
		}

		return null;
	}
}
