package VoxelEngine;

public class World {

    // TODO make centered, not absolute
    public static final int VIEW_DISTANCE = 8;

    Chunk[][] chunksInRange = new Chunk[VIEW_DISTANCE][VIEW_DISTANCE];
    TerrainGenerator generator;

    World() {
        generator = new SimplexTerrainGenerator(0L);

        // TODO z is always 0, there are no 3d chunks yet

        for(int x = 0; x < VIEW_DISTANCE; x++) {
            for(int y = 0; y < VIEW_DISTANCE; y++) {
                createNewChunk(x, y, 0);
            }
        }
    }

    public void createNewChunk(int x, int y, int z) {
        chunksInRange[x][y] = new ArrayChunk(generator.getGeneratorForChunk(x, y, z));
    }

    Chunk[][] getVisibleChunks() {
        return chunksInRange;
    }

    Chunk getChunk(int x, int y, int z) {
        return chunksInRange[x][y];
    }
}
