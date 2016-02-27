package VoxelEngine;

public class ArrayChunk extends Chunk<Block[][][]> {

    ArrayChunk(TerrainGenerator generator) {
        super(generator);
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

        // TODO make this more efficient
        for (short x = 0; x < CHUNK_SIZE; x++) {
            for (short y = 0; y < CHUNK_SIZE; y++) {
                for (short z = 0; z < CHUNK_SIZE; z++) {
                    triggerBlockUpdate(x, y, z);
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
