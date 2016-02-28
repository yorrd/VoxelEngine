package VoxelEngine;

public abstract class Chunk<T> {

    public static final short CHUNK_SIZE = 16;

    public static final int GRID_SIZE = 1000;  // in mm, 1m = 1000mm

    protected T chunkmap;

    Chunk[] neighbors;

    Chunk(TerrainGenerator generator, Chunk[] neighbors) {
        this.neighbors = neighbors;
        initializeChunk(generator);
    }

    /**
     * Fill with initial data (according to the generator) and do block updates for all blocks
     *
     * @param generator generator which tells us where to put what kind of block
     */
    abstract void initializeChunk(TerrainGenerator generator);

    abstract void set(short x, short y, short z, Block block);

    abstract Block get(short x, short y, short z);

    abstract Block[][][] getInterval(short x1, short x2, short y1, short y2, short z1, short z2);

    Block[][][] getEntireChunk() {
        return getInterval((short) 0, CHUNK_SIZE, (short) 0, CHUNK_SIZE, (short) 0, CHUNK_SIZE);
    }

    void triggerBlockUpdate(Chunk chunk, short x, short y, short z) {

        Block changedBlock = chunk.get(x, y, z);

        Block top = null;
        if(z + 1 < Chunk.CHUNK_SIZE) {
            top = chunk.get(x, y, ((short) (z + 1)));
        } else if(neighbors[Block.TOP] != null) {
            top = neighbors[Block.TOP].get(x, y, (short) 0);
        }
        if(top != null)
            top.blockUpdate(Block.BOTTOM, changedBlock);
        changedBlock.blockUpdate(Block.TOP, top);

        Block back = null;
        if(y + 1 < Chunk.CHUNK_SIZE) {
            back = chunk.get(x, ((short) (y + 1)), z);
        } else if(neighbors[Block.BACK] != null) {
            back = neighbors[Block.BACK].get(x, (short) 0, z);
        }
        if(back != null)
            back.blockUpdate(Block.FRONT, changedBlock);
        changedBlock.blockUpdate(Block.BACK, back);

        Block right = null;
        if(x + 1 < Chunk.CHUNK_SIZE) {
            right = chunk.get(((short) (x + 1)), y, z);
        } else if(neighbors[Block.RIGHT] != null) {
            right = neighbors[Block.RIGHT].get((short) 0, y, z);
        }
        if(right != null)
            right.blockUpdate(Block.LEFT, changedBlock);
        changedBlock.blockUpdate(Block.RIGHT, right);

        Block front = null;
        if(y - 1 > 0) {
            front = chunk.get(x, ((short) (y - 1)), z);
        } else if(neighbors[Block.FRONT] != null) {
            front = neighbors[Block.FRONT].get(x, ((short) (CHUNK_SIZE - 1)), z);
        }
        if(front != null)
            front.blockUpdate(Block.BACK, changedBlock);
        changedBlock.blockUpdate(Block.FRONT, front);

        Block left = null;
        if(x - 1 > 0) {
            left = chunk.get(((short) (x - 1)), y, z);
        } else if(neighbors[Block.LEFT] != null) {
            left = neighbors[Block.LEFT].get(((short) (CHUNK_SIZE - 1)), y, z);
        }
        if(left != null)
            left.blockUpdate(Block.RIGHT, changedBlock);
        changedBlock.blockUpdate(Block.LEFT, left);

        Block bottom = null;
        if(z - 1 > 0) {
            bottom = chunk.get(x, y, ((short) (z - 1)));
        } else if(neighbors[Block.BOTTOM] != null) {
            bottom = neighbors[Block.BOTTOM].get(x, y, ((short) (CHUNK_SIZE - 1)));
        }
        if(bottom != null)
            bottom.blockUpdate(Block.TOP, changedBlock);
        changedBlock.blockUpdate(Block.BOTTOM, bottom);
    }

    abstract public String toString();
}