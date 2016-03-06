package VoxelEngine;

import java.util.BitSet;

public abstract class Chunk<T> {

    public static final short CHUNK_SIZE = 16;

    public static final int GRID_SIZE = 1000;  // in mm, 1m = 1000mm

    protected T chunkmap;

    World world;
    BitSet isVisibleFlags = new BitSet(6);
    Chunk[] neighbors;

    Chunk(World world, TerrainGenerator generator, Chunk[] neighbors) {
        isVisibleFlags.set(0, 6);
        this.world = world;
        this.neighbors = neighbors;
        initializeChunk(generator);
    }

    /**
     * Fill with initial data (according to the generator) and do block updates for all blocks
     *
     * @param generator generator which tells us where to put what kind of block
     */
    abstract void initializeChunk(TerrainGenerator generator);

    /**
     * Optimize Blocks and Chunk visibility
     */
    abstract void optimize();

    abstract void set(short x, short y, short z, Block block);

    abstract Block get(short x, short y, short z);

    abstract Block[][][] getInterval(short x1, short x2, short y1, short y2, short z1, short z2);

    Block[][][] getEntireChunk() {
        return getInterval((short) 0, CHUNK_SIZE, (short) 0, CHUNK_SIZE, (short) 0, CHUNK_SIZE);
    }

    boolean isHidden() {
        return isVisibleFlags.cardinality() == 0;
    }

    abstract public String toString();
}