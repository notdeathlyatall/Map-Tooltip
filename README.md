# MapTooltip X

MapTooltip X is a client-only Fabric mod that shows a real preview of a filled map inside its
tooltip while **Shift** is held. It supports **Minecraft Java Edition 26.3 (Wilderness Bound)** and
**Minecraft Java Edition 1.21.11**.

The preview is not a picture, a generated approximation, or a reconstructed colour grid. It uses the
map data the server has already synchronized to the client and draws it through Minecraft's own map
rendering path. Anvil-renamed maps, custom names, lore, zoom levels and decorations all work because
the mod identifies maps by item and `MAP_ID` component, never by name.

MapTooltip X also cooperates with **Shulker Box Tooltip**. Plain Shift leaves that mod's container
preview alone; Shift+Ctrl adds the nested map preview while Shulker Box Tooltip's lock keeps the
container open.

---

## 1. Supported versions

| | 26.3 | 1.21.11 |
|---|---|---|
| Minecraft | 26.3 (Wilderness Bound) | 1.21.11 |
| Mod JAR | `dist/maptooltip-x-1.0.0-26.3.jar` | `dist/maptooltip-x-1.0.0-1.21.11.jar` |
| Java | 25 | 21 |
| Fabric Loader | 0.20.1 | 0.20.1 (minimum 0.17.3) |
| Fabric API | 0.166.0+26.3 | 0.141.6+1.21.11 |
| Loom | 1.19.4, non-remapping | 1.19-SNAPSHOT, remapping |
| Mappings | Official Mojang names | Official Mojang names |
| Environment | Client only | Client only |

Prebuilt copies of both JARs are tracked in [`dist/`](dist/README.md) for direct download, with
SHA-256 checksums in [`dist/README.md`](dist/README.md). The build commands below still produce the
same binaries under each project's `build/libs`.

Install **only the JAR for your game version**. Both artifacts use the mod ID `maptooltip-x`; they
are alternative builds, not add-ons.

### Dependencies

| | 26.3 | 1.21.11 |
|---|---|---|
| Required at runtime | Minecraft 26.3, Fabric Loader 0.20.1+, Fabric API, Java 25+ | Minecraft 1.21.11, Fabric Loader 0.17.3+, Fabric API, Java 21+ |
| Built against | Fabric Loader 0.20.1, Fabric API 0.166.0+26.3 | Fabric Loader 0.20.1, Fabric API 0.141.6+1.21.11 |
| Build tooling | Gradle 9.8.2 wrapper, Fabric Loom 1.19.4, official Mojang names | Gradle 9.8.2 wrapper, Fabric Loom 1.19-SNAPSHOT, official Mojang mappings |
| Optional compatibility | Shulker Box Tooltip for your game version (not required) | Shulker Box Tooltip for your game version (not required) |

Fabric API is declared as `fabric-api: *` in both `fabric.mod.json` files and is supplied by the
player; it is not bundled in either JAR. No other runtime libraries are required, bundled or shaded.
The mod is not compiled against Shulker Box Tooltip; compatibility uses the shared vanilla tooltip
component API and a mod-ID check.

## 2. Features

- Shift + hover over `Items.FILLED_MAP` shows a real preview of the synchronized map.
- Works with anvil-renamed maps, custom names, lore and any additional data components.
- Preserves the complete vanilla tooltip text, name and style.
- Reuses vanilla `MapRenderer`, `MapRenderState`, `MapTextureManager` and the version's GUI map
  draw path; no placeholder texture is shipped.
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
- No telemetry, no networking and no configuration screen.

## 3. Shulker Box Tooltip compatibility

Shulker Box Tooltip (mod ID `shulkerboxtooltip`) and MapTooltip X both use the same
`ItemStack.getTooltipImage()` slot. MapTooltip X therefore injects at **RETURN** and wraps whatever
component already exists instead of cancelling at HEAD and replacing it.

| Situation | Keys | Result |
|---|---|---|
| Direct filled map | Shift | MapTooltip X map preview |
| Shulker box or bundle, Shulker Box Tooltip installed | Shift | Shulker Box Tooltip's own preview; MapTooltip X leaves the image untouched |
| Shulker box or bundle, Shulker Box Tooltip installed | Shift+Ctrl | Shulker Box Tooltip's preview **and** the nested map preview |
| Shulker box or bundle, Shulker Box Tooltip installed | Shift+Ctrl, then move the mouse | Shulker Box Tooltip locks the hovered container, so the combined tooltip stays visible while you move |
| Shulker box or bundle, Shulker Box Tooltip not installed | Shift | MapTooltip X nested map preview |
| Any item | No Shift | Completely vanilla tooltip |

Ctrl is Shulker Box Tooltip's default lock-preview key. MapTooltip X checks the raw Control key only
when that mod is loaded; direct maps are not affected. If Shulker Box Tooltip's lock key is rebound
in its config, MapTooltip X still uses Ctrl as the nested-preview gate.

How the combination works:

1. Shulker Box Tooltip's Fabric mixin handles `getTooltipImage()` first and supplies its
   `PreviewTooltipComponent`.
2. MapTooltip X's mixin runs at `RETURN`, sees that component, and leaves it alone on plain Shift.
3. On Shift+Ctrl it creates a composite containing Shulker Box Tooltip's component plus the map
   preview. The existing component is rendered first, not discarded.
4. Shulker Box Tooltip's lock hook calls `getTooltipImage()` again while locked, receives the
   composite, and renders it immediately at the locked position. Releasing Ctrl unlocks normally
   and returns to the plain Shift behavior.

MapTooltip X does not compile against Shulker Box Tooltip and does not require it. Compatibility is
provided through the shared vanilla tooltip component API and a Fabric Loader mod-ID check.

## 4. Installation

### Minecraft 26.3

1. Install **Java 25** and a launcher that can run Minecraft 26.3.
2. Install **Fabric Loader 0.20.1** (or newer) for Minecraft 26.3.
3. Place **Fabric API 0.166.0+26.3** (or a compatible newer 26.3 build) in the `mods` folder.
4. Place `dist/maptooltip-x-1.0.0-26.3.jar` in the same `mods` folder.
5. Start the client.

### Minecraft 1.21.11

1. Install **Java 21** and a launcher that can run Minecraft 1.21.11.
2. Install **Fabric Loader 0.20.1** (minimum 0.17.3) for Minecraft 1.21.11.
3. Place **Fabric API 0.141.6+1.21.11** (or a compatible newer 1.21.11 build) in the `mods` folder.
4. Place `dist/maptooltip-x-1.0.0-1.21.11.jar` in the same `mods` folder.
5. Start the client.

The mod is client-side. It works on vanilla multiplayer servers; the server does not need the mod
and does not receive anything from it.

## 5. Build

### 26.3

Linux/macOS:

```bash
./gradlew build
```

Windows PowerShell:

```powershell
.\gradlew.bat build
```

A Java 25 JDK is required. Outputs:

```text
build/libs/maptooltip-x-1.0.0.jar
build/libs/maptooltip-x-1.0.0-sources.jar
```

### 1.21.11

The 1.21.11 build is a separate, self-contained Gradle project because 1.21.11 is still obfuscated
and needs Fabric Loom's remapping pipeline. It targets Java 21 bytecode.

Linux/macOS:

```bash
./gradlew -p 1.21.11 build
```

Windows PowerShell:

```powershell
.\gradlew.bat -p 1.21.11 build
```

A JDK 21 or newer is required. The checked build used JDK 25 with `--release 21`, so the resulting
classes are Java 21 class files. Outputs:

```text
1.21.11/build/libs/maptooltip-x-1.0.0.jar
1.21.11/build/libs/maptooltip-x-1.0.0-sources.jar
```

To regenerate readable 1.21.11 Minecraft sources:

```bash
./gradlew -p 1.21.11 genSources
```

## 6. Development environment requirements

### 26.3

- JDK 25 (verified with Eclipse Temurin 25.0.5+8).
- Gradle 9.8.2 through the included wrapper.
- Fabric Loom 1.19.4 (`net.fabricmc.fabric-loom`, the non-remapping plugin used for Minecraft
  26.1 and newer).
- Internet access on the first build so Loom can download Minecraft and Fabric artifacts.

### 1.21.11

- JDK 21 or newer (the build was verified with JDK 25 compiling with `--release 21`).
- Gradle 9.8.2 through the included wrapper.
- Fabric Loom 1.19-SNAPSHOT (`net.fabricmc.fabric-loom-remap`).
- Official Mojang mappings, applied by Loom.
- Internet access on the first build so Loom can download Minecraft, mappings, Fabric Loader and
  Fabric API.

No IDE or Kotlin installation is required; both projects are plain Java and Gradle. Versions are
pinned in the root `gradle.properties` and `1.21.11/gradle.properties`.

## 7. How map detection works

The detection chain is:

1. **Shift** — `ShiftHandler.isShiftDown()` calls `Minecraft.hasShiftDown()`. This reads the raw
   left/right Shift key state. The sneak key binding is deliberately not used, because toggle-sneak
   can report "down" after the physical key is released.
2. **Item identity** — the stack's item must be exactly `Items.FILLED_MAP`.
3. **Map identity** — the stack must have `DataComponents.MAP_ID`. The value type is
   `net.minecraft.world.level.saveddata.maps.MapId` (a record with an `int id()`).
4. **Data** — `ClientLevel.getMapData(MapId)` returns the `MapItemSavedData` the server has already
   sent to this client.

Names, custom names, lore, tooltip lines and the map's rendered ID text are never inspected. That is
why renaming a map in an anvil cannot affect detection.

The Mixin intercepts `ItemStack.getTooltipImage()` only. When Shift is not held, the stack is not a
map, or the tooltip image should be left to another mod, the method's existing return value is not
changed. When a preview is appropriate, the method returns a `TooltipComponent` for the map preview.
Releasing Shift immediately restores the normal tooltip on the next frame because container screens
rebuild their tooltip every frame. Nothing about the hovered slot is cached, so quickly moving
between slots cannot show a stale preview.

Nested lookup is intentionally conservative:

- `BUNDLE_CONTENTS` is scanned only when `TooltipDisplay` still shows that component.
- `CONTAINER` (shulker-box contents) is scanned under the same rule.
- The first filled map is used; scanning stops there.
- Nesting is supported one level deep.
- With Shulker Box Tooltip installed, nested previews require Shift+Ctrl as described above.

## 8. Vanilla map rendering integration

### 26.3

The important 26.3 APIs are:

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

### 1.21.11

The same pipeline uses the 1.21.11 signatures:

```java
// net.minecraft.client.renderer.MapRenderer
public void extractRenderState(MapId mapId, MapItemSavedData mapData, MapRenderState mapRenderState)

// net.minecraft.client.gui.GuiGraphics
public void submitMapRenderState(MapRenderState mapRenderState)

// net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
public void renderImage(Font font, int x, int y, int w, int h, GuiGraphics graphics)
public void renderText(GuiGraphics graphics, Font font, int x, int y)
```

`MapPreviewRenderer` creates a fresh `MapRenderState`, extracts the live map data, marks every
decoration visible, scales the current GUI pose to the requested preview size and calls
`submitMapRenderState(...)`.

There is no generated texture, no placeholder image, no colour reconstruction and no screenshot.
If the map data is absent, the renderer draws an unavailable panel instead of dereferencing a null
texture. Map texture updates continue to arrive through vanilla's normal
`ClientboundMapItemDataPacket` handling, so no custom packet is required.

## 9. Mixin documentation

One client Mixin is used in each build:

```text
com.notdeathly.maptooltip.mixin.ItemStackTooltipMixin
```

It injects at **RETURN** of `ItemStack.getTooltipImage()` with `cancellable = true`. It asks
`TooltipHandler.decorate(...)` whether a preview should be added. If not, it leaves the existing
`Optional` untouched. If yes, it wraps the existing component (which may belong to vanilla or
Shulker Box Tooltip) in a `MapTooltipComponent`.

Why a Mixin is necessary: Fabric's `ItemTooltipCallback` can append text lines only. Fabric's
tooltip component callback can convert a custom `TooltipComponent` into a renderer, but it cannot
attach an image to an arbitrary item stack. The tooltip image slot is decided inside
`ItemStack.getTooltipImage()`, so that one method is the minimal integration point.

`maptooltip.mixins.json` is declared with `"environment": "client"` in `fabric.mod.json`. The
`client` array contains only the Mixin above; there is no common Mixin and no server Mixin. The
1.21.11 JAR's Mixin annotation is remapped to the intermediary method name by Loom, so it resolves
in a production (obfuscated) 1.21.11 client without a refmap.

## 10. Testing checklist

### Basic tooltip behavior

1. Hover a filled map without Shift: the tooltip must be exactly vanilla.
2. Hold Shift: the map preview must appear below the item name.
3. Release Shift: the vanilla tooltip must return immediately.
4. Move rapidly across slots with Shift held: the preview must switch to the hovered map without
   showing the previous map.
5. Hover a map with `hide_tooltip`: no preview may appear.

### Shulker Box Tooltip behavior

1. Install Shulker Box Tooltip and a Shulker Box Tooltip-compatible Fabric API version.
2. Hover a shulker box containing a filled map with plain Shift: Shulker Box Tooltip's preview must
   appear, and MapTooltip X must not replace it or show a nested map panel.
3. Hold Shift+Ctrl over the same shulker: both Shulker Box Tooltip's preview and the map preview
   must appear.
4. Keep Shift+Ctrl held and move the mouse away: Shulker Box Tooltip's lock must keep the shulker
   preview open, with the map preview still attached.
5. Release Ctrl: the lock must release and plain Shift must show only Shulker Box Tooltip's preview.
6. Repeat with a bundle containing a map.

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
| Shulker box without Shulker Box Tooltip | Hover a shulker containing a filled map with Shift; the first map is previewed. |
| Hopper, dropper, dispenser | Same container path. |
| Bundle with a map | Hover the bundle with Shift; without Shulker Box Tooltip, the bundle grid and a smaller map preview are shown. |
| GUI scale 1-4+ | Change GUI scale in Options and repeat; the preview should stay inside the screen at normal resolutions and shrink on very short screens. |
| Shift pressed/released repeatedly | No stale image should remain after release. |
| Rapid slot movement | No stale map, crash or lingering texture should appear. |
| Multiplayer | Join a vanilla server; the server does not need the mod. |

### Automated/build checks performed here

- Both the 26.3 and 1.21.11 builds completed successfully: `./gradlew build` and
  `./gradlew -p 1.21.11 build`.
- The 26.3 JAR was opened and inspected: it contains only the mod classes, `fabric.mod.json`,
  `maptooltip.mixins.json`, the icon, the lang file and the license. It contains no server classes,
  no decompiled reference sources, no template classes and no test fixtures.
- The 1.21.11 JAR was opened and inspected with the same result. Its class files are Java 21
  (class-file version 65); the 26.3 class files are Java 25 (version 69).
- The expanded `fabric.mod.json` files report `environment: client`, one client entrypoint, one
  client-only Mixin config and the correct Minecraft/Java/Fabric dependencies.
- The 1.21.11 Mixin annotation was checked in the remapped JAR: `getTooltipImage` was rewritten to
  the intermediary method name `method_32347`, so the production Mixin targets the obfuscated
  method correctly.
- Map rendering, container components, `MapId`, `TooltipComponent`, `ClientTooltipComponent`,
  `MapRenderer`, `GuiGraphics`/`GuiGraphicsExtractor` and both Fabric tooltip callbacks were checked
  against the actual version artifacts before the code was written.
- No network, socket, telemetry or analytics code paths were found in either JAR. The only URL
  in either artifact is the project's source link in the mod metadata; there is no HTTP client
  code.

### Not performed

This development environment is headless and has no Minecraft display, GPU or game session, so
neither build was **launched in-game and no visual or multiplayer test was run here**. The checklist
above is the manual test plan. Build success, JAR inspection and API verification are real; claims
about in-game appearance would not be.

## 11. Privacy

MapTooltip X does **not** collect, store or transmit telemetry, analytics, crash reports, player
data, server data or map data. It contains no HTTP client, no socket code, no custom packets and no
background network activity. It reads only:

- the key state through Minecraft's input helpers;
- the item and data components of the hovered stack;
- the map data that Minecraft has already synchronized to the client.

Nothing is written outside normal Minecraft memory and rendering. There is no account system, no
configuration upload and no third-party service.

The full policy is in [`PRIVACY.md`](PRIVACY.md).

## 12. Known limitations

- **In-game behaviour is unverified in this environment.** Both projects compile and package
  correctly, but no client session was launched.
- **Choose the right artifact.** The root JAR is for 26.3 and Java 25; the `1.21.11` JAR is for
  1.21.11 and Java 21. They are not interchangeable.
- **No networking.** If the server has not sent a map's data to this client, no preview can be
  rendered; the unavailable panel is shown. The mod deliberately does not request map data, because
  custom packets are out of scope.
- **Nested maps are best-effort.** Only the first filled map inside one level of a bundle or
  `CONTAINER` item is found. Bundles show their normal grid plus a smaller preview, which makes the
  tooltip taller; at extreme GUI scales or very small windows the vanilla positioner may crop it.
  Custom container items that do not use `BUNDLE_CONTENTS` or `CONTAINER` are not scanned.
- **Hidden contents stay hidden.** If a container's contents are hidden through `TooltipDisplay`,
  its nested map is not previewed.
- **Shulker Box Tooltip lock key.** The compatibility gate uses Ctrl, matching Shulker Box
  Tooltip's default lock key. Rebinding that key in Shulker Box Tooltip does not change
  MapTooltip X's Ctrl gate.
- **Adaptive size.** On short screens the preview is scaled down (down to 48 x 48 GUI pixels) to
  keep the tooltip on screen. The map content is still the real texture; it is simply smaller.
- **Maps in other dimensions** can only render if the client has data for that map ID. Otherwise the
  unavailable panel is shown.
- **Other tooltip mods.** Text modifications are untouched. When Shulker Box Tooltip is absent, a
  nested preview wraps another mod's container image component; a direct filled map keeps another
  mod's direct-map component rather than stacking previews.
- **No configuration.** Shift is the only direct-map trigger; Shift+Ctrl is the nested-map trigger
  when Shulker Box Tooltip is installed. This is intentional and matches the requested scope.
- **No server support.** Installing a JAR on a server is harmless but does nothing; it is a client
  mod.

## 13. Project layout

```text
MapTooltip-X/
├── build.gradle                         26.3 build
├── settings.gradle
├── gradle.properties
├── gradlew / gradlew.bat
├── gradle/wrapper/
├── README.md
├── LICENSE
├── PRIVACY.md                            privacy policy
├── TERMS.md                              terms of service
├── dist/                                 prebuilt downloadable JARs
│   ├── README.md
│   ├── maptooltip-x-1.0.0-26.3.jar
│   └── maptooltip-x-1.0.0-1.21.11.jar
├── src/main/                            26.3 sources and resources
│   ├── java/com/notdeathly/maptooltip/
│   │   ├── MapTooltip.java
│   │   ├── client/
│   │   │   ├── MapTooltipClient.java
│   │   │   ├── MapDetector.java
│   │   │   ├── MapDataResolver.java
│   │   │   ├── MapPreviewRenderer.java
│   │   │   ├── MapTooltipComponent.java
│   │   │   ├── MapTooltipClientComponent.java
│   │   │   ├── TooltipHandler.java
│   │   │   └── ShiftHandler.java
│   │   └── mixin/
│   │       └── ItemStackTooltipMixin.java
│   └── resources/
│       ├── fabric.mod.json
│       ├── maptooltip.mixins.json
│       └── assets/maptooltip-x/
│           ├── icon.png
│           └── lang/en_us.json
└── 1.21.11/                             self-contained 1.21.11 build
    ├── build.gradle
    ├── settings.gradle
    ├── gradle.properties
    ├── gradlew / gradlew.bat
    ├── gradle/wrapper/
    ├── LICENSE
    └── src/main/                        same packages, 1.21.11 API adaptations
```

The two source sets are deliberately separate. 26.3 uses unobfuscated Mojang names and Java 25;
1.21.11 uses remapping Loom, official Mojang mappings and Java 21. Sharing Java sources between them
would add fragile preprocessing for a small amount of code, so the version-specific APIs are kept
explicit.

## 14. License

The repository is licensed under the MIT License; see [`LICENSE`](LICENSE). Both mod metadata files
declare `MIT`, matching that file. If you redistribute the mod or a modified version, keep the MIT
copyright and permission notice with it. Fabric API, Minecraft and their assets remain under their
own licenses; no Minecraft assets are redistributed in these JARs.

## 15. Terms of Service

Use of MapTooltip X is also described in [`TERMS.md`](TERMS.md), including third-party rules,
the warranty disclaimer and the limitation of liability. The MIT License remains the license for the
software and controls over any conflicting term; see [`LICENSE`](LICENSE).
