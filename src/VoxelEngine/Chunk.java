package VoxelEngine;

public abstract class Chunk<T> {

    public static final short CHUNK_SIZE = 16;

    public static final int GRID_SIZE = 1000;  // in mm, 1m = 1000mm

    protected T chunkmap;

    Chunk(TerrainGenerator generator) {
        initializeWorld(generator);
    }

    abstract void initializeWorld(TerrainGenerator generator);

    abstract void set(int x, int y, int z, Block block);

    abstract Block get(short x, short y, short z);

    abstract Block[][][] getInterval(short x1, short x2, short y1, short y2, short z1, short z2);

    Block[][][] getEntireChunk() {
        return getInterval((short) 0, CHUNK_SIZE, (short) 0, CHUNK_SIZE, (short) 0, CHUNK_SIZE);
    }

    long accessRandomBlock(long measurementOverhead) {
        short x = (short) (Math.random() * Chunk.CHUNK_SIZE);
        short y = (short) (Math.random() * Chunk.CHUNK_SIZE);
        short z = (short) (Math.random() * Chunk.CHUNK_SIZE);

        long startTime = System.nanoTime();
        get(x, y, z);
        long endTime = System.nanoTime();

        return endTime - startTime - measurementOverhead;
    }

    long accessMooreBlocks(long measurementOverhead) {

        short offset = 3;

        short x = (short) (Math.random() * (Chunk.CHUNK_SIZE - 2 * offset) + offset);
        short y = (short) (Math.random() * (Chunk.CHUNK_SIZE - 2 * offset) + offset);
        short z = (short) (Math.random() * (Chunk.CHUNK_SIZE - 2 * offset) + offset);

        short x1 = (short) (x - offset);
        short x2 = (short) (x + offset);
        short y1 = (short) (y - offset);
        short y2 = (short) (y + offset);
        short z1 = (short) (z - offset);
        short z2 = (short) (z + offset);

        // checking Moore environment of r = 3
        long startTime = System.nanoTime();
        getInterval(x1, x2, y1, y2, z1, z2);
        long endTime = System.nanoTime();

        // returning time per block
        return (endTime - startTime - measurementOverhead) / (short) ((Math.pow(offset * 2 + 1, 3)));
    }

    abstract void moveDown();
    abstract void showCoordinateBlocks();

    abstract public String toString();
}