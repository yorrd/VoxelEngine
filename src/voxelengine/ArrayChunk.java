package voxelengine;

class ArrayChunk extends Chunk<Block[][][]> {

    ArrayChunk(World world, TerrainGenerator generator, Chunk[] neighbors) {
        super(world, generator, neighbors);
    }

    @Override
    void initializeChunk(TerrainGenerator generator) {
        chunkmap = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for (short x = 0; x < CHUNK_SIZE; x++) {
            for (short y = 0; y < CHUNK_SIZE; y++) {
                for (short z = 0; z < CHUNK_SIZE; z++) {
                    set(x, y, z, generator.getBlock(x, y, z));
                }
            }
        }
    }

    @Override
    void optimize() {
        // optimize blocks perimeters
        for (short x = 0; x < CHUNK_SIZE; x++) {
            for (short y = 0; y < CHUNK_SIZE; y++) {
                for (short z = 0; z < CHUNK_SIZE; z++) {
                    Block current = get(x, y, z);
                    if(!isOnOutside(x, y, z)) {
                        // center is easy
                        current.setNeighbor(Block.TOP, get(x, y, (short) (z+1)));
                        current.setNeighbor(Block.BACK, get(x, (short) (y-1), z));
                        current.setNeighbor(Block.RIGHT, get((short) (x+1), y, z));
                        current.setNeighbor(Block.FRONT, get(x, (short) (y+1), z));
                        current.setNeighbor(Block.LEFT, get((short) (x-1), y, z));
                        current.setNeighbor(Block.BOTTOM, get(x, y, (short) (z-1)));
                    } else {
                        // on the edges, we have to decide whether to go in the next chunk
                        if(z+1 < CHUNK_SIZE)
                            current.setNeighbor(Block.TOP, get(x, y, (short) (z+1)));
                        else
                            current.setNeighbor(Block.TOP, neighbors[Block.TOP] != null ? neighbors[Block.TOP].get(x, y, (short) 0) : null);

                        if(y-1 >= 0)
                            current.setNeighbor(Block.BACK, get(x, (short) (y-1), z));
                        else
                            current.setNeighbor(Block.BACK, neighbors[Block.BACK] != null ? neighbors[Block.BACK].get(x, (short) (CHUNK_SIZE-1), z) : null);

                        if(x+1 < CHUNK_SIZE)
                            current.setNeighbor(Block.RIGHT, get((short) (x+1), y, z));
                        else
                            current.setNeighbor(Block.RIGHT, neighbors[Block.RIGHT] != null ? neighbors[Block.RIGHT].get((short) 0, y, z) : null);

                        if(y+1 < CHUNK_SIZE)
                            current.setNeighbor(Block.FRONT, get(x, (short) (y+1), z));
                        else
                            current.setNeighbor(Block.FRONT, neighbors[Block.FRONT] != null ? neighbors[Block.FRONT].get(x, (short) 0, z) : null);

                        if(x-1 >= 0)
                            current.setNeighbor(Block.LEFT, get((short) (x-1), y, z));
                        else
                            current.setNeighbor(Block.LEFT, neighbors[Block.LEFT] != null ? neighbors[Block.LEFT].get((short) (CHUNK_SIZE-1), y, z) : null);

                        if(z-1 >= 0)
                            current.setNeighbor(Block.BOTTOM, get(x, y, (short) (z-1)));
                        else
                            current.setNeighbor(Block.BOTTOM, neighbors[Block.BOTTOM] != null ? neighbors[Block.BOTTOM].get(x, y, (short) (CHUNK_SIZE-1)) : null);
                    }
                }
            }
        }

        isVisibleFlags.clear();
        // optimize chunk perimeter
        for (short x = 0; x < CHUNK_SIZE; x++) {
            for (short y = 0; y < CHUNK_SIZE; y++) {
                for (short z = 0; z < CHUNK_SIZE; z++) {
                    // primitive solution, chunk will be rendered as soon as one block on perimeter isn't COMPLETELY hidden
                    if(isOnOutside(x, y, z) && !get(x, y, z).isHidden())
                        isVisibleFlags.set(0, 6);
                }
            }
        }
    }

    @Override
    void set(short x, short y, short z, Block block) {
        chunkmap[x][y][z] = block;
    }

    @Override
    Block get(short x, short y, short z) {
        return chunkmap[x][y][z];
    }

    @Override
    Block[][][] getInterval(short x1, short x2, short y1, short y2, short z1, short z2) {
        Block[][][] returnArray = new Block[x2-x1][y2-y1][z2-z1];

        for(short x = x1; x < x2; x++) {
            for(short y = y1; y < y2; y++) {
                for(short z = z1; z < z2; z++) {
                    returnArray[x-x1][y-y1][z-z1] = get(x, y, z);
                }
            }
        }
        return returnArray;
    }

    private boolean isOnOutside(short x, short y, short z) {
        return (x == 0 || y == 0 || z == 0 || x == CHUNK_SIZE - 1 || y == CHUNK_SIZE - 1 || z == CHUNK_SIZE - 1);
    }

    public String toString() {
        String output = "";
        for(short x = 0; x < CHUNK_SIZE; x++)
            for(short y = 0; y < CHUNK_SIZE; y++) {
                for(short z = 0; z < CHUNK_SIZE; z++) {
                    Block c = get(x, y, z);
                    output += c.toString() + ", ";
                }
                output += "\n";
            }
        return output;
    }
}
