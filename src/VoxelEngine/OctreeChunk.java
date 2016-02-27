package VoxelEngine;

// TODO T = OctreeNode, not array
// TODO search
public class OctreeChunk extends Chunk<OctreeChunk.OctreeNode[]> {

    OctreeChunk(TerrainGenerator generator) {
        super(generator);
    }

    @Override
    void initializeChunk(TerrainGenerator generator) {
        chunkmap = new OctreeNode[8];
        // TODO actually listen to the generator
        for(int eight = 0; eight < 8; eight++) {
            chunkmap[eight] = new OctreeNode(new Block(Block.EMPTY));
        }
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
    void set(int x, int y, int z, Block block) {
        search(x, y, z).setBlock(block);
    }

    private OctreeNode search(int x, int y, int z) {
//        // norm back to indices
//        x += MAX_X;
//        y += MAX_Y;
//        z += MAX_Z;
//
//        // initialize values
//        OctreeNode currentNode = new OctreeNode(chunkmap);
//        int halfX = MAX_X * 2 + 1;
//        int halfY = MAX_Y * 2 + 1;
//        int halfZ = MAX_Z * 2 + 1;
//
//        // until we have found the end point where there are no more children
//        while(currentNode.hasChildren()) {
//            // part the possible space in halves in all directions (rounding up)
//            halfX = (int) Math.ceil(halfX / 2);
//            halfY = (int) Math.ceil(halfY / 2);
//            halfZ = (int) Math.ceil(halfZ / 2);
//
//            // set vars to true if they are within the first interval
//            boolean xRoute = x <= halfX;
//            boolean yRoute = y <= halfY;
//            boolean zRoute = z <= halfZ;
//
//            // evaluate which one we need
//            int n;
//            if(xRoute)
//                if(yRoute)
//                    if(zRoute)
//                        n = 1;
//                    else
//                        n = 5;
//                else
//                    if(zRoute)
//                        n = 3;
//                    else
//                        n = 7;
//            else
//                if(yRoute)
//                    if(zRoute)
//                        n = 2;
//                    else
//                        n = 6;
//                else
//                    if(zRoute)
//                        n = 4;
//                    else
//                        n = 8;
//
//            // make n an index
//            n -= 1;
//
//            if(n == -1)
//                throw new IllegalStateException("The index can never be -1 here.");
//
//            currentNode = currentNode.getChild(n);
//        }
//
//        return currentNode;
        return new OctreeNode(new Block(Block.EMPTY));
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
