package VoxelEngine;

public class SimplexTerrainGenerator extends TerrainGenerator {

    protected final OpenSimplexNoise noise;
    protected final long seed;

    SimplexTerrainGenerator(long seed) {
        super();
        this.seed = seed;
        noise = new OpenSimplexNoise(seed);
    }

    protected SimplexTerrainGenerator(long seed, int offset_x, int offset_y, int offset_z) {
        super(offset_x, offset_y, offset_z);
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

        int material = Block.EMPTY;

        if(noise.eval(x, y) * (Chunk.CHUNK_SIZE - (min + max)) + min > z)
            material = 0;
        else if(noise.eval(x, y) * (Chunk.CHUNK_SIZE - (min + max)) + min + layerHeight > z)
            material = 1;

        return new Block(material);
    }

    @Override
    public TerrainGenerator getGeneratorForChunk(int x, int y, int z) {
        return new SimplexTerrainGenerator(seed, x * Chunk.CHUNK_SIZE, y * Chunk.CHUNK_SIZE, z * Chunk.CHUNK_SIZE);
    }
}
