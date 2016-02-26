package VoxelEngine;

import java.util.Arrays;

public class ArrayChunk extends Chunk<Block[][][]> {

    ArrayChunk(TerrainGenerator generator) {
        super(generator);
    }

    @Override
    void initializeWorld(TerrainGenerator generator) {
        chunkmap = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for(int x = 0; x < CHUNK_SIZE; x++)
            for(int y = 0; y < CHUNK_SIZE; y++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    set(x, y, z, generator.getBlock(x, y, z));
                }
            }
    }

    @Override
    void set(int x, int y, int z, Block block) {
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

    Block[] get(int x, int y) {
        return chunkmap[x][y];
    }

    void moveDown() {
        for(int x = 0; x < CHUNK_SIZE; x++)
            for(int y = 0; y < CHUNK_SIZE; y++) {
                Block[] column = get(x, y);
                Arrays.sort(column, (o1, o2) -> {
                    if(o1.isEmtpy())
                        return o2.isEmtpy() ? 0 : 1;
                    else
                        return o2.isEmtpy() ? 0 : -1;
                });
            }
    }

    void showCoordinateBlocks() {
        for(int x = 0; x < CHUNK_SIZE; x++)
            for(int y = 0; y < CHUNK_SIZE; y++) {
                for(int z = 0; z < CHUNK_SIZE; z++) {
                    if(x == 0 || y == 0 || z == 0)
                        set(x, y, z, new Block(2));
                }
            }
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
