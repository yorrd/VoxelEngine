package VoxelEngine;

public abstract class Chunk<T> {

    public static final short CHUNK_SIZE = 16;

    public static final int GRID_SIZE = 1000;  // in mm, 1m = 1000mm

    protected T chunkmap;

    Chunk(TerrainGenerator generator) {
        initializeChunk(generator);
    }

    abstract void initializeChunk(TerrainGenerator generator);

    abstract void set(int x, int y, int z, Block block);

    abstract Block get(short x, short y, short z);

    abstract Block[][][] getInterval(short x1, short x2, short y1, short y2, short z1, short z2);

    Block[][][] getEntireChunk() {
        return getInterval((short) 0, CHUNK_SIZE, (short) 0, CHUNK_SIZE, (short) 0, CHUNK_SIZE);
    }

    abstract public String toString();
}