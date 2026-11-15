# 🔑 Permissions
Operators get every permission through `powermining.all`. Single tiers default to `false` and are granted
through the matching `.all` permission, for example `powermining.use.hammer.all` for every hammer.

## General Permissions
| **Permission** | **Description** | **Default** |
|--------------|----------------|------------|
| `powermining.all` | Grants all permissions | `op` |
| `powermining.give` | Allows receiving a PowerTool via `/powermining give <tool>` | `op` |
| `powermining.version` | Allows using `/powermining version` | `op` |
| `powermining.toggle` | Allows turning area mining on or off via `/powermining toggle` | `true` |

## Admin Permissions
| **Permission** | **Description** | **Default** |
|--------------|----------------|------------|
| `powermining.admin` | Grants all admin commands | `op` |
| `powermining.admin.help` | Allows using `/powermining admin help` | `op` |
| `powermining.admin.reload` | Allows using `/powermining admin reload` | `op` |
| `powermining.admin.language` | Allows using `/powermining admin language <language>` | `op` |
| `powermining.admin.debug` | Allows using `/powermining admin debug` | `op` |


## Crafting Permissions
| **Permission** | **Description** | **Default** |
|--------------|----------------|------------|
| `powermining.craft.all` | Allows crafting all PowerTools | `op` |
| `powermining.craft.hammer.all` | Allows crafting all Hammers | `op` |
| `powermining.craft.excavator.all` | Allows crafting all Excavators | `op` |
| `powermining.craft.plow.all` | Allows crafting all Plows | `op` |
| `powermining.craft.sickle.all` | Allows crafting all Sickles | `op` |

### Hammer Crafting
| **Permission** | **Description** | **Default** |
|--------------|----------------|------------|
| `powermining.craft.hammer.wooden` | Allows crafting the Wooden Hammer | `false` |
| `powermining.craft.hammer.stone` | Allows crafting the Stone Hammer | `false` |
| `powermining.craft.hammer.iron` | Allows crafting the Iron Hammer | `false` |
| `powermining.craft.hammer.golden` | Allows crafting the Golden Hammer | `false` |
| `powermining.craft.hammer.diamond` | Allows crafting the Diamond Hammer | `false` |
| `powermining.craft.hammer.netherite` | Allows crafting the Netherite Hammer | `false` |
| `powermining.craft.hammer.copper` | Allows crafting the Copper Hammer | `false` |

### Excavator Crafting
| **Permission** | **Description** | **Default** |
|--------------|----------------|------------|
| `powermining.craft.excavator.wooden` | Allows crafting the Wooden Excavator | `false` |
| `powermining.craft.excavator.stone` | Allows crafting the Stone Excavator | `false` |
| `powermining.craft.excavator.iron` | Allows crafting the Iron Excavator | `false` |
| `powermining.craft.excavator.golden` | Allows crafting the Golden Excavator | `false` |
| `powermining.craft.excavator.diamond` | Allows crafting the Diamond Excavator | `false` |
| `powermining.craft.excavator.netherite` | Allows crafting the Netherite Excavator | `false` |
| `powermining.craft.excavator.copper` | Allows crafting the Copper Excavator | `false` |

### Plow Crafting
| **Permission** | **Description** | **Default** |
|--------------|----------------|------------|
| `powermining.craft.plow.wooden` | Allows crafting the Wooden Plow | `false` |
| `powermining.craft.plow.stone` | Allows crafting the Stone Plow | `false` |
| `powermining.craft.plow.iron` | Allows crafting the Iron Plow | `false` |
| `powermining.craft.plow.golden` | Allows crafting the Golden Plow | `false` |
| `powermining.craft.plow.diamond` | Allows crafting the Diamond Plow | `false` |
| `powermining.craft.plow.netherite` | Allows crafting the Netherite Plow | `false` |
| `powermining.craft.plow.copper` | Allows crafting the Copper Plow | `false` |

### Sickle Crafting
| **Permission** | **Description** | **Default** |
|--------------|----------------|------------|
| `powermining.craft.sickle.wooden` | Allows crafting the Wooden Sickle | `false` |
| `powermining.craft.sickle.stone` | Allows crafting the Stone Sickle | `false` |
| `powermining.craft.sickle.iron` | Allows crafting the Iron Sickle | `false` |
| `powermining.craft.sickle.golden` | Allows crafting the Golden Sickle | `false` |
| `powermining.craft.sickle.diamond` | Allows crafting the Diamond Sickle | `false` |
| `powermining.craft.sickle.netherite` | Allows crafting the Netherite Sickle | `false` |
| `powermining.craft.sickle.copper` | Allows crafting the Copper Sickle | `false` |

## Usage Permissions
| **Permission** | **Description** | **Default** |
|--------------|----------------|------------|
| `powermining.use.all` | Allows using all PowerTools | `op` |
| `powermining.use.commands` | Allows using relevant commands | `op` |
| `powermining.use.hammer.all` | Allows using all Hammers | `op` |
| `powermining.use.excavator.all` | Allows using all Excavators | `op` |
| `powermining.use.plow.all` | Allows using all Plows | `op` |
| `powermining.use.sickle.all` | Allows using all Sickles | `op` |

### Hammer Usage
| **Permission** | **Description** | **Default** |
|--------------|----------------|------------|
| `powermining.use.hammer.wooden` | Allows using the Wooden Hammer | `false` |
| `powermining.use.hammer.stone` | Allows using the Stone Hammer | `false` |
| `powermining.use.hammer.iron` | Allows using the Iron Hammer | `false` |
| `powermining.use.hammer.golden` | Allows using the Golden Hammer | `false` |
| `powermining.use.hammer.diamond` | Allows using the Diamond Hammer | `false` |
| `powermining.use.hammer.netherite` | Allows using the Netherite Hammer | `false` |
| `powermining.use.hammer.copper` | Allows using the Copper Hammer | `false` |

### Excavator Usage
| **Permission** | **Description** | **Default** |
|--------------|----------------|------------|
| `powermining.use.excavator.wooden` | Allows using the Wooden Excavator | `false` |
| `powermining.use.excavator.stone` | Allows using the Stone Excavator | `false` |
| `powermining.use.excavator.iron` | Allows using the Iron Excavator | `false` |
| `powermining.use.excavator.golden` | Allows using the Golden Excavator | `false` |
| `powermining.use.excavator.diamond` | Allows using the Diamond Excavator | `false` |
| `powermining.use.excavator.netherite` | Allows using the Netherite Excavator | `false` |
| `powermining.use.excavator.copper` | Allows using the Copper Excavator | `false` |

### Plow Usage
| **Permission** | **Description** | **Default** |
|--------------|----------------|------------|
| `powermining.use.plow.wooden` | Allows using the Wooden Plow | `false` |
| `powermining.use.plow.stone` | Allows using the Stone Plow | `false` |
| `powermining.use.plow.iron` | Allows using the Iron Plow | `false` |
| `powermining.use.plow.golden` | Allows using the Golden Plow | `false` |
| `powermining.use.plow.diamond` | Allows using the Diamond Plow | `false` |
| `powermining.use.plow.netherite` | Allows using the Netherite Plow | `false` |
| `powermining.use.plow.copper` | Allows using the Copper Plow | `false` |

### Sickle Usage
| **Permission** | **Description** | **Default** |
|--------------|----------------|------------|
| `powermining.use.sickle.wooden` | Allows using the Wooden Sickle | `false` |
| `powermining.use.sickle.stone` | Allows using the Stone Sickle | `false` |
| `powermining.use.sickle.iron` | Allows using the Iron Sickle | `false` |
| `powermining.use.sickle.golden` | Allows using the Golden Sickle | `false` |
| `powermining.use.sickle.diamond` | Allows using the Diamond Sickle | `false` |
| `powermining.use.sickle.netherite` | Allows using the Netherite Sickle | `false` |
| `powermining.use.sickle.copper` | Allows using the Copper Sickle | `false` |

## Enchantment Permissions
| **Permission** | **Description** | **Default** |
|--------------|----------------|------------|
| `powermining.enchant.all` | Allows enchanting all PowerTools | `op` |
| `powermining.enchant.hammer.all` | Allows enchanting all Hammers | `op` |
| `powermining.enchant.excavator.all` | Allows enchanting all Excavators | `op` |
| `powermining.enchant.plow.all` | Allows enchanting all Plows | `op` |
| `powermining.enchant.sickle.all` | Allows enchanting all Sickles | `op` |

### Hammer Enchanting
| **Permission** | **Description** | **Default** |
|--------------|----------------|------------|
| `powermining.enchant.hammer.wooden` | Allows enchanting the Wooden Hammer | `false` |
| `powermining.enchant.hammer.stone` | Allows enchanting the Stone Hammer | `false` |
| `powermining.enchant.hammer.iron` | Allows enchanting the Iron Hammer | `false` |
| `powermining.enchant.hammer.golden` | Allows enchanting the Golden Hammer | `false` |
| `powermining.enchant.hammer.diamond` | Allows enchanting the Diamond Hammer | `false` |
| `powermining.enchant.hammer.netherite` | Allows enchanting the Netherite Hammer | `false` |
| `powermining.enchant.hammer.copper` | Allows enchanting the Copper Hammer | `false` |

### Excavator Enchanting
| **Permission** | **Description** | **Default** |
|--------------|----------------|------------|
| `powermining.enchant.excavator.wooden` | Allows enchanting the Wooden Excavator | `false` |
| `powermining.enchant.excavator.stone` | Allows enchanting the Stone Excavator | `false` |
| `powermining.enchant.excavator.iron` | Allows enchanting the Iron Excavator | `false` |
| `powermining.enchant.excavator.golden` | Allows enchanting the Golden Excavator | `false` |
| `powermining.enchant.excavator.diamond` | Allows enchanting the Diamond Excavator | `false` |
| `powermining.enchant.excavator.netherite` | Allows enchanting the Netherite Excavator | `false` |
| `powermining.enchant.excavator.copper` | Allows enchanting the Copper Excavator | `false` |

### Plow Enchanting
| **Permission** | **Description** | **Default** |
|--------------|----------------|------------|
| `powermining.enchant.plow.wooden` | Allows enchanting the Wooden Plow | `false` |
| `powermining.enchant.plow.stone` | Allows enchanting the Stone Plow | `false` |
| `powermining.enchant.plow.iron` | Allows enchanting the Iron Plow | `false` |
| `powermining.enchant.plow.golden` | Allows enchanting the Golden Plow | `false` |
| `powermining.enchant.plow.diamond` | Allows enchanting the Diamond Plow | `false` |
| `powermining.enchant.plow.netherite` | Allows enchanting the Netherite Plow | `false` |
| `powermining.enchant.plow.copper` | Allows enchanting the Copper Plow | `false` |

### Sickle Enchanting
| **Permission** | **Description** | **Default** |
|--------------|----------------|------------|
| `powermining.enchant.sickle.wooden` | Allows enchanting the Wooden Sickle | `false` |
| `powermining.enchant.sickle.stone` | Allows enchanting the Stone Sickle | `false` |
| `powermining.enchant.sickle.iron` | Allows enchanting the Iron Sickle | `false` |
| `powermining.enchant.sickle.golden` | Allows enchanting the Golden Sickle | `false` |
| `powermining.enchant.sickle.diamond` | Allows enchanting the Diamond Sickle | `false` |
| `powermining.enchant.sickle.netherite` | Allows enchanting the Netherite Sickle | `false` |
| `powermining.enchant.sickle.copper` | Allows enchanting the Copper Sickle | `false` |
