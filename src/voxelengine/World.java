package voxelengine;

public class World {

    public static final int VIEW_DISTANCE = 2;

    Chunk[][][] chunksInRange = new Chunk[VIEW_DISTANCE * 2 + 1][VIEW_DISTANCE * 2 + 1][VIEW_DISTANCE * 2 + 1];
    TerrainGenerator generator;

    public World() {
        generator = new SimplexTerrainGenerator(0L);

        for(int x = -VIEW_DISTANCE; x <= VIEW_DISTANCE; x++) {
            for(int y = -VIEW_DISTANCE; y <= VIEW_DISTANCE; y++) {
                for(int z = -VIEW_DISTANCE; z <= VIEW_DISTANCE; z++) {
                    // chunks only in a circle around the center, you can't see the corners anyways
                    if (distanceOnGrid(0, 0, x, y) < VIEW_DISTANCE)
                        createNewChunk(x, y, z);
                }
            }
        }

        // optimize
        for(int x = -VIEW_DISTANCE; x <= VIEW_DISTANCE; x++) {
            for (int y = -VIEW_DISTANCE; y <= VIEW_DISTANCE; y++) {
                for(int z = -VIEW_DISTANCE; z <= VIEW_DISTANCE; z++) {
                    Chunk currentChunk = getChunk(x, y, z);
                    if (currentChunk != null) {
                        currentChunk.optimize();
                        // prevent from rendering
                        if(currentChunk.isHidden())
                            unloadChunk(x, y, z);
                    }
                }
            }
        }
    }

    public void createNewChunk(int x, int y, int z) {
        Chunk topNeighbor = getNeighbor(Block.TOP, x, y, z);
        Chunk backNeighbor = getNeighbor(Block.BACK, x, y, z);
        Chunk rightNeighbor = getNeighbor(Block.RIGHT, x, y, z);
        Chunk frontNeighbor = getNeighbor(Block.FRONT, x, y, z);
        Chunk leftNeighbor = getNeighbor(Block.LEFT, x, y, z);
        Chunk bottomNeighbor = getNeighbor(Block.BOTTOM, x, y, z);
        Chunk[] neighbors = new Chunk[]{
                topNeighbor,
                backNeighbor,
                rightNeighbor,
                frontNeighbor,
                leftNeighbor,
                bottomNeighbor,
        };
        Chunk newChunk = new ArrayChunk(this, generator.getGeneratorForChunk(x, y, z), neighbors);
        if(topNeighbor != null) topNeighbor.neighbors[Block.BOTTOM] = newChunk;
        if(backNeighbor != null) backNeighbor.neighbors[Block.FRONT] = newChunk;
        if(rightNeighbor != null) rightNeighbor.neighbors[Block.LEFT] = newChunk;
        if(frontNeighbor != null) frontNeighbor.neighbors[Block.BACK] = newChunk;
        if(leftNeighbor != null) leftNeighbor.neighbors[Block.RIGHT] = newChunk;
        if(bottomNeighbor != null) bottomNeighbor.neighbors[Block.TOP] = newChunk;
        setChunk(x, y, z, newChunk);
    }

    public Chunk[][][] getVisibleChunks() {
        return chunksInRange;
    }

    Chunk getChunk(int x, int y, int z) {
        return chunksInRange[x + VIEW_DISTANCE][y + VIEW_DISTANCE][z + VIEW_DISTANCE];
    }

    void setChunk(int x, int y, int z, Chunk chunk) {
        chunksInRange[x + VIEW_DISTANCE][y + VIEW_DISTANCE][z + VIEW_DISTANCE] = chunk;
    }

    void unloadChunk(int x, int y, int z) {
        chunksInRange[x + VIEW_DISTANCE][y + VIEW_DISTANCE][z + VIEW_DISTANCE] = null;
    }

    Chunk getNeighbor(int side, int x, int y, int z) {
        try {
            switch (side) {
                case Block.TOP: return getChunk(x, y, z+1);
                case Block.BACK: return getChunk(x, y-1, z);
                case Block.RIGHT: return getChunk(x+1, y, z);
                case Block.FRONT: return getChunk(x, y+1, z);
                case Block.LEFT: return getChunk(x-1, y, z);
                case Block.BOTTOM: return getChunk(x, y, z-1);
                default: throw new IllegalStateException("This direction / side does not exist");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    static double distanceOnGrid(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int min = Math.min(dx, dy);
        int max = Math.max(dx, dy);

        int diagonalSteps = min;
        int straightSteps = max - min;

        return Math.sqrt(2) * diagonalSteps + straightSteps;
    }

    // don't use often, expensive
    Block globalGet(int x, int y, int z) {
        short chunkX = (short) Math.floor(x / Chunk.CHUNK_SIZE);
        short chunkY = (short) Math.floor(y / Chunk.CHUNK_SIZE);
        short chunkZ = (short) Math.floor(z / Chunk.CHUNK_SIZE);
        short blockX = (short) (x % Chunk.CHUNK_SIZE);
        short blockY = (short) (y % Chunk.CHUNK_SIZE);
        short blockZ = (short) (z % Chunk.CHUNK_SIZE);

        Chunk c = getChunk(chunkX, chunkY, chunkZ);
        if(c == null) return new Block(Block.BlockType.DEBUG);
        return c.get(blockX, blockY, blockZ);
    }

    void triggerBlockUpdate(int x, int y, int z) {
        // TODO finish adjusting when we need it
        // TODO if new chunk got visible

//        Chunk surroundingChunk = getChunk(x % Chunk.CHUNK_SIZE, y % Chunk.CHUNK_SIZE, z % Chunk.CHUNK_SIZE);
//        short withinX = (short) (x - x % Chunk.CHUNK_SIZE);
//        short withinY = (short) (y - y % Chunk.CHUNK_SIZE);
//        short withinZ = (short) (z - z % Chunk.CHUNK_SIZE);
//
//        Block changedBlock = surroundingChunk.get(withinX, withinY, withinZ);
//
//        Block top = null;
//        if(z + 1 < CHUNK_SIZE) {
//            top = get(x, y, ((short) (z + 1)));
//        } else if(neighbors[Block.TOP] != null) {
//            top = neighbors[Block.TOP].get(x, y, (short) 0);
//        }
//        if(top != null)
//            top.blockUpdate(Block.BOTTOM, changedBlock);
//        changedBlock.blockUpdate(Block.TOP, top);
//
//        Block front = null;
//        if(y + 1 < CHUNK_SIZE) {
//            front = get(x, ((short) (y + 1)), z);
//        } else if(neighbors[Block.FRONT] != null) {
//            front = neighbors[Block.FRONT].get(x, (short) 0, z);
//        }
//        if(front != null)
//            front.blockUpdate(Block.BACK, changedBlock);
//        changedBlock.blockUpdate(Block.FRONT, front);
//
//        Block right = null;
//        if(x + 1 < CHUNK_SIZE) {
//            right = get(((short) (x + 1)), y, z);
//        } else if(neighbors[Block.RIGHT] != null) {
//            right = neighbors[Block.RIGHT].get((short) 0, y, z);
//        }
//        if(right != null)
//            right.blockUpdate(Block.LEFT, changedBlock);
//        changedBlock.blockUpdate(Block.RIGHT, right);
//
//        Block back = null;
//        if(y - 1 > 0) {
//            back = get(x, ((short) (y - 1)), z);
//        } else if(neighbors[Block.BACK] != null) {
//            back = neighbors[Block.BACK].get(x, (short) (CHUNK_SIZE - 1), z);
//        }
//        if(back != null)
//            back.blockUpdate(Block.FRONT, changedBlock);
//        changedBlock.blockUpdate(Block.BACK, back);
//
//        Block left = null;
//        if(x - 1 > 0) {
//            left = get(((short) (x - 1)), y, z);
//        } else if(neighbors[Block.LEFT] != null) {
//            left = neighbors[Block.LEFT].get(((short) (CHUNK_SIZE - 1)), y, z);
//        }
//        if(left != null)
//            left.blockUpdate(Block.RIGHT, changedBlock);
//        changedBlock.blockUpdate(Block.LEFT, left);
//
//        Block bottom = null;
//        if(z - 1 > 0) {
//            bottom = get(x, y, ((short) (z - 1)));
//        } else if(neighbors[Block.BOTTOM] != null) {
//            bottom = neighbors[Block.BOTTOM].get(x, y, ((short) (CHUNK_SIZE - 1)));
//        }
//        if(bottom != null)
//            bottom.blockUpdate(Block.TOP, changedBlock);
//        changedBlock.blockUpdate(Block.BOTTOM, bottom);
    }

    // TODO move singleton to game object as soon as there is one
    private static World worldInstance;

    public static World getInstance() {
        if(worldInstance == null) {
            worldInstance = new World();
        }
        return worldInstance;
    }
}
