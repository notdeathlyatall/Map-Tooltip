package com.notdeathly.maptooltip.client;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

/**
 * Draws the real, synchronized map texture through vanilla's own GUI map path.
 *
 * <p>{@link GuiGraphicsExtractor#map(MapRenderState)} is the method vanilla's cartography table
 * uses. It blits the texture produced by {@code MapTextureManager} and overlays the vanilla
 * decoration sprites, rotations and labels. This class only supplies the render state extracted by
 * {@link MapRenderer#extractRenderState(MapId, MapItemSavedData, MapRenderState)}.</p>
 *
 * <p>Vanilla's GUI helper honours {@code renderOnFrame}, which filters out player markers because
 * they are not drawn on item frames. A held map renders every decoration, so our private copy of
 * the render state marks all decorations visible before drawing. Map data itself is never
 * modified.</p>
 */
public final class MapPreviewRenderer {
	public static final MapPreviewRenderer INSTANCE = new MapPreviewRenderer();

	private static final Component UNAVAILABLE = Component.translatable("maptooltip-x.preview.unavailable");
	private static final int UNAVAILABLE_COLOR = 0xFFB8B8B8;
	private static final int UNAVAILABLE_BACKGROUND = 0xFF101010;
	private static final int LINE_HEIGHT = 9;

	private final MapRenderState renderState = new MapRenderState();

	private MapPreviewRenderer() {
	}

	/**
	 * Draws a map at the current pose origin. The caller must already have translated the pose to
	 * the top-left corner of the map. All drawing is render-thread only; the reusable render state
	 * is never shared with another frame.
	 */
	public void render(GuiGraphicsExtractor graphics, Font font, MapId mapId, int mapSize) {
		MapItemSavedData data = MapDataResolver.resolve(mapId);
		if (data == null) {
			renderUnavailable(graphics, font, mapSize);
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();
		minecraft.getMapRenderer().extractRenderState(mapId, data, this.renderState);

		if (this.renderState.texture == null) {
			renderUnavailable(graphics, font, mapSize);
			return;
		}

		for (MapRenderState.MapDecorationRenderState decoration : this.renderState.decorations) {
			decoration.renderOnFrame = true;
		}

		float scale = mapSize / (float) MapRenderer.WIDTH;
		graphics.pose().pushMatrix();
		graphics.pose().scale(scale, scale);
		graphics.map(this.renderState);
		graphics.pose().popMatrix();
	}

	private static void renderUnavailable(GuiGraphicsExtractor graphics, Font font, int mapSize) {
		graphics.fill(0, 0, mapSize, mapSize, UNAVAILABLE_BACKGROUND);

		List<FormattedCharSequence> lines = font.split(UNAVAILABLE, Math.max(32, mapSize - 12));
		int textY = (mapSize - lines.size() * LINE_HEIGHT) / 2;

		for (FormattedCharSequence line : lines) {
			graphics.centeredText(font, line, mapSize / 2, textY, UNAVAILABLE_COLOR);
			textY += LINE_HEIGHT;
		}
	}

}
