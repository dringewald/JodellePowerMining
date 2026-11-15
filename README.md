# ⚡ Jodelle Power Mining  
*A powerful tool enhancement plugin for Spigot servers!*  

[![Programmed in Java](https://img.shields.io/badge/Programmed%20in-Java-f89820)](https://www.java.com/de/) 
[![Programmed with VSCodium](https://img.shields.io/badge/Programmed%20with-VSCodium-2F80ED?logo=vscodium)](https://vscodium.com/) 
[![](https://img.shields.io/badge/BStats-Metrics-00695c)](https://bstats.org/plugin/bukkit/JodellePowerMining/24587)
[![GNU General Public License](https://img.shields.io/badge/⚖️%20License-GNU-yellow.svg)](https://www.gnu.org/licenses/gpl-3.0.html)<br/>
[![Created with ChatGPT](https://img.shields.io/badge/Created%20with-ChatGPT-10a37f?logo=openai)](https://openai.com/chatgpt)
[![Join us on Discord](https://img.shields.io/badge/Discord-Join%20Now-5865F2?logo=discord)](https://discord.gg/jymDumdFVU)
[![Donate on PayPal](https://img.shields.io/badge/Donate-PayPal-0070ba?logo=paypal)](https://www.paypal.com/paypalme/NoXHolt)
[![Steam Profile](https://img.shields.io/badge/Steam-View%20Profile-000000?logo=steam)](https://steamcommunity.com/id/nox_holt)

## 📌 Overview
**Jodelle Power Mining** is a **revived and enhanced** version of an old mining plugin originally created by **bloodyshade** (2013).

This plugin introduces **special PowerTools** that allow **efficient block breaking**, such as **3x3 mining with hammers and excavators**.  

🔹 **Mine faster.**  
🔹 **Dig smarter.**  
🔹 **Customize everything.**

💖 If you love this project, consider [donating](https://www.paypal.me/noxholt) to support further development!  

## 📖 Disclaimer
I did not originally create this plugin!  
I am just updating it because the **original creator** has abandoned it.  

I really **enjoyed** the plugin, so I decided to **update and improve it further**.

💡 **Original credits go to:**  
- **bloodyshade** – The original creator of the plugin
- **mgl23606** - For keeping the plugin uptodate until Minecraft 1.17

## 🛠️ Features
✅ **New Power Tools** – Includes **Hammer, Excavator, and Plow** for enhanced mining & digging.  
✅ **3x3 Mining & Digging** – Tools break multiple blocks at once (fully configurable).
✅ **Vanilla Block Lists** - Hammers and excavators break what pickaxes and shovels are meant for, or the blocks you list yourself.  
✅ **Protection Plugins** - Every extra block fires a normal block break, so Lands, WorldGuard, GriefPrevention, CoreProtect and Jobs apply.  
✅ **Configurable Recipes** – Fully customizable crafting recipes for PowerTools
✅ **Language support** – Full translation support
✅ **Permissions System** – Control **who** can use, craft, and enchant PowerTools.  
✅ **Multiple commands** – Check version, give PowerTools, reload config, set language, etc.  
✅ **WorldGuard Support** – Respects **protected areas** from the [WorldGuard plugin](https://dev.bukkit.org/projects/worldguard) (optional dependency).  
✅ **Jobs Support** – Block breaks of the tool are respecting Jobs of the [Job plugin](https://www.spigotmc.org/resources/jobs-reborn.4216/) (optional dependency).  
✅ **Enchantments Transfer** – Tools crafted with enchanted materials inherit enchantments.  

## 📸 Screenshots
### Hammer
![Hammer Usage](./github/img/Hammer.gif?raw=true)

### Excavator
![Excavator Usage](./github/img/Excavator.gif?raw=true)

### Plow
![Plow Usage](./github/img/Plow.gif?raw=true)
> Thanks to Tom from [NoX](https://discord.gg/jymDumdFVU) for these Screenshots

## 📥 Installation  
1. **Download** the latest `.jar` from [Releases](https://github.com/dringewald/JodellePowerMining/releases).  
2. **Place it** in the `plugins` folder of your **Spigot** server.  
3. **Restart** your server.  
4. **Configure** settings in the `config.yml` file if needed.  

## 🔧 Configuration

- With the overhaul of the plugin to Version 1.0, it **automatically migrates outdated configs** to the latest version.
  - ⚠️ If possible use a freshly generated config file
- The **config file now includes versioning** for future updates.
- The `Deep` option has been **renamed to `Depth`** (this is migrated automatically).
- The **language system** allows full translation of all messages, item names, and lore.
- 🔧 [Config file (config.yml)](./src/main/resources/config.yml)

## 📜 Commands
| **Command** | **Description** | **Permission** |
|------------|----------------|---------------|
| `/powermining` | Displays plugin information | `powermining.use.commands` |
| `/powermining help` | Shows the help menu | `powermining.use.commands` |
| `/powermining info` | Displays plugin info (same as `/powermining`) | `powermining.use.commands` |
| `/powermining version` | Shows the current plugin version | `powermining.version` |
| `/powermining give <tool>` | Gives a PowerTool to the player | `powermining.give` |
| `/powermining admin` | Shows the admin help menu | `powermining.admin` |
| `/powermining admin reload` | Reloads the plugin configuration and language files | `powermining.admin.reload` |
| `/powermining admin language <language>` | Changes the language file immediately | `powermining.admin.language` |
| `/powermining admin debug` | Toggles debug mode (Logs extra info, may spam console) | `powermining.admin.debug` |

## 🎨 Textures
The folder [resourcepack](./resourcepack) holds an own texture for every PowerTool, drawn by
[generate_textures.py](./resourcepack/generate_textures.py) (no Mojang assets, free to use and to change).

![Textures](./resourcepack/preview.png?raw=true)

The folder [resourcepack-hd](./resourcepack-hd) holds the same tools in 64x64, drawn by
[generate_hd_textures.py](./resourcepack/generate_hd_textures.py). Use one of the two packs, both use the same item names.

![Textures 64x64](./resourcepack-hd/preview.png?raw=true)

**With ItemsAdder or ResourcePackManager:** the plugin hands the 64x64 textures to them on every start
(`installTextures: true`). With ItemsAdder run `/iazip` once, ResourcePackManager merges the pack by itself.
Then go on with step 2.

1. Without these plugins: zip `pack.mcmeta` and `assets` of one pack as server resource pack, or merge the `assets`
   folder into the pack of another resource pack plugin.
2. Set `customItemModels: true` in the config and run `/powermining admin reload`.
3. Newly crafted PowerTools use the textures. Tools crafted before keep the vanilla look.

## 🔑 Permissions
You can find all Permissions [here](./github/docs/Permissions.md)

## **📌 Change Log**
### **🆕 Latest Updates - November 15, 2026 (v1.7)**
- **⛏️ Protection plugins** - Extra blocks are now broken as if the player mined them, so Lands, WorldGuard, GriefPrevention, CoreProtect and Jobs see every block. Drops, XP and tool damage follow vanilla. The plow asks protection plugins before tilling. `legacyMining: true` brings back the old way.
- **📋 Block lists** - Hammers and excavators now use the vanilla block tags, so new blocks work without a config change and the tool tier is checked like in vanilla. Blocks with an inventory are skipped. `customBlockList: true` keeps using the "Minable" and "Diggable" lists.
- **🟠 Copper tools** - Added the Copper Hammer, Copper Excavator and Copper Plow, with recipes, permissions and translations.
- **🔀 Toggle** - `/powermining toggle` turns area mining off and on for yourself. The choice survives restarts.
- **📏 Sizes per tool** - The new `toolSizes` section sets Radius and Depth for single PowerTools, for example 5x5 for the Netherite Hammer.
- **🌱 Plow and excavator** - Till coarse dirt, rooted dirt and paths, and make paths on dirt, podzol, mycelium, coarse and rooted dirt, as in vanilla. Farmland and paths now need air above.
- **🌾 Sickle** - New PowerTool made from a sword. Breaking a ripe crop harvests the ripe crops around it and plants them again, paid with one seed from the drops.
- **🔨 Netherite upgrade** - Diamond PowerTools can be upgraded at the smithing table like vanilla tools, enchantments stay.
- **🎒 Auto pickup** - Optional: drops go straight into the inventory (`autoPickup`).
- **🐢 Mining speed** - Optional factor for how fast PowerTools break the block you hit (`miningSpeed`).
- **🎨 Textures** - Own textures for every PowerTool in 16x16 (`resourcepack`) and 64x64 (`resourcepack-hd`), used with `customItemModels: true`. With ItemsAdder or ResourcePackManager the plugin installs the 64x64 textures by itself.
- **🌍 Disabled worlds** - PowerTools work like vanilla tools in the worlds listed in `disabledWorlds`.
- **📖 Recipe book** - Players get the recipes they may craft in their recipe book.
- **🚀 Releases** - Pushing a version tag builds the plugin and publishes a GitHub release.
- **🐞 Fixes** - Mining stops when the tool breaks, `/powermining admin reload` no longer keeps blocks removed from the lists, and the plugin no longer keeps data of players who left.
You'll find every changelog [here](./github/docs/Changelog.md)

## **🌎 Useful Links**
- 🔗 [BStats Page](https://bstats.org/plugin/bukkit/JodellePowerMining/24587)
- 🔗 [Spigot (since 1.21 (in progress))](.)  
- 🔗 [Spigot (until 1.17)](https://www.spigotmc.org/resources/jodelle-powermining.59834/)  
- 🔗 [Github (until 1.17)](https://github.com/mgl23606/JodellePowerMining/)  

## **🚀 Future Plans**
- Adding Coppertools
- Adding the newer blocks (1.21.11 up to 26.3, e.g. Cinnabar and Sulfur)
- Open to suggestions!  
- If you have feature requests, feel free to **open an issue**.

## **📢 Support & Contributions**
If you enjoy this plugin, please consider **[contributing](https://github.com/dringewald/JodellePowerMining/) or [donating](https://www.paypal.me/noxholt)** to help keep development active!  

⚡ **Supercharge your mining experience with Jodelle Power Mining!** ⚡  
