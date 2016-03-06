package VoxelEngine;


public class EmptyWorldGenerator extends TerrainGenerator {

    EmptyWorldGenerator() {
        isInitialized = true;
    }

    @Override
    protected Block createBlock(int x, int y, int z) {
        return new Block(Block.BlockType.EMPTY);
    }

    @Override
    public TerrainGenerator getGeneratorForChunk(int x, int y, int z) {
        return new EmptyWorldGenerator();
    }
}
