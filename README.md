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
  badlands_world:
    generator: VanillaPvPWorldGenerator:badlands
    biome-provider: VanillaPvPWorldGenerator:badlands
  snow_world:
    generator: VanillaPvPWorldGenerator:snow
    biome-provider: VanillaPvPWorldGenerator:snow
  mushroom_world:
    generator: VanillaPvPWorldGenerator:mushroom
    biome-provider: VanillaPvPWorldGenerator:mushroom
```


### Recommended Combinations
- Desert generator with Plains biome mode:
    - Creates desert-style terrain
    - Maintains grass block coloring from plains biome if you have any builds/structures
- Plains generator with Plains biome mode:
    - Creates plains-style terrain
    - Maintains grass block coloring from plains biome if you have any builds/structures
- Badlands generator with Badlands biome mode:
    - Creates badlands-style terrain
- Snow generator with Snow biome mode:
    - Creates snow-style terrain
- Mushroom generator with Mushroom biome mode:
    - Creates mushroom-style terrain

### Using with Multiverse for Biome and World Generation
If you're using Multiverse, follow these steps to generate worlds with the VanillaPvP generator and biome provider:

Creating a World with a Custom Generator
Run the following command to create a world using the plugin's custom generator:
```
/mv create <world_name> normal -g VanillaPvPWorldGenerator:<generator_type>
```
Replace <world_name> with your desired world name and <generator_type> with desert, plains, badlands, snow, or mushroom, depending on your preference.

Setting the Biome Provider
To specify the biome provider for the world, add this to your bukkit.yml under the worlds section (replace <world_name> with your world name):

```yaml
worlds:
  <world_name>:
    generator: VanillaPvPWorldGenerator:<generator_type>
    biome-provider: VanillaPvPWorldGenerator:plains
```
