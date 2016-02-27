package VoxelEngine;

public abstract class Chunk<T> {

    public static final short CHUNK_SIZE = 16;

    public static final int GRID_SIZE = 1000;  // in mm, 1m = 1000mm

    protected T chunkmap;

    Chunk(TerrainGenerator generator) {
        initializeChunk(generator);
    }

    abstract void initializeChunk(TerrainGenerator generator);

    abstract void set(short x, short y, short z, Block block);

    abstract Block get(short x, short y, short z);

    abstract Block[][][] getInterval(short x1, short x2, short y1, short y2, short z1, short z2);

    Block[][][] getEntireChunk() {
        return getInterval((short) 0, CHUNK_SIZE, (short) 0, CHUNK_SIZE, (short) 0, CHUNK_SIZE);
    }

    void triggerBlockUpdate(short x, short y, short z) {

        Block changedBlock = get(x, y, z);

        if(z + 1 < CHUNK_SIZE) {
            Block top = get(x, y, ((short) (z + 1)));
            if (top != null)
                top.blockUpdate(Block.BOTTOM, changedBlock);
        } else {
            // TODO chunk edge
        }

        if(y + 1 < CHUNK_SIZE) {
            Block back = get(x, ((short) (y + 1)), z);
            if (back != null)
                back.blockUpdate(Block.FRONT, changedBlock);
        } else {
            // TODO chunk edge
        }

        if(x + 1 < CHUNK_SIZE) {
            Block right = get(((short) (x + 1)), y, z);
            if (right != null)
                right.blockUpdate(Block.LEFT, changedBlock);
        } else {
            // TODO chunk edge
        }

        if(y - 1 > 0) {
            Block front = get(x, ((short) (y - 1)), z);
            if (front != null)
                front.blockUpdate(Block.BACK, changedBlock);
        } else {
            // TODO chunk edge
        }

        if(x - 1 > 0) {
            Block left = get(((short) (x - 1)), y, z);
            if (left != null)
                left.blockUpdate(Block.RIGHT, changedBlock);
        } else {
            // TODO chunk edge
        }

        if(z - 1 > 0) {
            Block bottom = get(x, y, ((short) (z - 1)));
            if (bottom != null)
                bottom.blockUpdate(Block.TOP, changedBlock);
        } else {
            // TODO chunk edge
        }
    }

    abstract public String toString();
}