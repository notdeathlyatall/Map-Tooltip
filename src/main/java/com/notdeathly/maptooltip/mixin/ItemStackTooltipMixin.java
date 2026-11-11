package com.notdeathly.maptooltip.mixin;

import java.util.Optional;

import com.notdeathly.maptooltip.client.TooltipHandler;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds the preview to the tooltip image slot that vanilla already provides for items such as
 * bundles. Vanilla, Fabric and other mods keep full control of the tooltip text and of every stack
 * that is not a filled map with Shift held.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackTooltipMixin {
	@Inject(method = "getTooltipImage", at = @At("HEAD"), cancellable = true)
	private void maptooltipx$previewFilledMap(CallbackInfoReturnable<Optional<TooltipComponent>> callback) {
		TooltipComponent preview = TooltipHandler.createPreview((ItemStack) (Object) this);
		if (preview != null) {
			callback.setReturnValue(Optional.of(preview));
		}
	}
}
