package dev.noah.vanillapvpworldgenerator;

import org.bukkit.Bukkit;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class VanillaPvPWorldGenerator extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("WorldGenerator was enabled successfully.");
    }

    @Override
    public void onDisable() {
        getLogger().info("WorldGenerator was disabled successfully.");
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        Bukkit.getLogger().info("Custom world gen called using id: " + id + " for world: " + worldName);
//        return new DesertChunkGenerator();
        switch (id) {
            case "plains":
                return new PlainsChunkGenerator();
            case "desert":
                return new DesertChunkGenerator();
            case "badlands":
                return new BadlandsChunkGenerator();
            case "snow":
                return new SnowChunkGenerator();
            case "mushroom":
                return new MushroomChunkGenerator();
            default:
                return null;
        }
    }

    @Override
    public BiomeProvider getDefaultBiomeProvider(String worldName, String id) {

        switch (id) {
            case "plains":
                return new PlainsWorldBiome();
            case "desert":
                return new DesertWorldBiome();
            case "badlands":
                return new BadlandsWorldBiome();
            case "snow":
                return new SnowWorldBiome();
            case "mushroom":
                return new MushroomWorldBiome();
            default:
                return null;
        }
    }
}
