package dev.noah.vanillapvpworldgenerator;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class VanillaPvPWorldGenerator extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().log(Level.INFO, "WorldGenerator was enabled successfully.");
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "WorldGenerator was disabled successfully.");
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        getLogger().log(Level.WARNING, "CustomChunkGenerator is used!");
        return new CustomChunkGenerator(); // Return an instance of the chunk generator we want to use.
    }
}
