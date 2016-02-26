package VoxelEngine;

public class World {

    Chunk oneTestChunk;
    TerrainGenerator generator;

    World() {
        generator = new SimplexTerrainGenerator(0L);
        createNewChunk(0, 0, 0);
    }

    public void createNewChunk(int x, int y, int z) {
        // TODO actually do this for the chunked asked for
        oneTestChunk = new IntervalTreeChunk(generator.getGeneratorForChunk(x, y, z));
    }

    Chunk[] getVisibleChunks() {
        return new Chunk[]{oneTestChunk};
    }
}
