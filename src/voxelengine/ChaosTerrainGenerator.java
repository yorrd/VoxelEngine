package voxelengine;

public class ChaosTerrainGenerator extends TerrainGenerator {

    protected ChaosTerrainGenerator(int offset_x, int offset_y, int offset_z) {
        super(offset_x, offset_y, offset_z);
    }

    @Override
    protected Block createBlock(int x, int y, int z) {
        if(z<4 && x+y != 0)//if(x<4 && z<4 && y<4 && !(y==2 && x==2))
            return new Block(Block.BlockType.STONE);
//        else if(y==2&&x==2&&z==3)
//            return new Block(Block.BlockType.DEBUG);
        else
            return new Block(Block.BlockType.EMPTY);
    }

    @Override
    public TerrainGenerator getGeneratorForChunk(int x, int y, int z) {
        return new ChaosTerrainGenerator(x * Chunk.CHUNK_SIZE, y * Chunk.CHUNK_SIZE, z * Chunk.CHUNK_SIZE);
    }
}
