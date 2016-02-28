package VoxelEngine;

public class World {

    public static final int VIEW_DISTANCE = 4;

    Chunk[][] chunksInRange = new Chunk[VIEW_DISTANCE * 2 + 1][VIEW_DISTANCE * 2 + 1];
    TerrainGenerator generator;

    World() {
        generator = new SimplexTerrainGenerator(0L);

        // TODO z is always 0, there are no 3d chunks yet

        for(int x = -VIEW_DISTANCE; x <= VIEW_DISTANCE; x++) {
            for(int y = -VIEW_DISTANCE; y <= VIEW_DISTANCE; y++) {
                createNewChunk(x, y, 0);
            }
        }
    }

    public void createNewChunk(int x, int y, int z) {
        Chunk[] neighbors = new Chunk[]{
            getNeighbor(Block.TOP, x, y, z),
            getNeighbor(Block.BACK, x, y, z),
            getNeighbor(Block.RIGHT, x, y, z),
            getNeighbor(Block.FRONT, x, y, z),
            getNeighbor(Block.LEFT, x, y, z),
            getNeighbor(Block.BOTTOM, x, y, z),
        };
        Chunk newChunk = new ArrayChunk(generator.getGeneratorForChunk(x, y, z), neighbors);
        setChunk(x, y, z, newChunk);
    }

    Chunk[][] getVisibleChunks() {
        return chunksInRange;
    }

    Chunk getChunk(int x, int y, int z) {
        Chunk chunk;
        try {
            chunk = chunksInRange[x + VIEW_DISTANCE][y + VIEW_DISTANCE];
        } catch (Exception e) {
            chunk = null;
        }
        return chunk;
    }

    void setChunk(int x, int y, int z, Chunk chunk) {
        chunksInRange[x + VIEW_DISTANCE][y + VIEW_DISTANCE] = chunk;
    }

    Chunk getNeighbor(int side, int x, int y, int z) {
        switch (side) {
            case Block.TOP: return getChunk(x, y, z+1);
            case Block.BACK: return getChunk(x, y+1, z);
            case Block.RIGHT: return getChunk(x+1, y, z);
            case Block.FRONT: return getChunk(x, y-1, z);
            case Block.LEFT: return getChunk(x-1, y, z);
            case Block.BOTTOM: return getChunk(x, y, z-1);
            default: throw new IllegalStateException("This direction / side does not exist");
        }
    }
}
