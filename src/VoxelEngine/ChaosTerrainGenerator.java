package VoxelEngine;

public class ChaosTerrainGenerator extends TerrainGenerator {

    @Override
    protected Block createBlock(int x, int y, int z) {
        return new Block(Block.BlockType.getRandom());
    }

    @Override
    public TerrainGenerator getGeneratorForChunk(int x, int y, int z) {
        return null;
    }
}
