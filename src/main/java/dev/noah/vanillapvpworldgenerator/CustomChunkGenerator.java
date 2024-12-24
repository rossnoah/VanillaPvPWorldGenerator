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
        // Set frequencies
        detailNoise.SetFrequency(0.05f);

        terrainNoise.SetFrequency(0.014f);
        terrainNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);

        terrainNoise2.SetFrequency(0.007f);
        terrainNoise2.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
    }

    @Override
    public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        // Create a deterministic random for bedrock placement in this chunk
        long chunkSeed = (chunkX * 341873128712L + chunkZ * 132897987541L);
        Random chunkRandom = new Random(chunkSeed);

        // 1) Randomly place Bedrock in first 4 layers, fallback is Deepslate
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

        // 2) From bedrock layer up to Y=40 is uniformly Deepslate
        //    Instead of loops: setRegion( xMin, yMin, zMin, xMax, yMax, zMax, material )
        //    Remember yMax is exclusive.
        int yDeepslateStart = chunkData.getMinHeight() + 4;
        int yDeepslateEnd   = Math.min(40, chunkData.getMaxHeight()); // clamp to chunk’s max height

        if (yDeepslateEnd > yDeepslateStart) {
            // x from 0..16, y from yDeepslateStart..yDeepslateEnd, z from 0..16
            chunkData.setRegion(
                    0, yDeepslateStart, 0,
                    16, yDeepslateEnd, 16,
                    Material.DEEPSLATE
            );
        }

        // 3) For each column, place stone / sandstone / sand
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                // Calculate how "tall" the stone should go in this column
                float noiseValue = (terrainNoise.GetNoise(x + (chunkX * 16), z + (chunkZ * 16)) * 10)
                        + (terrainNoise2.GetNoise(x + (chunkX * 16), z + (chunkZ * 16)) * 5);
                int stoneDepth = 10 + (int) noiseValue; // variable depth for stone

                // We'll start layering at Y=40
                int baseY = 40;
                int maxY  = chunkData.getMaxHeight();

                // Stone region: Y in [baseY, baseY + stoneDepth)
                int stoneTop = baseY + stoneDepth;
                if (stoneTop > maxY) {
                    stoneTop = maxY;
                }
                if (stoneTop > baseY) {
                    chunkData.setRegion(x, baseY, z, x + 1, stoneTop, z + 1, Material.STONE);
                }

                // Sandstone region: Y in [stoneTop, stoneTop + 2)
                int sandstoneBottom = stoneTop;
                int sandstoneTop    = sandstoneBottom + 2;
                if (sandstoneTop > maxY) {
                    sandstoneTop = maxY;
                }
                if (sandstoneTop > sandstoneBottom) {
                    chunkData.setRegion(x, sandstoneBottom, z, x + 1, sandstoneTop, z + 1, Material.SANDSTONE);
                }

                // Sand region: Y in [sandstoneTop, sandstoneTop + 3)
                // Original code used +5, but typically that was 2 for sandstone + 3 for sand = total +5 from stoneTop
                int sandBottom = sandstoneTop;
                int sandTop    = sandBottom + 3;
                if (sandTop > maxY) {
                    sandTop = maxY;
                }
                if (sandTop > sandBottom) {
                    chunkData.setRegion(x, sandBottom, z, x + 1, sandTop, z + 1, Material.SAND);
                }

                // 4) Possibly place cactus or dead bush on the top-most sand block
                int surfaceHeight = sandTop - 1; // The top-most sand is at sandTop-1
                // Make sure surfaceHeight + 1 is within max height for plants
                if (surfaceHeight < maxY - 1 && surfaceHeight >= chunkData.getMinHeight()) {
                    double detailValue = detailNoise.GetNoise(x + (chunkX * 16), z + (chunkZ * 16));

                    // Attempt cactus
                    if (detailValue > 0.9 && chunkRandom.nextDouble() < 0.1) {
                        // 10% chance for a cactus in the right noise region
                        int cactusHeight = 1;
                        if (chunkRandom.nextDouble() < 0.3) {
                            cactusHeight = 2;
                        } else if (chunkRandom.nextDouble() < 0.1) {
                            cactusHeight = 3;
                        }

                        // Check adjacency
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

                        // Place cactus blocks if possible
                        if (canPlaceCactus) {
                            for (int h = 0; h < cactusHeight && (surfaceHeight + 1 + h) < maxY; h++) {
                                chunkData.setBlock(x, surfaceHeight + 1 + h, z, Material.CACTUS);
                            }
                        }
                    }
                    // Attempt dead bush
                    else if (detailValue < -0.8 && chunkRandom.nextDouble() < 0.05) {
                        chunkData.setBlock(x, surfaceHeight + 1, z, Material.DEAD_BUSH);
                    }
                }
            }
        }
    }
}
