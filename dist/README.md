# Prebuilt MapTooltip X JARs

These are the downloadable, client-only Fabric builds. Install **only the JAR matching your
Minecraft version**; both files use the mod ID `maptooltip-x`.

| Minecraft | Download | SHA-256 | Requirements |
|---|---|---|---|
| 26.3 | [maptooltip-x-1.0.0-26.3.jar](maptooltip-x-1.0.0-26.3.jar) | `c874d496b487ba44c53ef56a0be15275c247c29f82b5ecfce283b95769e0b633` | Java 25, Fabric Loader 0.20.1+, Fabric API 0.166.0+26.3 |
| 1.21.11 | [maptooltip-x-1.0.0-1.21.11.jar](maptooltip-x-1.0.0-1.21.11.jar) | `8f9d106b26633bdeb3bbf6b7f842a528fb01cf25f4e5177095514ce9627943f2` | Java 21, Fabric Loader 0.17.3+, Fabric API 0.141.6+1.21.11 |

Place the selected JAR in the instance's `mods` folder. These are the same binaries produced by
`./gradlew build` and `./gradlew -p 1.21.11 build`; the tracked copies are provided for direct
downloads and checksum verification. See the repository [README](../README.md) for controls,
Shulker Box Tooltip compatibility, build instructions and known limitations.

Both builds are client-side. The server does not need the mod and receives no custom packets.
