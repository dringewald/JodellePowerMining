# Jodelle Power Mining

**Area mining tools for Spigot and Paper servers: hammers, excavators, plows and sickles.**

[![Programmed in Java](https://img.shields.io/badge/Programmed%20in-Java-f89820)](https://www.java.com/de/)
[![Programmed with VSCodium](https://img.shields.io/badge/Programmed%20with-VSCodium-2F80ED?logo=vscodium)](https://vscodium.com/)
[![bStats](https://img.shields.io/badge/BStats-Metrics-00695c)](https://bstats.org/plugin/bukkit/JodellePowerMining/24587)
[![GNU General Public License](https://img.shields.io/badge/⚖️%20License-GNU-yellow.svg)](https://www.gnu.org/licenses/gpl-3.0.html)<br/>
[![Join us on Discord](https://img.shields.io/badge/Discord-Join%20Now-5865F2?logo=discord)](https://discord.gg/jymDumdFVU)
[![Donate on PayPal](https://img.shields.io/badge/Donate-PayPal-0070ba?logo=paypal)](https://www.paypal.com/paypalme/NoXHolt)
[![Steam Profile](https://img.shields.io/badge/Steam-View%20Profile-000000?logo=steam)](https://steamcommunity.com/id/nox_holt)

Jodelle Power Mining adds PowerTools: upgraded versions of the vanilla pickaxe, shovel, hoe and sword that
work on a whole area instead of a single block. Every extra block is broken as if the player had mined it,
so protection, logging and job plugins keep working.

## Contents
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [PowerTools](#powertools)
- [Commands](#commands)
- [Configuration](#configuration)
- [Textures](#textures)
- [Integrations](#integrations)
- [Permissions](#permissions)
- [Changelog](#changelog)
- [Credits](#credits)
- [Support](#support)

## Features
- **Four tool types** in seven tiers each, from wood to netherite, including copper.
- **Area mining** with a configurable radius and depth, globally or per tool.
- **Vanilla behaviour:** block tags and tool tiers decide what a tool can break. Drops, XP, Fortune,
  Silk Touch, Unbreaking and tool damage work as with the vanilla tool.
- **Protection friendly:** extra blocks fire a normal block break event, so Lands, WorldGuard,
  GriefPrevention, CoreProtect and Jobs see every block.
- **Configurable recipes**, a netherite upgrade at the smithing table and anvil repairs.
- **Player toggle** to switch area mining off for yourself, plus worlds where PowerTools act like vanilla tools.
- **Optional extras:** auto pickup, a mining speed factor and recipe book unlocks.
- **Own textures** in 16x16 and 64x64, installed automatically with ItemsAdder or ResourcePackManager.
- **Full translation support** for messages, item names and lore (English and German included).
- **Automatic config migration** from older versions.

## Screenshots
| Hammer | Excavator | Plow |
|:---:|:---:|:---:|
| ![Hammer](./github/img/Hammer.gif?raw=true) | ![Excavator](./github/img/Excavator.gif?raw=true) | ![Plow](./github/img/Plow.gif?raw=true) |

> Screenshots by Tom from [NoX](https://discord.gg/jymDumdFVU).

## Requirements
| | |
|---|---|
| **Server** | Spigot, Paper or a fork, Minecraft 26.3 |
| **Java** | 25 |
| **Optional** | WorldGuard, Jobs Reborn, ItemsAdder, ResourcePackManager |

Older Minecraft versions may work but are not tested.

## Installation
1. Download the latest `.jar` from the [releases](https://github.com/dringewald/JodellePowerMining/releases).
2. Put it into the `plugins` folder of your server.
3. Restart the server.
4. Adjust `plugins/JodellePowerMining/config.yml` if needed and run `/powermining admin reload`.

## PowerTools
| Tool | Crafted from | What it does |
|---|---|---|
| **Hammer** | Pickaxe | Mines an area of pickaxe blocks such as stone, ores and deepslate. |
| **Excavator** | Shovel | Digs an area of shovel blocks such as dirt, sand and gravel. Right click makes paths. |
| **Plow** | Hoe | Tills an area of dirt and grass into farmland. |
| **Sickle** | Sword | Harvests the ripe crops around the one you break and replants them, paid with one seed from the drops. |

Each tool exists in wood, stone, copper, iron, gold, diamond and netherite. The default recipe surrounds the
vanilla tool with four of its material:

```
 . M .
 M T M      M = material (logs, stone, copper ingot, iron ingot, gold ingot, diamond, netherite ingot)
 . M .      T = vanilla tool of the same tier
```

Diamond PowerTools can also be upgraded to netherite at the smithing table. Hold sneak to use a PowerTool
like its vanilla tool for a single block.

## Commands
The alias `/jpm` works for every command.

| Command | Description | Permission |
|---|---|---|
| `/powermining` | Shows plugin information | `powermining.use.commands` |
| `/powermining help` | Shows the help menu | `powermining.use.commands` |
| `/powermining version` | Shows the installed version | `powermining.version` |
| `/powermining toggle` | Turns area mining on or off for yourself | `powermining.toggle` |
| `/powermining give <tool>` | Gives you a PowerTool | `powermining.give` |
| `/powermining admin` | Shows the admin help menu | `powermining.admin` |
| `/powermining admin reload` | Reloads the config and language files | `powermining.admin.reload` |
| `/powermining admin language <language>` | Switches the language | `powermining.admin.language` |
| `/powermining admin debug` | Toggles debug logging | `powermining.admin.debug` |

## Configuration
The [config.yml](./src/main/resources/config.yml) documents every option. The most important ones:

| Option | Default | Description |
|---|---|---|
| `language` | `en_US` | Language file to use |
| `Radius` / `Depth` | `2` / `1` | Size of the mining area (radius 2 is 3x3) |
| `toolSizes` | none | Radius and depth for single PowerTools, for example a 5x5 Netherite Hammer |
| `useDurabilityPerBlock` | `true` | Every broken block costs durability, not just the one you hit |
| `disabledWorlds` | none | Worlds where PowerTools act like vanilla tools |
| `autoPickup` | `false` | Drops go straight into the inventory |
| `miningSpeed` | `1.0` | Speed factor compared to the vanilla tool |
| `netheriteSmithing` | `true` | Allows the netherite upgrade at the smithing table |
| `unlockRecipes` | `true` | Adds the recipes a player may craft to the recipe book |
| `customItemModels` | `false` | PowerTools use their own textures from the resource pack |
| `installTextures` | `true` | Hands the textures to ItemsAdder or ResourcePackManager |
| `legacyMining` | `false` | Uses the old, faster way of breaking blocks without block break events |
| `customBlockList` | `false` | Uses the `Minable` and `Diggable` lists instead of the vanilla block tags |

Configs from older versions are migrated automatically on start. Recipes and XP drops can be changed in
the `recipes` and `xp-drops` sections.

## Textures
The plugin ships its own textures for every PowerTool, created with scripts in this repository and free
to use and change. No Mojang assets are included.

| 16x16 ([resourcepack](./resourcepack)) | 64x64 ([resourcepack-hd](./resourcepack-hd)) |
|:---:|:---:|
| ![Textures 16x16](./resourcepack/preview.png?raw=true) | ![Textures 64x64](./resourcepack-hd/preview.png?raw=true) |

**With ItemsAdder or ResourcePackManager** the plugin installs the 64x64 textures on every start:
- ItemsAdder: run `/iazip` once after the first start or an update.
- ResourcePackManager: the pack is merged and hosted automatically.

**Without these plugins** zip `pack.mcmeta` and the `assets` folder of one pack and use it as server
resource pack, or merge `assets` into the pack of another resource pack plugin.

Then set `customItemModels: true` and run `/powermining admin reload`. PowerTools crafted from then on use
the new textures. The textures are generated by [generate_textures.py](./resourcepack/generate_textures.py)
and [generate_hd_textures.py](./resourcepack/generate_hd_textures.py).

## Integrations
| Plugin | Support |
|---|---|
| [WorldGuard](https://dev.bukkit.org/projects/worldguard) | Respects protected regions |
| [Jobs Reborn](https://www.spigotmc.org/resources/jobs-reborn.4216/) | Every broken block counts for jobs |
| Lands, GriefPrevention, CoreProtect and others | Supported through normal block break events |
| ItemsAdder | Textures are installed into its contents folder |
| [ResourcePackManager](https://wiki.nightbreak.io/ResourcePackManager/info/) | Textures are registered through its API |

## Permissions
All permissions are listed in [Permissions.md](./github/docs/Permissions.md). Operators have everything by
default. To give players all PowerTools, grant `powermining.craft.all`, `powermining.use.all` and
`powermining.enchant.all`.

## Changelog
**Version 1.7** (November 15, 2026) brings event-based mining for protection plugins, vanilla block tags,
copper tools, the sickle, per-tool sizes, the player toggle, the netherite upgrade, auto pickup, own
textures with ItemsAdder and ResourcePackManager support, and several fixes.

The full history is in the [changelog](./github/docs/Changelog.MD).

## Credits
This plugin is a revived and extended version of the original PowerMining:
- **bloodyshade**: original author (2013)
- **mgl23606**: maintained the plugin up to Minecraft 1.17 ([repository](https://github.com/mgl23606/JodellePowerMining/))
- **Holt**: current maintainer

## Support
- Questions, bug reports and feature requests: [GitHub issues](https://github.com/dringewald/JodellePowerMining/issues) or [Discord](https://discord.gg/jymDumdFVU)
- Usage statistics: [bStats](https://bstats.org/plugin/bukkit/JodellePowerMining/24587)
- Older versions: [Spigot (up to 1.17)](https://www.spigotmc.org/resources/jodelle-powermining.59834/)

If you like the plugin, contributions and [donations](https://www.paypal.me/noxholt) help to keep it maintained.

## License
Licensed under the [GNU General Public License v3.0](./LICENSE).
