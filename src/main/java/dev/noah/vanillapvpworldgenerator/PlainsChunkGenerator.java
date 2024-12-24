package dev.noah.vanillapvpworldgenerator;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import java.util.Random;

public class PlainsChunkGenerator extends ChunkGenerator {
    private final FastNoiseLite terrainNoise = new FastNoiseLite(1337);
    private final FastNoiseLite terrainNoise2 = new FastNoiseLite(1337 * 1337);
    private final FastNoiseLite detailNoise = new FastNoiseLite((int) (1337 * 3.1415926));

    public PlainsChunkGenerator() {
        // Adjust frequencies as desired
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

        // 1) Randomly place Bedrock in the first 4 layers, fallback is Deepslate
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
        int yDeepslateStart = chunkData.getMinHeight() + 4;
        int yDeepslateEnd   = Math.min(40, chunkData.getMaxHeight()); // clamp to chunk’s max height
        if (yDeepslateEnd > yDeepslateStart) {
            chunkData.setRegion(
                    0, yDeepslateStart, 0,
                    16, yDeepslateEnd, 16,
                    Material.DEEPSLATE
            );
        }

        // 3) For each column, place stone, dirt, and then grass on top
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                // Calculate terrain height using noise
                float noiseValue = (terrainNoise.GetNoise(x + (chunkX * 16), z + (chunkZ * 16)) * 10)
                        + (terrainNoise2.GetNoise(x + (chunkX * 16), z + (chunkZ * 16)) * 5);
                int stoneDepth = 10 + (int) noiseValue; // how tall the stone extends above y=40

                // Start layering at Y=40
                int baseY = 40;
                int maxY  = chunkData.getMaxHeight();

                // Stone region
                int stoneTop = baseY + stoneDepth;
                if (stoneTop > maxY) {
                    stoneTop = maxY;
                }
                if (stoneTop > baseY) {
                    chunkData.setRegion(x, baseY, z, x + 1, stoneTop, z + 1, Material.STONE);
                }

                // Dirt region: 2 blocks
                int dirtBottom = stoneTop;
                int dirtTop    = dirtBottom + 2;
                if (dirtTop > maxY) {
                    dirtTop = maxY;
                }
                if (dirtTop > dirtBottom) {
                    chunkData.setRegion(x, dirtBottom, z, x + 1, dirtTop, z + 1, Material.DIRT);
                }

                // Grass region: 1 block
                int grassBottom = dirtTop;
                int grassTop    = grassBottom + 1;
                if (grassTop > maxY) {
                    grassTop = maxY;
                }
                if (grassTop > grassBottom) {
                    chunkData.setRegion(x, grassBottom, z, x + 1, grassTop, z + 1, Material.GRASS_BLOCK);
                }

                // 4) Possibly place tall grass or flowers on top
                int surfaceHeight = grassTop - 1; // The top-most grass block is at grassTop-1
                if (surfaceHeight < maxY - 1 && surfaceHeight >= chunkData.getMinHeight()) {
                    double detailValue = detailNoise.GetNoise(x + (chunkX * 16), z + (chunkZ * 16));

                    // Attempt tall grass
                    if (detailValue > 0.8 && chunkRandom.nextDouble() < 0.1) {
                        chunkData.setBlock(x, surfaceHeight + 1, z, Material.GRASS);
                    }
                    // Attempt a flower
                    else if (detailValue < -0.8 && chunkRandom.nextDouble() < 0.05) {
                        // Randomly pick a flower
                        Material flower = (chunkRandom.nextBoolean() ? Material.POPPY : Material.DANDELION);
                        chunkData.setBlock(x, surfaceHeight + 1, z, flower);
                    }
                }
            }
        }
    }


}
