package dev.noah.vanillapvpworldgenerator;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import java.util.Random;

public class MushroomChunkGenerator extends ChunkGenerator {
    private final FastNoiseLite terrainNoise = new FastNoiseLite(1337);
    private final FastNoiseLite terrainNoise2 = new FastNoiseLite(1337 * 1337);
    private final FastNoiseLite detailNoise = new FastNoiseLite((int) (1337 * 3.1415926));

    public MushroomChunkGenerator() {
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
        int yDeepslateStart = chunkData.getMinHeight() + 4;
        int yDeepslateEnd   = Math.min(40, chunkData.getMaxHeight());

        if (yDeepslateEnd > yDeepslateStart) {
            chunkData.setRegion(
                    0, yDeepslateStart, 0,
                    16, yDeepslateEnd, 16,
                    Material.DEEPSLATE
            );
        }

        // 3) For each column, place stone / mycelium
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                float noiseValue = (terrainNoise.GetNoise(x + (chunkX * 16), z + (chunkZ * 16)) * 10)
                        + (terrainNoise2.GetNoise(x + (chunkX * 16), z + (chunkZ * 16)) * 5);
                int stoneDepth = 10 + (int) noiseValue;

                int baseY = 40;
                int maxY  = chunkData.getMaxHeight();

                int stoneTop = baseY + stoneDepth;
                if (stoneTop > maxY) {
                    stoneTop = maxY;
                }
                if (stoneTop > baseY) {
                    chunkData.setRegion(x, baseY, z, x + 1, stoneTop, z + 1, Material.STONE);
                }

                int myceliumBottom = stoneTop;
                int myceliumTop    = myceliumBottom + 5;
                if (myceliumTop > maxY) {
                    myceliumTop = maxY;
                }
                if (myceliumTop > myceliumBottom) {
                    chunkData.setRegion(x, myceliumBottom, z, x + 1, myceliumTop, z + 1, Material.MYCELIUM);
                }

                int surfaceHeight = myceliumTop - 1;
                if (surfaceHeight < maxY - 1 && surfaceHeight >= chunkData.getMinHeight()) {
                    double detailValue = detailNoise.GetNoise(x + (chunkX * 16), z + (chunkZ * 16));

                    if (detailValue > 0.9 && chunkRandom.nextDouble() < 0.1) {
                        chunkData.setBlock(x, surfaceHeight + 1, z, Material.RED_MUSHROOM_BLOCK);
                    }
                    else if (detailValue < -0.8 && chunkRandom.nextDouble() < 0.05) {
                        chunkData.setBlock(x, surfaceHeight + 1, z, Material.BROWN_MUSHROOM_BLOCK);
                    }
                }
            }
        }
    }
}
