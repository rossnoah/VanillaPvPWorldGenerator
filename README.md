# VanillaPvP World Generator

## Installation
1. Place plugin JAR in `plugins` directory
2. Stop server
3. Configure `bukkit.yml`
4. Start server

## Configuration

### Bukkit Configuration
To enable the custom generation you need to paste this worlds section into the bottom of your `bukkit.yml`. Then adjust as needed to match your world names:
```yaml
worlds:
  world:
    generator: VanillaPvPWorldGenerator:desert
    biome-provider: VanillaPvPWorldGenerator:plains
  plains_world:
    generator: VanillaPvPWorldGenerator:plains
    biome-provider: VanillaPvPWorldGenerator:plains
```


### Recommended Combinations
- Desert generator with Plains biome mode:
    - Creates desert-style terrain
    - Maintains grass block coloring from plains biome if you have any builds/structures
- Plains generator with Plains biome mode:
    - Creates plains-style terrain
    - Maintains grass block coloring from plains biome if you have any builds/structures