package dev.noah.vanillapvpworldgenerator;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import java.util.Random;

public class CustomChunkGenerator extends ChunkGenerator {
    private final FastNoiseLite terrainNoise = new FastNoiseLite(1337);
    private final FastNoiseLite terrainNoise2 = new FastNoiseLite(1337 * 1337);
    private final FastNoiseLite detailNoise = new FastNoiseLite((int) (1337 * 3.1415926));

    public CustomChunkGenerator() {
        // Set frequencies, lower frequency = slower change.
        detailNoise.SetFrequency(0.05f);

        terrainNoise.SetFrequency(0.014f);
        terrainNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        terrainNoise2.SetFrequency(0.007f);
        terrainNoise2.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
    }

    @Override
    public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        // Create a deterministic random object for this chunk
        long chunkSeed = (chunkX * 341873128712L + chunkZ * 132897987541L);
        Random chunkRandom = new Random(chunkSeed);

        double percentBedrock = 1;
        for (int y = chunkData.getMinHeight(); y < chunkData.getMinHeight() + 4; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    if (chunkRandom.nextDouble() < percentBedrock) {
                        chunkData.setBlock(x, y, z, Material.BEDROCK);
                    } else {
                        chunkData.setBlock(x, y, z, Material.DEEPSLATE);
                    }
                }
            }
            percentBedrock -= 0.2;
        }

        for (int y = chunkData.getMinHeight() + 4; y < 40; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    chunkData.setBlock(x, y, z, Material.DEEPSLATE);
                }
            }
        }

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                float noiseValue = (terrainNoise.GetNoise(x + (chunkX * 16), z + (chunkZ * 16)) * 10) +
                        (terrainNoise2.GetNoise(x + (chunkX * 16), z + (chunkZ * 16)) * 5);
                int stoneDepth = 10 + (int) noiseValue; // Variable depth for stone

                for (int y = 40; y < 40 + stoneDepth && y < chunkData.getMaxHeight(); y++) {
                    chunkData.setBlock(x, y, z, Material.STONE);
                }

                for (int y = 40 + stoneDepth; y < 40 + stoneDepth + 2 && y < chunkData.getMaxHeight(); y++) {
                    chunkData.setBlock(x, y, z, Material.SANDSTONE);
                }

                for (int y = 40 + stoneDepth + 2; y < 40 + stoneDepth + 5 && y < chunkData.getMaxHeight(); y++) {
                    chunkData.setBlock(x, y, z, Material.SAND);
                }

                int surfaceHeight = 40 + stoneDepth + 4; // Top layer height

                // Add occasional cacti and dead bushes
                if (surfaceHeight < chunkData.getMaxHeight() - 1) {
                    double detailNoiseValue = detailNoise.GetNoise(x + (chunkX * 16), z + (chunkZ * 16));

                    if (detailNoiseValue > 0.9 && chunkRandom.nextDouble() < 0.1) { // Rarer chance for cactus
                        int cactusHeight = 1; // Default cactus height
                        if (chunkRandom.nextDouble() < 0.3) { // 30% chance for a taller cactus
                            cactusHeight = 2;
                        } else if (chunkRandom.nextDouble() < 0.1) { // 10% chance for the tallest cactus
                            cactusHeight = 3;
                        }

                        boolean canPlaceCactus = true;
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                if (dx == 0 && dz == 0) continue;
                                if (chunkData.getType(x + dx, surfaceHeight + 1, z + dz) != Material.AIR) {
                                    canPlaceCactus = false;
                                    break;
                                }
                            }
                            if (!canPlaceCactus) break;
                        }

                        if (canPlaceCactus) {
                            for (int h = 0; h < cactusHeight && surfaceHeight + 1 + h < chunkData.getMaxHeight(); h++) {
                                chunkData.setBlock(x, surfaceHeight + 1 + h, z, Material.CACTUS);
                            }
                        }
                    } else if (detailNoiseValue < -0.8 && chunkRandom.nextDouble() < 0.05) { // 5% chance for a dead bush
                        chunkData.setBlock(x, surfaceHeight + 1, z, Material.DEAD_BUSH);
                    }
                }
            }
        }
    }

}
