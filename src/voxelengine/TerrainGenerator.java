package voxelengine;

import sun.plugin.dom.exception.InvalidStateException;

public abstract class TerrainGenerator {

    final int chunkX;
    final int chunkY;
    final int chunkZ;
    boolean isInitialized = false;

    TerrainGenerator() {
        chunkX = 0;
        chunkY = 0;
        chunkZ = 0;
    }

    protected TerrainGenerator(int chunkX, int chunkY, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.chunkZ = chunkZ;
        this.isInitialized = true;
    }

    final Block getBlock(int x, int y, int z) {
        if(!isInitialized)
            throw new InvalidStateException("Generator hasn't been initialized for a chunk.");
        else
            return createBlock(x, y, z);
    }

    protected abstract Block createBlock(int x, int y, int z);

    public abstract TerrainGenerator getGeneratorForChunk(int x, int y, int z);
}
