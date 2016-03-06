package VoxelEngine;

public class SimplexTerrainGenerator extends TerrainGenerator {

    protected final OpenSimplexNoise noise;
    protected final long seed;

    SimplexTerrainGenerator(long seed) {
        super();
        this.seed = seed;
        noise = new OpenSimplexNoise(seed);
    }

    protected SimplexTerrainGenerator(long seed, int offsetX, int offsetY, int offsetZ) {
        super(offsetX, offsetY, offsetZ);
        this.seed = seed;
        noise = new OpenSimplexNoise(seed);
    }

    @Override
    protected Block createBlock(int x, int y, int z) {
        x += chunk_x;
        y += chunk_y;
        z += chunk_z;

        int min = 4;
        int max = 10;
        int layerHeight = 2;

        Block.BlockType material = Block.BlockType.EMPTY;

        if(noise.eval(x, y) * (Chunk.CHUNK_SIZE - (min + max)) + min > z)
            material = Block.BlockType.STONE;
        else if(noise.eval(x, y) * (Chunk.CHUNK_SIZE - (min + max)) + min + layerHeight > z)
            material = Block.BlockType.GRASS;

        return new Block(material);
    }

    @Override
    public TerrainGenerator getGeneratorForChunk(int x, int y, int z) {
        return new SimplexTerrainGenerator(seed, x * Chunk.CHUNK_SIZE, y * Chunk.CHUNK_SIZE, z * Chunk.CHUNK_SIZE);
    }
}
