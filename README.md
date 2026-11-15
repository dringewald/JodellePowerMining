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
✅ **Customizable Blocks** – Choose which blocks can be mined or dug using PowerTools.  
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

## 🔑 Permissions
You can find all Permissions [here](./github/docs/Permissions.md)

## **📌 Change Log**
### **🆕 Latest Updates - November 15, 2026 (v1.6)**
- **🔄 Updated plugin to Minecraft 26.3** - Built against the Spigot API 26.3. Earlier versions are not supported (should work, but untested).
- **☕ Java 25** - The plugin now needs Java 25, like Minecraft 26.x itself.
- **📦 Dependencies** - Updated WorldGuard, bStats and the bundled libraries.
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
