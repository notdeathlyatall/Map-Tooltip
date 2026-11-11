# MapTooltip X

MapTooltip X is a client-only Fabric mod for **Minecraft Java Edition 26.3 (Wilderness Bound)**.
Hold **Shift** while hovering a filled map in an inventory or container screen and a preview of
that exact map appears inside the item's tooltip.

The preview is not a picture, a generated approximation, or a reconstructed colour grid. It uses the
map data the server has already synchronized to the client and draws it through Minecraft's own map
rendering path. Anvil-renamed maps, custom names, lore, zoom levels and decorations all work because
the mod identifies maps by item and `MAP_ID` component, never by name.

---

## 1. Description

| | |
|---|---|
| Mod name | MapTooltip X |
| Mod ID | `maptooltip-x` |
| Minecraft | 26.3 (Wilderness Bound) |
| Loader | Fabric Loader 0.20.1 |
| Fabric API | 0.166.0+26.3 |
| Java | 25 |
| Environment | Client only |
| Mappings | Official Mojang names (non-remapping Loom 1.19.4) |
| Gradle | 9.8.2 |

There is no server component, no networking, no packets, no configuration screen and no
world-data modification. The mod is written in Java only.

## 2. Features

- Shift + hover over `Items.FILLED_MAP` shows a real preview of the synchronized map.
- Works with anvil-renamed maps, custom names, lore and any additional data components.
- Preserves the complete vanilla tooltip text, name and style.
- Reuses vanilla `MapRenderer`, `MapRenderState`, `MapTextureManager` and
  `GuiGraphicsExtractor.map(...)`; no placeholder texture is shipped.
- Displays the vanilla 128 x 128 map texture, terrain colours, explored areas, structures,
  decorations, banners, player arrows and directional markers.
- Includes all map decorations, not only the decorations vanilla draws on item frames, by marking
  a private copy of the render state visible.
- Supports player inventory, chests, barrels, ender chests, shulker boxes, hoppers, droppers,
  dispensers, creative inventory and other screens based on `AbstractContainerScreen`.
- Best-effort previews for the first filled map inside a bundle or an item container
  (`BUNDLE_CONTENTS` / `CONTAINER`), while preserving the container's own tooltip image where one
  exists.
- Shows a clean "Map data unavailable" panel when the client has no synchronized data for that map.
- Adapts the preview size downwards on short screens so the tooltip can still fit.
- Does not create new textures or copy the map's colour array; vanilla's `MapTextureManager`
  keeps ownership of the texture.
- Reflects map updates while the tooltip is open, because the render state is extracted from the
  live `MapItemSavedData` each frame.

## 3. Installation

1. Install **Java 25** and a launcher that can run Minecraft 26.3.
2. Install **Fabric Loader 0.20.1** (or newer) for Minecraft 26.3.
3. Place **Fabric API 0.166.0+26.3** (or a compatible newer 26.3 build) in the `mods` folder.
4. Place `maptooltip-x-1.0.0.jar` from `build/libs` in the same `mods` folder.
5. Start the client. The mod is client-side, so it works on vanilla multiplayer servers; the server
   does not need the mod and does not receive anything from it.

## 4. Build

Linux/macOS:

```bash
./gradlew build
```

Windows PowerShell:

```powershell
.\gradlew.bat build
```

The build uses the Gradle wrapper (9.8.2) and downloads Minecraft 26.3, Loom, Fabric Loader and
Fabric API from the official repositories. A Java 25 JDK is required.

Outputs:

```text
build/libs/maptooltip-x-1.0.0.jar
build/libs/maptooltip-x-1.0.0-sources.jar
```

To regenerate readable Minecraft sources (already done while developing this mod):

```bash
./gradlew genSources
```

## 5. Development environment requirements

- JDK 25 (the build was verified with Eclipse Temurin 25.0.5+8).
- Gradle 9.8.2 through the included wrapper.
- Fabric Loom 1.19.4 (`net.fabricmc.fabric-loom`, the non-remapping plugin used for Minecraft
  26.1 and newer).
- Internet access on the first build so Loom can download Minecraft and Fabric artifacts.
- No IDE or Kotlin installation is required; the project is plain Java and Gradle.

Versions are pinned in `gradle.properties`. They were checked against the Fabric Maven and the
official 26.3 dependency set rather than copied from an older project.

## 6. Shift-based map detection

The detection chain is:

1. **Shift** — `ShiftHandler.isShiftDown()` calls `Minecraft.hasShiftDown()`. This 26.3 helper reads
   the raw left/right Shift key state. The sneak key binding is deliberately not used, because
   toggle-sneak can report "down" after the physical key is released.
2. **Item identity** — the stack's item must be exactly `Items.FILLED_MAP`.
3. **Map identity** — the stack must have `DataComponents.MAP_ID`. In 26.3 the value type is
   `net.minecraft.world.level.saveddata.maps.MapId` (a record with an `int id()`), not the older
   `MapIdComponent` name. The requested `MapIdComponent` behaviour is what this component provides.
4. **Data** — `ClientLevel.getMapData(MapId)` returns the `MapItemSavedData` the server has already
   sent to this client.

Names, custom names, lore, tooltip lines and the map's rendered ID text are never inspected. That is
why renaming a map in an anvil cannot affect detection.

The Mixin intercepts `ItemStack.getTooltipImage()` only. When Shift is not held, or the stack is not
a map, the method is not cancelled and vanilla behaviour is untouched. When the condition matches,
the method returns a `TooltipComponent` for the map preview. Releasing Shift immediately restores
the normal tooltip on the next frame because container screens rebuild their tooltip every frame.
Nothing about the hovered slot is cached, so quickly moving between slots cannot show a stale
preview.

Nested lookup is intentionally conservative:

- `BUNDLE_CONTENTS` is scanned only when `TooltipDisplay` still shows that component.
- `CONTAINER` (shulker-box contents) is scanned under the same rule.
- The first filled map is used; scanning stops there.
- Nesting is supported one level deep.

## 7. Vanilla map rendering integration

The important 26.3 APIs, as verified from Loom's generated sources, are:

```java
// net.minecraft.client.renderer.MapRenderer
public void extractRenderState(MapId mapId, MapItemSavedData mapData, MapRenderState mapRenderState)

// net.minecraft.client.renderer.state.MapRenderState
public @Nullable Identifier texture;
public final List<MapRenderState.MapDecorationRenderState> decorations;

// net.minecraft.client.gui.GuiGraphicsExtractor
public void map(MapRenderState mapRenderState)

// net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics)
```

The render path is:

1. `MapDataResolver` obtains the client-side `MapItemSavedData` for the `MapId`.
2. `MapPreviewRenderer` calls `Minecraft.getMapRenderer().extractRenderState(...)`. That call
   delegates to `MapTextureManager.prepareMapTexture(...)`, which updates/registers the real
   128 x 128 dynamic map texture, and captures every `MapDecoration` with its vanilla sprite,
   position, rotation and label.
3. The private `MapRenderState` is drawn with `GuiGraphicsExtractor.map(renderState)`, the same
   method the vanilla cartography table uses to preview maps.
4. `renderOnFrame` is set to `true` on the private render-state decoration entries. Vanilla marks
   player arrows and off-map indicators as `showOnItemFrame == false`, and its GUI map helper skips
   those; a held map shows them, so the preview does too. The underlying `MapItemSavedData` is never
   modified.

There is no generated texture, no placeholder image, no colour reconstruction and no screenshot.
If the map data is absent, the renderer draws an unavailable panel instead of dereferencing a null
texture. Map texture updates continue to arrive through vanilla's normal
`ClientboundMapItemDataPacket` handling (`MapTextureManager.update`), so no custom packet is
required.

## 8. Mixin documentation

One client Mixin is used:

```text
com.notdeathly.maptooltip.mixin.ItemStackTooltipMixin
```

It injects at the head of `ItemStack.getTooltipImage()` with `cancellable = true`. It asks
`TooltipHandler.createPreview(...)` for a preview and, only when one exists, returns
`Optional.of(preview)`. Otherwise it does nothing at all.

Why a Mixin is necessary: Fabric's `ItemTooltipCallback` can append text lines only. Fabric's
`ClientTooltipComponentCallback` can convert a custom `TooltipComponent` into a renderer, but it
cannot attach an image to an arbitrary item stack. The tooltip image slot is decided inside
`ItemStack.getTooltipImage()`, so that one method is the minimal integration point.

`maptooltip.mixins.json` is declared with `"environment": "client"` in `fabric.mod.json`. The
`client` array contains only the Mixin above; there is no common Mixin and no server Mixin.

## 9. Testing instructions

### Manual setup

1. Run `./gradlew runClient` to launch a development client, or install the built JAR in a normal
   26.3 Fabric profile.
2. Create or join a world and obtain filled maps. In creative, the vanilla `filled_map` item and
   cartography table are enough; explicit command syntax is not required by this mod.
3. Open the player inventory.
4. Hover a filled map normally: the tooltip must be exactly vanilla.
5. Hold Shift while hovering: the map preview must appear below the item name.
6. Release Shift: the vanilla tooltip must return immediately.
7. Move rapidly across slots with Shift held: the preview must switch to the hovered map without
   showing the previous map.

### Scenario checklist

| Scenario | How to verify |
|---|---|
| Normal filled map | Hover with Shift in the player inventory. |
| Anvil-renamed map | Rename a map in an anvil, then hover it with Shift. Detection must still work. |
| Custom name with lore | Rename and add lore; all vanilla lines must remain, with the preview below the name. |
| Different zoom levels | Use cartography-table zoom (scale 0-4); the preview must show the real zoomed texture. |
| Decorations and player markers | Add banners, place the map in an item frame, or use a map held by another player; player arrows and markers must appear. |
| Map data unavailable | Hover a map that has never been held or placed after joining; the unavailable panel must appear, not a crash. |
| Player inventory | Covered by the standard container tooltip path. |
| Chest, barrel, ender chest | Same path, all `AbstractContainerScreen` subclasses. |
| Shulker box | Hover a shulker containing a filled map with Shift; the first map is previewed. |
| Hopper, dropper, dispenser | Same container path. |
| Bundle with a map | Hover the bundle with Shift; the bundle grid and a smaller map preview are shown. |
| GUI scale 1-4+ | Change GUI scale in Options and repeat; the preview should stay inside the screen at normal resolutions and shrink on very short screens. |
| Shift pressed/released repeatedly | No stale image should remain after release. |
| Rapid slot movement | No stale map, crash or lingering texture should appear. |
| Multiplayer | Join a vanilla server; the server does not need the mod. |
| Hidden tooltip | A stack with `hide_tooltip` must not reveal a preview. |

### Automated/build checks performed here

- `./gradlew genSources` completed successfully against Minecraft 26.3.
- The mod compiled with `./gradlew build` using Java 25 against the real 26.3 artifacts.
- The final JAR was opened and inspected: it contains only the mod classes, `fabric.mod.json`,
  `maptooltip.mixins.json`, the icon, the lang file and the license. It contains no server
  classes, no decompiled reference sources, no template classes and no test fixtures.
- The expanded `fabric.mod.json` reports `environment: client`, one client entrypoint, one
  client-only Mixin config, and dependencies on Fabric Loader >= 0.20.1, Minecraft ~26.3, Java >= 25
  and Fabric API.
- `MapRenderer`, `MapRenderState`, `MapTextureManager`, `MapId`, `TooltipComponent`,
  `ClientTooltipComponent`, `GuiGraphicsExtractor.map(...)`, `ItemStack.getTooltipImage()` and
  Fabric's `ClientTooltipComponentCallback` were read from the actual generated 26.3 sources before
  the code was written.

### Not performed

This development environment is headless and has no Minecraft display, GPU or game session, so the
mod was **not launched in-game and no visual or multiplayer test was run here**. The checklist above
is the manual test plan. Build success, JAR inspection and API verification are real; claims about
in-game appearance would not be.

## 10. Known limitations

- **In-game behaviour is unverified in this environment.** The project compiles and packages
  correctly, but no client session was launched.
- **No networking.** If the server has not sent a map's data to this client, no preview can be
  rendered; the unavailable panel is shown. The mod deliberately does not request map data, because
  custom packets are out of scope.
- **Nested maps are best-effort.** Only the first filled map inside one level of a bundle or
  `CONTAINER` item is found. Bundles show their normal grid plus a smaller preview, which makes the
  tooltip taller; at extreme GUI scales or very small windows the vanilla positioner may crop it.
  Custom container items that do not use `BUNDLE_CONTENTS` or `CONTAINER` are not scanned.
- **Hidden contents stay hidden.** If a container's contents are hidden through `TooltipDisplay`,
  its nested map is not previewed.
- **Adaptive size.** On short screens the preview is scaled down (down to 48 x 48 GUI pixels) to
  keep the tooltip on screen. The map content is still the real texture; it is simply smaller.
- **Maps in other dimensions** can only render if the client has data for that map ID. Otherwise the
  unavailable panel is shown.
- **Other tooltip mods.** Text modifications are untouched. If another mod replaces the tooltip
  image of a bundle (not just its text), the nested preview is layered on top of that component
  while Shift is held; no attempt is made to merge arbitrary third-party image components.
- **No configuration.** Shift is the only trigger; this is intentional and matches the requested
  scope.
- **No server support.** Installing the JAR on a server is harmless but does nothing; it is a
  client mod.

## 11. Project layout

```text
MapTooltip-X/
├── build.gradle
├── settings.gradle
├── gradle.properties
├── gradlew / gradlew.bat
├── gradle/wrapper/
├── README.md
├── LICENSE
└── src/main/
    ├── java/com/notdeathly/maptooltip/
    │   ├── MapTooltip.java
    │   ├── client/
    │   │   ├── MapTooltipClient.java      client entrypoint / Fabric callback registration
    │   │   ├── MapDetector.java           item + MAP_ID detection, nested lookup
    │   │   ├── MapDataResolver.java       synchronized client map data
    │   │   ├── MapPreviewRenderer.java    vanilla render-state extraction + GUI map draw
    │   │   ├── MapTooltipComponent.java   data-side tooltip component
    │   │   ├── MapTooltipClientComponent.java tooltip layout and drawing
    │   │   ├── TooltipHandler.java        preview factory / Fabric client callback
    │   │   └── ShiftHandler.java          26.3 Shift input
    │   └── mixin/
    │       └── ItemStackTooltipMixin.java
    └── resources/
        ├── fabric.mod.json
        ├── maptooltip.mixins.json
        └── assets/maptooltip-x/
            ├── icon.png
            └── lang/en_us.json
```

## 12. License

The repository's `LICENSE` file applies. The mod metadata currently declares
`All Rights Reserved`, matching that file. Fabric API, Minecraft and their assets remain under
their own licenses; no Minecraft assets are redistributed in this JAR.
