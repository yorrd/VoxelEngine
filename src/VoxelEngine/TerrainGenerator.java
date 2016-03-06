package VoxelEngine;

import sun.plugin.dom.exception.InvalidStateException;

public abstract class TerrainGenerator {

    protected final int chunk_x;
    protected final int chunk_y;
    protected final int chunk_z;
    protected boolean isInitialized = false;

    TerrainGenerator() {
        chunk_x = 0;
        chunk_y = 0;
        chunk_z = 0;
    }

    protected TerrainGenerator(int chunkX, int chunkY, int chunkZ) {
        this.chunk_x = chunkX;
        this.chunk_y = chunkY;
        this.chunk_z = chunkZ;
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
