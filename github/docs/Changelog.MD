# **📌 Changelog**
## **🆕 Latest Updates - November 15, 2026 (v1.7)**
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

## **📌 Previous Updates**
### 📅 **November 15, 2026 (v1.6)**
- **🔄 Updated plugin to Minecraft 26.3** - Built against the Spigot API 26.3. Earlier versions are not supported (should work, but untested).
- **☕ Java 25** - The plugin now needs Java 25, like Minecraft 26.x itself.
- **📦 Dependencies** - Updated WorldGuard, bStats and the bundled libraries.

### 📅 **January 29, 2026 (v1.5)**
- **🔄 Updated plugin to fully work in Minecraft 1.21.11** - Earlier versions are not supported (should work, but untested).
- **📑 Docs** Added JavaDocs for Developers.
- **♻️ Updater (BETA)** Removed Spigot-Updater and added an untested Github Updater for the plugin.
- **🛠️ Durability** - Fixed durability config options, so durability will be correctly reduced.

### 📅 **Sep 21, 2025 (v1.4)**
- **🔄 Updated plugin to fully work in Minecraft 1.21.8** - Earlier versions are not supported (should work, but untested).

### 📅 **May 7, 2025 (v1.3)**
- **♻️ Updater** Added "unfinished" Updater
- **🔄 Updated plugin to fully work in Minecraft 1.21.5** - Earlier versions are not supported (should work, but untested).

### 📅 **February 24, 2025 (v1.2)**
- **💠 XP Orbs** - Made a config setting for XP drops
- **🐞 Silk Touch** - Fixed a bug where silk touch still gave XP drops.
- **🛠️ Config Migrator** - Fixed config migrator accidentally deleting recipes.

### 📅 **February 16, 2025**
- **💼 Jobs** - Fixed plugin not working, when [Jobs Reborn](https://www.spigotmc.org/resources/jobs-reborn.4216/) wasn't installed.
- **🔨 Anvils** - Fixed Anvils not adding durability when the Item with lower durability was on the right side. 
- **✨ Enchantments** - Fixed Unbreaking enchantment not working correctly.
- **🛠️ Durability** - Fixed Items with enchantments not breaking correctly.

### 📅 **February 15, 2025**
- **💼 Jobs support** - Added support for [Jobs Reborn](https://www.spigotmc.org/resources/jobs-reborn.4216/).

### 📅 **January 29, 2025**
- **🔄 Updated plugin to fully work in Minecraft 1.21.4** - Earlier versions are not supported (should work, but untested).
- **🔤 Added full Language system** – Everything is translatable (messages, items, commands).
- **🔨 Added Anvil support** Let's you repair and fix your PowerTool without losing your precious resources.
- **🛠️ Added admin commands** – `reload`, `language`, `debug`, etc.
- **🪨 Added missing blocks up to Minecraft 1.21.4**.
- **📁 Introduced Config Versioning** – For future automatic updates.
- **🔄 Automatic Config Migration** - Works, but if possible try to use the new config file
  - **Added:** `language` (default: `en_US`).
  - **Added:** `debug` (default: `false`).
  - **Renamed** `Deep` → `Depth` (value is kept the same).
- **🐞 Fixed a bug where XP orbs did not drop when using a PowerTool.**
- **🧹 Removed outdated dependencies**.

### 📅 **June 14, 2021**
- Fixed **Plow permissions**.
- Verified that all permissions work as expected.

### 📅 **June 8, 2021**
- **Added WorldGuard support** (Optional dependency).
- Now **checks each block before breaking** to prevent griefing.
- Updated dependencies in `pom.xml`.

### 📅 **June 6, 2021**
- **Implemented item durability logic** – PowerTools now break correctly when they reach zero durability.
- **Added breaking sound effects** when a tool breaks.

### 📅 **June 3, 2021**
- **Added new commands:** `/jpm give`, `/jpm version`
- **Enchantment Transfer** – Enchantments from crafting materials now **transfer to the PowerTool**.
- **Max stack size validation for crafting** – Prevents unrealistic recipes (e.g., stacking multiple pickaxes in one slot).
- **Code optimizations** for better performance.

### 📅 **May 26, 2021**
- **Refactored permission handling**
  - Uses **HashMaps** for permission storage instead of hardcoding them in each method.
  - Improves performance and maintainability.

### 📅 **May 25, 2021**
- **Fixed durability loss bug** – PowerTools now lose durability correctly.
- **Implemented Unbreaking enchantment logic**.
- **Improved BlockBreakListener**:
  - `breakNaturally()` now properly drops items.
  - **Fortune enchantment now works correctly** with PowerTools.
  - **Plow logic cleaned up** for better performance.
