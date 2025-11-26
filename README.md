<div align="center">

![icon](https://cdn.modrinth.com/data/cached_images/273e3cabf4488b7377b86142a82f747c97bd728b.png)

[![publish](https://github.com/Than00ber/WellfedMod/actions/workflows/publish.yml/badge.svg)](https://github.com/Than00ber/WellfedMod/actions/workflows/publish.yml)

# WellFed - Valheim-Style Food System for Minecraft

This mod overhauls how eating works, encouraging players to think more about what food choices they make. Heavily inspired by Valheim's food system.

</div>

## Configuration

### Common

- `foodHeartsMultiplier`: Multiplier for health benefits from food (default: 1.0)
- `foodDurationMultiplier`: Multiplier for food effect durations (default: 1.0)

### Client

- `foodBarOffsetX`: Horizontal offset for the food display UI (default: 0)
- `foodBarOffsetY`: Vertical offset for the food display UI (default: 0)

## Game Rules

The mod adds several game rules for server-wide customization:

| Game Rule                         | Default | Description                                       |
| --------------------------------- | ------- | ------------------------------------------------- |
| `wellfed:playerStartingHearts`    | 20      | Hearts players start with when joining/respawning |
| `wellfed:maxConsumableFood`       | 3       | Maximum number of different foods active at once  |
| `wellfed:allowEatingTheSameItem`  | false   | Whether players can eat multiple of the same food |
| `wellfed:foodItemStacks`          | true    | Whether same food items tick down simultaneously  |
| `wellfed:hungerFoodDrain`         | 2       | How much hunger affects food exhaustion           |
| `wellfed:regenHealthTickInterval` | 60      | Ticks between natural health regeneration         |
| `wellfed:regenHealthFoodDrain`    | 3       | Food drained each time health regenerates         |
