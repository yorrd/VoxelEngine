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

    protected TerrainGenerator(int chunk_x, int chunk_y, int chunk_z) {
        this.chunk_x = chunk_x;
        this.chunk_y = chunk_y;
        this.chunk_z = chunk_z;
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
