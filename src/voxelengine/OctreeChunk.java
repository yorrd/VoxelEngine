package voxelengine;

// TODO T = OctreeNode, not array
// TODO search
public class OctreeChunk extends Chunk<OctreeChunk.OctreeNode[]> {

    OctreeChunk(World world, TerrainGenerator generator, Chunk[] neighbors) {
        super(world, generator, neighbors);
    }

    @Override
    void initializeChunk(TerrainGenerator generator) {
        chunkmap = new OctreeNode[8];
        // TODO actually listen to the generator
        for(int eight = 0; eight < 8; eight++) {
            chunkmap[eight] = new OctreeNode(new Block(Block.BlockType.EMPTY));
        }
    }

    @Override
    void optimize() {
        // TODO implement
    }

    @Override
    Block get(short x, short y, short z) {
        return search(x, y, z).getBlock();
    }

    @Override
    Block[][][] getInterval(short x1, short x2, short y1, short y2, short z1, short z2) {
        // TODO
        return new Block[0][0][0];
    }

    @Override
    void set(short x, short y, short z, Block block) {
        search(x, y, z).setBlock(block);
    }

    private OctreeNode search(int x, int y, int z) {
        // TODO
        return new OctreeNode(new Block(Block.BlockType.EMPTY));
    }

    void moveDown() {
    }

    void showCoordinateBlocks() {
    }

    public String toString() {
        return "";
    }


    class OctreeNode {

        /*
        Layer 1   Layer 2 (above)
        3 4  -->  7 8
        1 2  -->  5 6
         */

        private Block block;
        private OctreeNode[] children;

        OctreeNode(Block block) {
            setBlock(block);
        }

        OctreeNode(OctreeNode[] children) {
            setChildren(children);
        }

        void setNthChild(int n, OctreeNode node) {
            block = null;
            children[n] = node;
        }

        void setChildren(OctreeNode[] nodes) {
            block = null;
            this.children = nodes;
        }

        void setBlock(Block block) {
            children = null;
            this.block = block;
        }

        Block getBlock() {
            return block;
        }

        OctreeNode getChild(int n) {
            if(children != null)
                return children[n];
            else
                return null;
        }

        boolean hasChildren() {
            if(block == null && children != null)
                return true;
            if(block != null && children == null)
                return false;
            throw new IllegalStateException("Exactly one of block or children has to be none");
        }
    }
}
