package com.notdeathly.maptooltip.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.world.level.saveddata.maps.MapId;

import org.jspecify.annotations.Nullable;

/**
 * Tooltip line that renders a map preview. Vanilla lays out and positions the whole tooltip, so
 * this component only reports its size and draws inside the rectangle it is given.
 */
public final class MapTooltipClientComponent implements ClientTooltipComponent {
	private static final int BORDER = 2;
	private static final int GAP = 2;
	private static final int MIN_MAP_SIZE = 48;
	private static final int NESTED_WITH_ORIGINAL_SIZE = 96;
	private static final int TEXT_RESERVE = 40;
	private static final int BORDER_COLOR = 0xFF101010;
	private static final int FRAME_COLOR = 0xFF8B8B8B;
	private static final int MAP_BACKGROUND = 0xFF000000;

	private final MapId mapId;
	private final @Nullable ClientTooltipComponent original;
	private final int mapSize;

	public MapTooltipClientComponent(MapTooltipComponent component) {
		this.mapId = component.mapId();
		this.original = component.original() == null ? null : ClientTooltipComponent.create(component.original());
		this.mapSize = selectMapSize(component.nested(), this.original);
	}

	private static int selectMapSize(boolean nested, @Nullable ClientTooltipComponent original) {
		int preferred = nested && original != null ? NESTED_WITH_ORIGINAL_SIZE : MapRenderer.WIDTH;
		Font font = Minecraft.getInstance().font;
		int guiHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
		int originalHeight = original == null ? 0 : original.getHeight(font) + GAP;
		int available = guiHeight - TEXT_RESERVE - originalHeight - BORDER * 2;

		int size = Math.min(preferred, available);
		size = Math.max(MIN_MAP_SIZE, size);
		size = size / 8 * 8;
		return Math.max(MIN_MAP_SIZE, Math.min(size, preferred));
	}

	private int panelSize() {
		return this.mapSize + BORDER * 2;
	}

	@Override
	public int getWidth(Font font) {
		int originalWidth = this.original == null ? 0 : this.original.getWidth(font);
		return Math.max(panelSize(), originalWidth);
	}

	@Override
	public int getHeight(Font font) {
		int originalHeight = this.original == null ? 0 : this.original.getHeight(font) + GAP;
		return originalHeight + panelSize();
	}

	@Override
	public boolean showTooltipWithItemInHand() {
		return this.original != null && this.original.showTooltipWithItemInHand();
	}

	@Override
	public void extractText(GuiGraphicsExtractor graphics, Font font, int x, int y) {
		if (this.original != null) {
			this.original.extractText(graphics, font, x, y);
		}
	}

	@Override
	public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
		int currentY = y;
		if (this.original != null) {
			this.original.extractImage(font, x, currentY, w, this.original.getHeight(font), graphics);
			currentY += this.original.getHeight(font) + GAP;
		}

		int panel = panelSize();
		int panelX = x + Math.max(0, (w - panel) / 2);
		graphics.fill(panelX, currentY, panelX + panel, currentY + panel, BORDER_COLOR);
		graphics.fill(panelX + 1, currentY + 1, panelX + panel - 1, currentY + panel - 1, FRAME_COLOR);
		graphics.fill(panelX + BORDER, currentY + BORDER, panelX + BORDER + this.mapSize, currentY + BORDER + this.mapSize, MAP_BACKGROUND);

		graphics.pose().pushMatrix();
		graphics.pose().translate(panelX + BORDER, currentY + BORDER);
		MapPreviewRenderer.INSTANCE.render(graphics, font, this.mapId, this.mapSize);
		graphics.pose().popMatrix();
	}
}
