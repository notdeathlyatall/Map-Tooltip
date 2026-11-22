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
 * bundles.
 *
 * <p>Injecting at {@code RETURN} is deliberate. Shulker Box Tooltip and other preview mods also
 * own this slot, and the last injection wins. By wrapping the value they already produced, a map
 * can be added next to their component instead of replacing it. When this mod has nothing to add,
 * the existing value is left exactly as it was.</p>
 */
@Mixin(ItemStack.class)
public abstract class ItemStackTooltipMixin {
	@Inject(method = "getTooltipImage", at = @At("RETURN"), cancellable = true)
	private void maptooltipx$decorateTooltipImage(CallbackInfoReturnable<Optional<TooltipComponent>> callback) {
		Optional<TooltipComponent> current = callback.getReturnValue();
		TooltipComponent original = current == null ? null : current.orElse(null);
		TooltipComponent decorated = TooltipHandler.decorate((ItemStack) (Object) this, original);
		if (decorated != null) {
			callback.setReturnValue(Optional.of(decorated));
		}
	}
}
