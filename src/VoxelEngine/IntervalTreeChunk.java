package VoxelEngine;

public class IntervalTreeChunk extends Chunk<IntervalTreeChunk.IntervalTreeNode> {

    static final short CHUNK_VOLUME = Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE;
    private IntervalTreeNode intervalTree;

    IntervalTreeChunk(TerrainGenerator generator) {
        super(generator);
    }

    void initializeWorld(TerrainGenerator generator) {
        intervalTree = new IntervalTreeNode(new Block(Block.EMPTY), (short)0, (short)(CHUNK_VOLUME - 1));
        for (short x = 0; x < CHUNK_SIZE; x++) {
            for (short y = 0; y < CHUNK_SIZE; y++) {
                for (short z = 0; z < CHUNK_SIZE; z++) {
                    set(x, y, z, generator.getBlock(x, y, z));
                }
            }
        }
    }

    void set(int x, int y, int z, Block block) {
        intervalTree.setBlock((byte)x, (byte)y, (byte)z, block);
    }

    Block get(short x, short y, short z) {  //TODO Change type to BYTE (instead of INT)
        return intervalTree.getBlock((byte)x, (byte)y, (byte)z);
    }

    //TODO In welche Richtung die Intervalle durchlaufen?
    //Aktuelle Annahme yzx
    Block[][][] getInterval(short x1, short x2, short y1, short y2, short z1, short z2) {
        Block[][][] interval = new Block[y2-y1][z2-z1][x2-x1];
        short tmp;
        for (short y = 0; y < y2-y1; y++) {
            for (short z = 0; z < z2-z1; z++) {
                tmp = (short) (CHUNK_SIZE*(z* Chunk.CHUNK_SIZE + y));
                Block[] result = intervalTree.getInterval((short)(tmp + x1), (short)(tmp + x2), (short) 0, (short)(x2-x1-1), new Block[x2 - x1]);
                interval[y][z] = result;
            }
        }
        return interval;
    }

    @Deprecated
    void moveDown() {
        //not to be implemented
    }

    @Deprecated
    void showCoordinateBlocks() {
        //not to be implemented
    }

    public String toString() {
        return "";
    }



    class IntervalTreeNode {

        private IntervalTreeNode leftNode;
        private IntervalTreeNode rightNode;
        private short startPoint;
        private short endPoint;
        private Block block;

        IntervalTreeNode(Block overlapping, short start, short length) {
            startPoint = start;
            endPoint = (short)(start + length);
            block = overlapping;
        }

        short getStart() {
            return startPoint;
        }

        short getEnd() {
            return endPoint;
        }

        Block getBlock(short x, short y, short z) {
            short onedimPoint = (short) ((z* Chunk.CHUNK_SIZE + y) * Chunk.CHUNK_SIZE + x);

            if (onedimPoint >= startPoint && onedimPoint <= endPoint)
                return block;
            else if (onedimPoint > endPoint)
                return rightNode.getBlock(x, y, z);
            else // if (onedimPoint < startPoint)
                return leftNode.getBlock(x, y, z);
        }

        private Block[] getInterval (short start, short end, short startArray, short endArray, Block[] interval) {

            if (end < start)
                return interval;

            if (end == start) {
                return interval;
            }

            for (int i = 0; false; i++ /*TODO Create new for-head!*/ ) {break;}

            if (leftNode != null)
                interval = leftNode.getInterval(start, (short)(startPoint-1), startArray, endArray, interval); //TODO Change startArray and endArray

            if (rightNode != null)
                interval = rightNode.getInterval((short)(endPoint+1), end, startArray, endArray, interval); //TODO Change startArray and endArray

            return interval;
        }

        void setBlock(short x, short y, short z, Block block) {
//            this.block = new Block(0);

            int type = block.getType();
            short onedimcoord = (short) (Chunk.CHUNK_SIZE * (z* Chunk.CHUNK_SIZE + y) + x);

            if (onedimcoord < endPoint && onedimcoord > startPoint){
                if (type == this.block.getType()) {
                    return;
                } else {
                    IntervalTreeNode newLeftNode = new IntervalTreeNode(this.block, startPoint, (short)(onedimcoord - startPoint - 1));
                    IntervalTreeNode newRightNode = new IntervalTreeNode(this.block, (short)(onedimcoord + 1), (short)(endPoint - onedimcoord + 1));
                    newLeftNode.leftNode = this.leftNode;
                    newRightNode.rightNode = this.rightNode;
                    this.startPoint = onedimcoord;
                    this.endPoint = onedimcoord;
                    this.block = block;
                    this.leftNode = newLeftNode;
                    this.rightNode = newRightNode;
                }
            } else if (onedimcoord == startPoint) {
                if (type == this.block.getType())
                    return;
                else {
                    IntervalTreeNode newNode = new IntervalTreeNode(this.block, (short)(startPoint + 1), (short)(endPoint - startPoint - 1));
                    newNode.rightNode = this.rightNode;
                    this.endPoint = this.startPoint;
                    this.block = block;
                    this.rightNode = newNode;
                }
            } else if (onedimcoord == endPoint) {
                if (type == this.block.getType())
                    return;
                else {
                    IntervalTreeNode newNode = new IntervalTreeNode(this.block, startPoint, (short)(endPoint - startPoint - 1));
                    newNode.leftNode = this.leftNode;
                    this.startPoint = this.endPoint;
                    this.block = block;
                    this.leftNode = newNode;
                }
            } else if (onedimcoord < startPoint) {
                leftNode.setBlock(x, y, z, block);
            } else {
                rightNode.setBlock(x, y, z, block);
            }
        }
    }
}