# Privacy Policy

**MapTooltip X**

Last updated: 22 November 2026

MapTooltip X is a client-only Fabric mod for Minecraft Java Edition. It is open source under the
MIT License. This policy explains what information the mod processes and what it does not collect.

## Summary

- **No telemetry, analytics, advertising, accounts or cloud sync.**
- **No personal data, server data or map data is collected, stored or transmitted by the mod.**
- **No custom packets and no HTTP, socket or other network code.**
- **No account, configuration or user-data files are created by the mod.**

## Information processed locally

While the game is running, MapTooltip X reads only the information needed to draw a tooltip:

- the current Shift and Ctrl key state through Minecraft's input helpers;
- the item stack being hovered and its data components, to identify a filled map and any nested
  container contents;
- the map data that Minecraft has already synchronized to this client.

This processing happens in memory for rendering. The author of MapTooltip X does not receive it, and
the mod does not send it to any server or third party.

MapTooltip X emits one fixed informational startup message through Minecraft's standard logger. The
message contains no player, server or map data; it is handled by Minecraft's existing logging.

## Network activity

MapTooltip X makes no network requests. It contains no HTTP client, socket code, custom packets or
background network activity, and it does not contact GitHub, Mojang, Fabric or any other service.

Minecraft, Fabric Loader, Fabric API, your launcher and any other mods you install have their own
network behavior and privacy policies. If you download a release from GitHub, GitHub processes that
download request under GitHub's own privacy statement; the mod itself does not receive information
from that request.

## Data sharing and retention

MapTooltip X has no data to share, sell or retain. It has no account system, no configuration
upload, no crash-report upload and no third-party service integration.

## Children's privacy

MapTooltip X does not knowingly collect personal information from anyone, including children.

## Changes to this policy

If this policy changes, the updated version will be committed to this repository and the date at
the top will be revised. The current version is always the one in the repository.

## Contact

For privacy questions, open an issue at
<https://github.com/notdeathlyatall/Map-Tooltip/issues>.
